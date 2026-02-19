package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONObject;

/**
 * ReportGeneratingActivity shows a loading animation while generating the
 * report.
 * Automatically navigates to ReportActivity when report is ready.
 */
public class ReportGeneratingActivity extends AppCompatActivity {

    private static final String TAG = "ReportGenerating";

    private SessionManager sessionManager;
    private ApiManager apiManager;
    private TextView tvAnalysisStatus;
    private Handler handler;
    private int dotsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generating);

        initManagers();
        initViews();
        startLoadingAnimation();
        generateReport();
    }

    private void initManagers() {
        sessionManager = new SessionManager(this);
        apiManager = ApiManager.getInstance(this);
        handler = new Handler();
    }

    private void initViews() {
        tvAnalysisStatus = findViewById(R.id.tvAnalysisStatus);
    }

    private void startLoadingAnimation() {
        final Runnable animateDotsRunnable = new Runnable() {
            @Override
            public void run() {
                if (tvAnalysisStatus != null) {
                    dotsCount = (dotsCount + 1) % 4;
                    StringBuilder dots = new StringBuilder();
                    for (int i = 0; i < dotsCount; i++) {
                        dots.append(".");
                    }
                    tvAnalysisStatus.setText("Analyzing your results" + dots.toString());
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.post(animateDotsRunnable);
    }

    private void generateReport() {
        int profileId = sessionManager.getSelectedProfileId();

        if (profileId <= 0) {
            showError("No profile selected");
            finish();
            return;
        }

        apiManager.generateReport(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    handler.removeCallbacksAndMessages(null);
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                int reportId = data.optInt("report_id", 0);

                                // Navigate to ReportActivity with the new report
                                Intent intent = new Intent(ReportGeneratingActivity.this, ReportActivity.class);
                                intent.putExtra("report_id", reportId);
                                startActivity(intent);
                                finish();
                            } else {
                                showError("Failed to generate report");
                            }
                        } else {
                            showError(response.optString("message", "Failed to generate report"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing report response", e);
                        showError("Error generating report");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    handler.removeCallbacksAndMessages(null);
                    showError(errorMessage);
                });
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);

        // Go back after showing error
        handler.postDelayed(this::finish, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
