package com.simats.optovision.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simats.optovision.DashboardActivity;
import com.simats.optovision.R;
import com.simats.optovision.ReportActivity;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";

    private SessionManager sessionManager;
    private ApiManager apiManager;
    private LinearLayout historyContainer;
    private View emptyState;
    private View loadingIndicator;
    private TextView tvResultsCount;
    private TextView tvEmptyMessage;

    // Filters
    private Spinner spinnerStatus;
    private Spinner spinnerTimePeriod;
    private Button btnSort;

    // Filter state
    private String selectedStatus = "all";
    private String selectedTimePeriod = "all";
    private boolean sortNewestFirst = true;

    // Data
    private List<JSONObject> allSessions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        sessionManager = new SessionManager(requireContext());
        apiManager = ApiManager.getInstance(requireContext());

        initViews(view);
        setupFilters();
        loadTestSessions();

        return view;
    }

    private void initViews(View view) {
        historyContainer = view.findViewById(R.id.historyContainer);
        emptyState = view.findViewById(R.id.emptyState);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        tvResultsCount = view.findViewById(R.id.tvResultsCount);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        // Filter views
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        spinnerTimePeriod = view.findViewById(R.id.spinnerTimePeriod);
        btnSort = view.findViewById(R.id.btnSort);

        Button btnStartTest = view.findViewById(R.id.btnStartTest);
        if (btnStartTest != null) {
            btnStartTest.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToTab(R.id.nav_tests);
                }
            });
        }
    }

    private void setupFilters() {
        // Status filter options
        String[] statusOptions = { "All Status", "Normal", "Attention", "Critical" };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                statusOptions);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedStatus = "all";
                        break;
                    case 1:
                        selectedStatus = "normal";
                        break;
                    case 2:
                        selectedStatus = "attention";
                        break;
                    case 3:
                        selectedStatus = "critical";
                        break;
                }
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Time period filter options
        String[] timePeriodOptions = { "All Time", "This Week", "This Month", "Last 3 Months" };
        ArrayAdapter<String> timePeriodAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                timePeriodOptions);
        timePeriodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTimePeriod.setAdapter(timePeriodAdapter);
        spinnerTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedTimePeriod = "all";
                        break;
                    case 1:
                        selectedTimePeriod = "week";
                        break;
                    case 2:
                        selectedTimePeriod = "month";
                        break;
                    case 3:
                        selectedTimePeriod = "3months";
                        break;
                }
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Sort button toggle
        btnSort.setOnClickListener(v -> {
            sortNewestFirst = !sortNewestFirst;
            btnSort.setText(sortNewestFirst ? "↓ Newest" : "↑ Oldest");
            applyFilters();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTestSessions();
    }

    private void loadTestSessions() {
        int profileId = sessionManager.getSelectedProfileId();

        if (profileId <= 0) {
            Log.w(TAG, "No profile selected");
            showEmptyState("No profile selected");
            return;
        }

        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        // First try to fetch reports
        apiManager.getReports(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded())
                    return;

                try {
                    if (response.optBoolean("success", false)) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            JSONArray reports = data.optJSONArray("reports");
                            if (reports != null && reports.length() > 0) {
                                requireActivity().runOnUiThread(() -> {
                                    if (loadingIndicator != null) {
                                        loadingIndicator.setVisibility(View.GONE);
                                    }
                                    allSessions.clear();
                                    try {
                                        for (int i = 0; i < reports.length(); i++) {
                                            JSONObject report = reports.getJSONObject(i);
                                            report.put("is_report", true); // Mark as report type
                                            allSessions.add(report);
                                        }
                                        applyFilters();
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error adding reports", e);
                                    }
                                });
                                return;
                            }
                        }
                    }
                    // No reports found, fallback to test sessions
                    loadTestSessionsFallback(profileId);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing reports", e);
                    loadTestSessionsFallback(profileId);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load reports: " + errorMessage);
                // Fallback to test sessions
                loadTestSessionsFallback(profileId);
            }
        });
    }

    private void loadTestSessionsFallback(int profileId) {
        apiManager.getTestSessions(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded())
                    return;

                requireActivity().runOnUiThread(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisibility(View.GONE);
                    }

                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                JSONArray sessions = data.optJSONArray("sessions");
                                if (sessions != null && sessions.length() > 0) {
                                    allSessions.clear();
                                    for (int i = 0; i < sessions.length(); i++) {
                                        JSONObject session = sessions.getJSONObject(i);
                                        session.put("is_report", false); // Mark as session type
                                        allSessions.add(session);
                                    }
                                    applyFilters();
                                    return;
                                }
                            }
                        }
                        showEmptyState("No test history yet. Take your first test!");
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing sessions", e);
                        showEmptyState("Error loading history");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load test sessions: " + errorMessage);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisibility(View.GONE);
                        }
                        showEmptyState("Unable to load history. Check connection.");
                    });
                }
            }
        });
    }

    private void applyFilters() {
        if (!isAdded())
            return;

        List<JSONObject> filteredSessions = new ArrayList<>();

        // Apply filters
        for (JSONObject session : allSessions) {
            String status = session.optString("overall_status", "normal");
            String dateKey = session.optString("session_date_key", "");

            // Status filter
            if (!selectedStatus.equals("all") && !status.equals(selectedStatus)) {
                continue;
            }

            // Time period filter (simplified - full implementation would parse dates)
            // For now, we include all and let the backend handle time filtering if needed
            if (!selectedTimePeriod.equals("all")) {
                // Could add date parsing here for client-side filtering
            }

            filteredSessions.add(session);
        }

        // Apply sort
        if (!sortNewestFirst) {
            Collections.reverse(filteredSessions);
        }

        // Display
        displaySessions(filteredSessions);
    }

    private void displaySessions(List<JSONObject> sessions) {
        if (historyContainer == null || !isAdded())
            return;

        historyContainer.removeAllViews();

        if (sessions.isEmpty()) {
            showEmptyState("No reports match your filters");
            return;
        }

        // Hide empty state
        if (emptyState != null) {
            emptyState.setVisibility(View.GONE);
        }

        // Update results count
        if (tvResultsCount != null) {
            tvResultsCount.setText(sessions.size() + (sessions.size() == 1 ? " report found" : " reports found"));
        }

        // Add session items
        for (JSONObject session : sessions) {
            addSessionItem(session);
        }
    }

    private void addSessionItem(JSONObject session) {
        if (!isAdded())
            return;

        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_history_session, historyContainer, false);

        TextView tvSessionTitle = itemView.findViewById(R.id.tvSessionTitle);
        TextView tvSessionDate = itemView.findViewById(R.id.tvSessionDate);
        TextView tvTestsCompleted = itemView.findViewById(R.id.tvTestsCompleted);
        TextView tvAvgScore = itemView.findViewById(R.id.tvAvgScore);
        TextView tvStatus = itemView.findViewById(R.id.tvStatus);

        boolean isReport = session.optBoolean("is_report", false);

        if (isReport) {
            // Display report data
            if (tvSessionTitle != null) {
                String reportTime = session.optString("report_time", "");
                tvSessionTitle.setText("Eye Health Report" + (reportTime.isEmpty() ? "" : " • " + reportTime));
            }

            if (tvSessionDate != null) {
                tvSessionDate.setText(session.optString("report_date", ""));
            }

            if (tvTestsCompleted != null) {
                String riskLevel = session.optString("risk_level", "Unknown");
                tvTestsCompleted.setText(riskLevel);
            }

            if (tvAvgScore != null) {
                double overallScore = session.optDouble("overall_score", 0);
                tvAvgScore.setText("Score: " + (int) overallScore + "%");
            }

            if (tvStatus != null) {
                String riskLevel = session.optString("risk_level", "Normal").toLowerCase();
                setStatusDisplay(tvStatus, riskLevel);
            }

            // Click to view report details
            final int reportId = session.optInt("report_id", 0);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ReportActivity.class);
                intent.putExtra("report_id", reportId);
                startActivity(intent);
            });
        } else {
            // Display test session data
            if (tvSessionTitle != null) {
                int testsCompleted = session.optInt("tests_completed", 0);
                tvSessionTitle.setText("Test Session • " + testsCompleted + " test" + (testsCompleted != 1 ? "s" : ""));
            }

            if (tvSessionDate != null) {
                tvSessionDate.setText(session.optString("session_date", ""));
            }

            if (tvTestsCompleted != null) {
                int testsCompleted = session.optInt("tests_completed", 0);
                tvTestsCompleted.setText(testsCompleted + "/6 Tests");
            }

            if (tvAvgScore != null) {
                double rightScore = session.optDouble("avg_right_score", 0);
                double leftScore = session.optDouble("avg_left_score", 0);
                double avgScore = (rightScore + leftScore) / 2;
                tvAvgScore.setText("Avg: " + (int) avgScore + "%");
            }

            if (tvStatus != null) {
                String status = session.optString("overall_status", "normal");
                setStatusDisplay(tvStatus, status);
            }

            // Test sessions show a message - no detailed view
            itemView.setOnClickListener(v -> {
                // Could show a dialog with test details or navigate to a summary
                android.widget.Toast.makeText(requireContext(),
                        "Complete all 6 tests and games to generate a full report",
                        android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        historyContainer.addView(itemView);
    }

    private void setStatusDisplay(TextView tvStatus, String statusOrRiskLevel) {
        String displayStatus;
        int color;

        String lower = statusOrRiskLevel.toLowerCase();
        if (lower.contains("very high") || lower.equals("critical")) {
            displayStatus = "Critical";
            color = 0xFFE74C3C;
        } else if (lower.contains("high") || lower.equals("attention")) {
            displayStatus = "Attention";
            color = 0xFFEF4444;
        } else if (lower.contains("moderate")) {
            displayStatus = "Moderate";
            color = 0xFFFF9800;
        } else if (lower.contains("low")) {
            displayStatus = "Low Risk";
            color = 0xFF8BC34A;
        } else {
            displayStatus = "Normal";
            color = 0xFF27AE60;
        }
        tvStatus.setText(displayStatus);
        tvStatus.setTextColor(color);
    }

    private void showEmptyState(String message) {
        if (!isAdded())
            return;

        if (emptyState != null) {
            emptyState.setVisibility(View.VISIBLE);
        }
        if (tvEmptyMessage != null) {
            tvEmptyMessage.setText(message);
        }
        if (historyContainer != null) {
            historyContainer.removeAllViews();
        }
        if (tvResultsCount != null) {
            tvResultsCount.setText("0 reports found");
        }
    }
}
