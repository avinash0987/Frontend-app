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

public class AstigmatismResultActivity extends AppCompatActivity {

    private static final String TAG = "AstigmatismResult";

    private TextView tvRightEyeScore, tvLeftEyeScore, tvAssessment;
    private TextView btnContinue, btnBackToHome, tvBack;

    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    private TestCompletionManager testManager;
    private SessionManager sessionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astigmatism_result);

        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);
        int profileId = sessionManager.getSelectedProfileId();
        testManager = new TestCompletionManager(this, profileId);

        // Get scores from intent
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);
        leftEyeScore = getIntent().getIntExtra("leftEyeScore", 0);

        // Mark test as complete and save scores locally
        testManager.setAstigmatismCompleted(true);
        testManager.setAstigmatismScores(rightEyeScore, leftEyeScore);

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

        // Test ID 4 = Astigmatism Test
        apiManager.saveTestResult(profileId, 4, rightPercent, leftPercent, status,
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
        btnContinue = findViewById(R.id.btnContinue);
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
            assessment = "Excellent! Your clock reading ability is strong, indicating good visual acuity with minimal or no astigmatism.";
        } else if (totalScore >= 7) {
            assessment = "Good! You can read clocks well. Minor difficulty may suggest mild visual variation.";
        } else if (totalScore >= 5) {
            assessment = "Fair. Some difficulty reading clock faces may indicate possible astigmatism. Consider an eye exam.";
        } else {
            assessment = "We recommend consulting an eye care professional for a comprehensive astigmatism evaluation.";
        }

        tvAssessment.setText(assessment);
    }

    private void updateScoreColor(TextView textView, int score) {
        if (score >= 4) {
            textView.setTextColor(0xFF10B981); // Green
        } else if (score >= 3) {
            textView.setTextColor(0xFFF59E0B); // Yellow/Orange
        } else {
            textView.setTextColor(0xFFEF4444); // Red
        }
    }

    private void setupClickListeners() {
        if (tvBack != null) {
            tvBack.setOnClickListener(v -> navigateToTests());
        }

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                // Navigate to Contrast Sensitivity Test
                Intent intent = new Intent(this, ContrastTestActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> navigateToTests());
        }
    }

    private void navigateToTests() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
