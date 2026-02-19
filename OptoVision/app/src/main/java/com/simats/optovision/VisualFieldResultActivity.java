package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;
import com.simats.optovision.utils.TestCompletionManager;

import org.json.JSONObject;

public class VisualFieldResultActivity extends AppCompatActivity {

    private static final String TAG = "VisualFieldResult";

    private TextView tvRightEyeScore, tvLeftEyeScore, tvAssessment;
    private TextView btnBackToHome, tvBack;

    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    private TestCompletionManager testManager;
    private SessionManager sessionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_field_result);

        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);
        int profileId = sessionManager.getSelectedProfileId();
        testManager = new TestCompletionManager(this, profileId);

        // Get scores from intent
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);
        leftEyeScore = getIntent().getIntExtra("leftEyeScore", 0);

        // Mark test as complete and save scores locally
        testManager.setVisualFieldCompleted(true);
        testManager.setVisualFieldScores(rightEyeScore, leftEyeScore);

        // Save to database
        saveToDatabase();

        initViews();
        displayResults();
        setupClickListeners();

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToTests();
            }
        });
    }

    private void saveToDatabase() {
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId <= 0) {
            Log.w(TAG, "No profile selected, skipping database save");
            return;
        }

        // Calculate percentage scores (5 correct out of 5 = 100%)
        double rightPercent = (rightEyeScore / 5.0) * 100;
        double leftPercent = (leftEyeScore / 5.0) * 100;

        // Determine status based on scores
        String status = "normal";
        double avgScore = (rightPercent + leftPercent) / 2;
        if (avgScore < 50) {
            status = "critical";
        } else if (avgScore < 70) {
            status = "attention";
        }

        // Test ID 6 = Visual Field Test
        apiManager.saveTestResult(profileId, 6, rightPercent, leftPercent, status,
                new ApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG, "Test result saved to database: " + response.toString());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Failed to save test result: " + errorMessage);
                    }
                });
    }

    private void initViews() {
        tvRightEyeScore = findViewById(R.id.tvRightEyeScore);
        tvLeftEyeScore = findViewById(R.id.tvLeftEyeScore);
        tvAssessment = findViewById(R.id.tvAssessment);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        tvBack = findViewById(R.id.tvBack);
    }

    private void displayResults() {
        tvRightEyeScore.setText(rightEyeScore + "/5");
        tvLeftEyeScore.setText(leftEyeScore + "/5");

        // Update score colors based on performance
        updateScoreColor(tvRightEyeScore, rightEyeScore);
        updateScoreColor(tvLeftEyeScore, leftEyeScore);

        // Generate assessment
        int totalScore = rightEyeScore + leftEyeScore;
        String assessment;

        if (totalScore >= 9) {
            assessment = "Excellent! Your peripheral vision is strong. You successfully detected dots in all visual field regions.";
        } else if (totalScore >= 7) {
            assessment = "Good! Your peripheral vision is mostly intact. You detected most dots appearing in your side vision.";
        } else if (totalScore >= 5) {
            assessment = "Fair. You may have some blind spots in your peripheral vision. Consider consulting an eye care professional.";
        } else {
            assessment = "We recommend consulting an eye care professional for a comprehensive visual field examination.";
        }

        tvAssessment.setText(assessment);
    }

    private void updateScoreColor(TextView textView, int score) {
        if (score >= 4) {
            textView.setTextColor(0xFF10B981); // Green
        } else if (score >= 3) {
            textView.setTextColor(0xFF03A9F4); // Cyan
        } else {
            textView.setTextColor(0xFFEF4444); // Red
        }
    }

    private void setupClickListeners() {
        if (tvBack != null) {
            tvBack.setOnClickListener(v -> navigateToTests());
        }

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> navigateToTests());
        }
    }

    private void navigateToTests() {
        // Since Visual Field is the last test (6th), navigate to games recommendation
        Intent intent = new Intent(this, CompleteGamesActivity.class);
        startActivity(intent);
        finish();
    }
}
