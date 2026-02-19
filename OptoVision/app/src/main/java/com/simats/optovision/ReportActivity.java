package com.simats.optovision;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ReportActivity displays comprehensive eye health report with dynamic data
 * from database.
 * Uses Excel-based classification for risk assessment and nutrition
 * recommendations.
 */
public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "ReportActivity";

    private SessionManager sessionManager;
    private ApiManager apiManager;

    // Views matching existing layout IDs
    private TextView tvProfileName, tvProfileAge, tvRightBaseline, tvLeftBaseline;
    private LinearLayout testResultsContainer, gameResultsContainer;
    private TextView tvRightEyeOverall, tvLeftEyeOverall, tvGameOverall;
    private TextView tvOverallScore, tvVisionStatus;
    private LinearLayout nutritionContainer;
    private TextView tvNutritionRecommendations, tvEyeCareInstructions;
    private CardView assessmentCard;
    private TextView tvRiskLevel;
    private Button btnSaveToHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initManagers();
        initViews();

        // Check if loading existing report or generating new
        int reportId = getIntent().getIntExtra("report_id", 0);
        if (reportId > 0) {
            loadExistingReport(reportId);
        } else {
            generateNewReport();
        }
    }

    private void initManagers() {
        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);
    }

    private void initViews() {
        // Profile views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileAge = findViewById(R.id.tvProfileAge);
        tvRightBaseline = findViewById(R.id.tvRightBaseline);
        tvLeftBaseline = findViewById(R.id.tvLeftBaseline);

        // Containers
        testResultsContainer = findViewById(R.id.testResultsContainer);
        gameResultsContainer = findViewById(R.id.gameResultsContainer);

        // Scores
        tvRightEyeOverall = findViewById(R.id.tvRightEyeOverall);
        tvLeftEyeOverall = findViewById(R.id.tvLeftEyeOverall);
        tvGameOverall = findViewById(R.id.tvGameOverall);
        tvOverallScore = findViewById(R.id.tvOverallScore);
        tvVisionStatus = findViewById(R.id.tvVisionStatus);

        // Assessment
        assessmentCard = findViewById(R.id.assessmentCard);
        tvRiskLevel = findViewById(R.id.tvRiskLevel);

        // Nutrition
        tvNutritionRecommendations = findViewById(R.id.tvNutritionRecommendations);
        tvEyeCareInstructions = findViewById(R.id.tvEyeCareInstructions);

        // Save button
        btnSaveToHistory = findViewById(R.id.btnSaveToHistory);
        if (btnSaveToHistory != null) {
            btnSaveToHistory.setVisibility(View.GONE); // Hidden since we auto-save
        }
    }

    private void generateNewReport() {
        int profileId = sessionManager.getSelectedProfileId();

        apiManager.generateReport(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                displayReport(data);
                            } else {
                                showError("No report data received");
                            }
                        } else {
                            showError(response.optString("message", "Failed to generate report"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing report", e);
                        showError("Error displaying report");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }

    private void loadExistingReport(int reportId) {
        int profileId = sessionManager.getSelectedProfileId();

        apiManager.getReportById(reportId, profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                displayReport(data);
                            } else {
                                showError("Report not found");
                            }
                        } else {
                            showError(response.optString("message", "Failed to load report"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing report", e);
                        showError("Error displaying report");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }

    private void displayReport(JSONObject data) {
        try {
            // Display profile info
            JSONObject profile = data.optJSONObject("profile");
            if (profile != null) {
                if (tvProfileName != null) {
                    tvProfileName.setText(profile.optString("name", "User"));
                }
                if (tvProfileAge != null) {
                    int age = profile.optInt("age", 0);
                    tvProfileAge.setText(age > 0 ? age + " years" : "--");
                }
                if (tvRightBaseline != null) {
                    tvRightBaseline.setText(profile.optString("prior_right_sight", "Unknown"));
                }
                if (tvLeftBaseline != null) {
                    tvLeftBaseline.setText(profile.optString("prior_left_sight", "Unknown"));
                }
            }

            // Display test results
            JSONArray testResults = data.optJSONArray("test_results");
            if (testResults != null && testResultsContainer != null) {
                testResultsContainer.removeAllViews();
                for (int i = 0; i < testResults.length(); i++) {
                    JSONObject test = testResults.getJSONObject(i);
                    addTestResultItem(test);
                }
            }

            // Display game results
            JSONArray gameResults = data.optJSONArray("game_results");
            JSONObject scores = data.optJSONObject("scores");
            boolean gamesPlayed = scores != null && scores.optBoolean("games_played", false);

            if (gameResultsContainer != null) {
                gameResultsContainer.removeAllViews();
                if (gamesPlayed && gameResults != null && gameResults.length() > 0) {
                    for (int i = 0; i < gameResults.length(); i++) {
                        JSONObject game = gameResults.getJSONObject(i);
                        addGameResultItem(game);
                    }
                } else {
                    // Show "No games played" message
                    TextView noGamesText = new TextView(this);
                    noGamesText.setText("No games played yet. Play games to improve your eye health assessment!");
                    noGamesText.setTextColor(getResources().getColor(R.color.text_secondary));
                    noGamesText.setPadding(0, 16, 0, 16);
                    gameResultsContainer.addView(noGamesText);
                }
            }

            // Display scores
            if (scores != null) {
                if (tvRightEyeOverall != null) {
                    double rightScore = scores.optDouble("avg_right_test_score", 0);
                    tvRightEyeOverall.setText("Right Eye Overall Score: " + (int) rightScore + " / 100");
                }
                if (tvLeftEyeOverall != null) {
                    double leftScore = scores.optDouble("avg_left_test_score", 0);
                    tvLeftEyeOverall.setText("Left Eye Overall Score: " + (int) leftScore + " / 100");
                }
                if (tvGameOverall != null) {
                    if (gamesPlayed) {
                        double gameScore = scores.optDouble("avg_game_score", 0);
                        tvGameOverall.setText("Overall Game Performance Score: " + (int) gameScore + " / 100");
                    } else {
                        tvGameOverall.setText("Overall Game Performance Score: N/A (No games played)");
                    }
                }
                if (tvOverallScore != null) {
                    double overallScore = scores.optDouble("overall_score", 0);
                    tvOverallScore.setText("• Overall Eye Health Score: " + (int) overallScore + " / 100");
                }
            }

            // Display assessment
            JSONObject assessment = data.optJSONObject("assessment");
            if (assessment != null) {
                String riskLevel = assessment.optString("risk_level", "Unknown");
                if (tvRiskLevel != null) {
                    tvRiskLevel.setText("• AI Vision Risk Level: " + riskLevel);
                }

                // Display vision status (based on eye status)
                if (tvVisionStatus != null) {
                    String rightStatus = assessment.optString("estimated_right_eye_status", "Normal");
                    String leftStatus = assessment.optString("estimated_left_eye_status", "Normal");
                    // Use the worse status of the two eyes
                    String visionStatus = getWorseStatus(rightStatus, leftStatus);
                    tvVisionStatus.setText("• Vision Status: " + visionStatus);
                }

                // Update card color based on risk level
                updateAssessmentCardColor(riskLevel);

                // Display nutrition recommendations
                JSONArray nutrients = assessment.optJSONArray("required_nutrients");
                if (nutrients != null && tvNutritionRecommendations != null) {
                    StringBuilder nutrientsList = new StringBuilder();
                    for (int i = 0; i < nutrients.length(); i++) {
                        nutrientsList.append("• ").append(nutrients.getString(i));
                        if (i < nutrients.length() - 1)
                            nutrientsList.append("\n");
                    }
                    tvNutritionRecommendations.setText(nutrientsList.toString());
                }

                // Display suggested action as eye care instructions
                if (tvEyeCareInstructions != null) {
                    String suggestedAction = assessment.optString("suggested_action", "");
                    tvEyeCareInstructions.setText(suggestedAction);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error displaying report data", e);
            showError("Error displaying report");
        }
    }

    private void addTestResultItem(JSONObject test) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_test_result, testResultsContainer, false);

        TextView tvTestName = itemView.findViewById(R.id.tvTestName);
        TextView tvRightScore = itemView.findViewById(R.id.tvRightScore);
        TextView tvLeftScore = itemView.findViewById(R.id.tvLeftScore);
        TextView tvTestStatus = itemView.findViewById(R.id.tvTestStatus);

        if (tvTestName != null) {
            tvTestName.setText(test.optString("test_name", "Test"));
        }

        int rightScore = (int) test.optDouble("right_score", 0);
        int leftScore = (int) test.optDouble("left_score", 0);

        if (tvRightScore != null) {
            tvRightScore.setText(rightScore + "%");
        }
        if (tvLeftScore != null) {
            tvLeftScore.setText(leftScore + "%");
        }

        // Determine individual test status based on average of both eye scores
        if (tvTestStatus != null) {
            double avgScore = (rightScore + leftScore) / 2.0;
            String status;
            int statusColor;

            if (avgScore >= 70) {
                status = "Normal";
                statusColor = Color.parseColor("#10B981"); // Green
            } else if (avgScore >= 50) {
                status = "Mild Concern";
                statusColor = Color.parseColor("#F59E0B"); // Amber/Yellow
            } else if (avgScore >= 30) {
                status = "Moderate Concern";
                statusColor = Color.parseColor("#F97316"); // Orange
            } else {
                status = "Severe Concern";
                statusColor = Color.parseColor("#EF4444"); // Red
            }

            tvTestStatus.setText(status);
            tvTestStatus.setTextColor(statusColor);
        }

        testResultsContainer.addView(itemView);
    }

    private void addGameResultItem(JSONObject game) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_game_result, gameResultsContainer, false);

        TextView tvGameName = itemView.findViewById(R.id.tvGameName);
        TextView tvGameScore = itemView.findViewById(R.id.tvGameScore);

        if (tvGameName != null) {
            tvGameName.setText(game.optString("game_name", "Game"));
        }
        if (tvGameScore != null) {
            int score = (int) game.optDouble("score", 0);
            tvGameScore.setText(score + "%");
        }

        gameResultsContainer.addView(itemView);
    }

    /**
     * Determine which eye status is worse for display purposes.
     * Priority: Severe Concern > Moderate Concern > Mild Concern > Normal
     */
    private String getWorseStatus(String rightStatus, String leftStatus) {
        String[] statusOrder = { "Severe Concern", "Moderate Concern", "Mild Concern", "Normal" };

        for (String status : statusOrder) {
            if (rightStatus.contains(status) || leftStatus.contains(status)) {
                return status;
            }
        }
        return "Normal";
    }

    private void updateAssessmentCardColor(String riskLevel) {
        if (assessmentCard == null)
            return;

        int backgroundColor;
        String lowerRisk = riskLevel.toLowerCase();

        if (lowerRisk.equals("very high")) {
            backgroundColor = Color.parseColor("#FFCDD2"); // Red
        } else if (lowerRisk.equals("high")) {
            backgroundColor = Color.parseColor("#FFEBEE"); // Light Red
        } else if (lowerRisk.equals("medium")) {
            backgroundColor = Color.parseColor("#FFF3E0"); // Orange
        } else if (lowerRisk.equals("low")) {
            backgroundColor = Color.parseColor("#E8F5E9"); // Green
        } else {
            backgroundColor = Color.parseColor("#FFFDE7"); // Default yellow
        }

        assessmentCard.setCardBackgroundColor(backgroundColor);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
    }
}
