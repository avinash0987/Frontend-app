package com.simats.optovision;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class PreviousResultsActivity extends AppCompatActivity {

    private static final String TAG = "PreviousResults";

    private SessionManager sessionManager;
    private ApiManager apiManager;

    private TextView tvProfileName, tvTestDate, tvOverallStatus, tvAverageScore;
    private LinearLayout resultsContainer, testResultsList;
    private View emptyState;
    private ProgressBar loadingIndicator;
    private ImageView btnBack;

    // Test names and icons
    private static final String[] TEST_NAMES = {
            "Distance Vision Test",
            "Near Vision Test",
            "Color Vision Test",
            "Astigmatism Test",
            "Contrast Sensitivity Test",
            "Visual Field Test"
    };

    private static final String[] TEST_ICONS = {
            "👁️", "👓", "🌈", "◐", "◐", "🔍"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_results);

        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);

        initViews();
        setupClickListeners();
        loadPreviousResults();
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvTestDate = findViewById(R.id.tvTestDate);
        tvOverallStatus = findViewById(R.id.tvOverallStatus);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        resultsContainer = findViewById(R.id.resultsContainer);
        testResultsList = findViewById(R.id.testResultsList);
        emptyState = findViewById(R.id.emptyState);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        btnBack = findViewById(R.id.btnBack);

        // Set profile name
        String profileName = sessionManager.getSelectedProfileName();
        tvProfileName.setText(profileName != null && !profileName.isEmpty() ? profileName : "Profile");
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadPreviousResults() {
        int profileId = sessionManager.getSelectedProfileId();

        if (profileId <= 0) {
            showEmptyState();
            return;
        }

        showLoading();

        apiManager.getTestResults(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    hideLoading();

                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                JSONArray results = data.optJSONArray("results");
                                if (results != null && results.length() > 0) {
                                    displayResults(results);
                                    return;
                                }
                            }
                        }
                        showEmptyState();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing results", e);
                        showEmptyState();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load results: " + errorMessage);
                runOnUiThread(() -> {
                    hideLoading();
                    showEmptyState();
                });
            }
        });
    }

    private void displayResults(JSONArray results) {
        emptyState.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.VISIBLE);
        testResultsList.removeAllViews();

        double totalScore = 0;
        int testCount = 0;
        String worstStatus = "normal";
        String latestDate = "";

        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject result = results.getJSONObject(i);

                int testId = result.optInt("test_id", 0);
                double rightScore = result.optDouble("right_eye_score", 0);
                double leftScore = result.optDouble("left_eye_score", 0);
                // Get status from right_status or left_status (API returns separate status per
                // eye)
                String rightStatus = result.optString("right_status", "normal");
                String leftStatus = result.optString("left_status", "normal");
                // Use the worse status
                String status = "normal";
                if (rightStatus.equals("critical") || leftStatus.equals("critical")) {
                    status = "critical";
                } else if (rightStatus.equals("attention") || leftStatus.equals("attention")) {
                    status = "attention";
                }
                String testDate = result.optString("session_date", "");

                // Calculate averages
                double avgScore = (rightScore + leftScore) / 2;
                totalScore += avgScore;
                testCount++;

                // Track worst status
                if (status.equals("critical")) {
                    worstStatus = "critical";
                } else if (status.equals("attention") && !worstStatus.equals("critical")) {
                    worstStatus = "attention";
                }

                // Track latest date
                if (testDate.compareTo(latestDate) > 0) {
                    latestDate = testDate;
                }

                // Add result item
                addTestResultItem(testId, rightScore, leftScore, status);

            } catch (Exception e) {
                Log.e(TAG, "Error parsing result item", e);
            }
        }

        // Update summary
        if (testCount > 0) {
            int avgPercent = (int) (totalScore / testCount);
            tvAverageScore.setText("Average Score: " + avgPercent + "%");
        }

        // Update overall status
        tvOverallStatus.setText(capitalizeFirst(worstStatus));
        int statusColor = getStatusColor(worstStatus);
        tvOverallStatus.setTextColor(statusColor);

        // Update test date
        if (!latestDate.isEmpty()) {
            tvTestDate.setText("Last tested: " + latestDate);
        }
    }

    private void addTestResultItem(int testId, double rightScore, double leftScore, String status) {
        if (testId < 1 || testId > 6)
            return;

        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_test_result, testResultsList, false);

        TextView tvTestIcon = itemView.findViewById(R.id.tvTestIcon);
        TextView tvTestName = itemView.findViewById(R.id.tvTestName);
        TextView tvTestStatus = itemView.findViewById(R.id.tvTestStatus);
        TextView tvRightScore = itemView.findViewById(R.id.tvRightScore);
        TextView tvLeftScore = itemView.findViewById(R.id.tvLeftScore);

        // Set test info
        tvTestIcon.setText(TEST_ICONS[testId - 1]);
        tvTestName.setText(TEST_NAMES[testId - 1]);
        tvTestStatus.setText(capitalizeFirst(status));
        tvTestStatus.setTextColor(getStatusColor(status));

        // Set scores
        tvRightScore.setText((int) rightScore + "%");
        tvLeftScore.setText((int) leftScore + "%");

        // Color scores based on value
        tvRightScore.setTextColor(getScoreColor(rightScore));
        tvLeftScore.setTextColor(getScoreColor(leftScore));

        testResultsList.addView(itemView);
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty())
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "critical":
                return 0xFFEF4444; // Red
            case "attention":
                return 0xFFFF9800; // Orange
            default:
                return 0xFF10B981; // Green
        }
    }

    private int getScoreColor(double score) {
        if (score >= 80) {
            return 0xFF10B981; // Green
        } else if (score >= 60) {
            return 0xFFFF9800; // Orange
        } else {
            return 0xFFEF4444; // Red
        }
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.GONE);
    }
}
