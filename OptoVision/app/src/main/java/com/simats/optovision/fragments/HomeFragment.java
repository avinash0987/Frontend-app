package com.simats.optovision.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simats.optovision.DashboardActivity;
import com.simats.optovision.EyeCareGuidelinesActivity;
import com.simats.optovision.EyeExercisesActivity;
import com.simats.optovision.GamesActivity;
import com.simats.optovision.R;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;
import com.simats.optovision.utils.TestCompletionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView tvGreeting;
    private TextView btnStartTest;
    private LinearLayout containerRecentActivity;
    private LinearLayout emptyRecentActivity;
    private SessionManager sessionManager;
    private TestCompletionManager testManager;
    private ApiManager apiManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        int profileId = sessionManager.getSelectedProfileId();
        testManager = new TestCompletionManager(requireContext(), profileId);
        apiManager = ApiManager.getInstance(requireContext());

        tvGreeting = view.findViewById(R.id.tvGreeting);
        btnStartTest = view.findViewById(R.id.btnStartTest);
        containerRecentActivity = view.findViewById(R.id.containerRecentActivity);
        emptyRecentActivity = view.findViewById(R.id.emptyRecentActivity);

        // Load selected profile name
        loadProfileData();

        // Load recent activity from database
        loadRecentActivityFromDatabase();

        // Set click listeners
        btnStartTest.setOnClickListener(v -> {
            // Navigate to Tests tab
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToTab(R.id.nav_tests);
            }
        });

        // Quick action cards
        View cardStartTest = view.findViewById(R.id.cardStartTest);
        View cardNutrition = view.findViewById(R.id.cardNutrition);
        View cardTests = view.findViewById(R.id.cardTests);
        View cardExercises = view.findViewById(R.id.cardExercises);
        View cardGames = view.findViewById(R.id.cardGames);
        View cardGuidelines = view.findViewById(R.id.cardGuidelines);
        TextView tvStartNow = view.findViewById(R.id.tvStartNow);

        // Start Test card -> Tests tab
        if (cardStartTest != null) {
            cardStartTest.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToTab(R.id.nav_tests);
                }
            });
        }

        // Nutrition card -> Nutrition tab
        if (cardNutrition != null) {
            cardNutrition.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToTab(R.id.nav_nutrition);
                }
            });
        }

        // Tests card -> Tests tab
        if (cardTests != null) {
            cardTests.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToTab(R.id.nav_tests);
                }
            });
        }

        // Exercises card -> Eye Exercises Activity
        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EyeExercisesActivity.class);
                startActivity(intent);
            });
        }

        // Games card -> Games Activity
        if (cardGames != null) {
            cardGames.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), GamesActivity.class);
                startActivity(intent);
            });
        }

        // Guidelines card -> Eye Care Guidelines Activity
        if (cardGuidelines != null) {
            cardGuidelines.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EyeCareGuidelinesActivity.class);
                startActivity(intent);
            });
        }

        // Start Now text -> Tests tab
        if (tvStartNow != null) {
            tvStartNow.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToTab(R.id.nav_tests);
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh recent activity when returning
        loadRecentActivityFromDatabase();
        // Load latest eye health score
        loadLatestEyeHealthScore();
    }

    private void loadLatestEyeHealthScore() {
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId <= 0)
            return;

        View view = getView();
        if (view == null)
            return;

        TextView tvEyeHealthScore = view.findViewById(R.id.tvEyeHealthScore);
        TextView tvScoreStatus = view.findViewById(R.id.tvScoreStatus);

        // Fetch latest report from database
        apiManager.getLatestReport(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded())
                    return;

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null && data.optBoolean("has_report", false)) {
                                double overallScore = data.optDouble("overall_score", 0);
                                String visionStatus = data.optString("vision_status", "");

                                tvEyeHealthScore.setText(String.valueOf((int) overallScore));
                                tvScoreStatus.setText(visionStatus);

                                // Set color based on score
                                int color;
                                if (overallScore >= 85) {
                                    color = 0xFF27AE60; // Green
                                } else if (overallScore >= 70) {
                                    color = 0xFF8BC34A; // Light Green
                                } else if (overallScore >= 50) {
                                    color = 0xFFFF9800; // Orange
                                } else if (overallScore >= 30) {
                                    color = 0xFFFF5722; // Deep Orange
                                } else {
                                    color = 0xFFE74C3C; // Red
                                }
                                tvEyeHealthScore.setTextColor(color);
                            } else {
                                // No report yet
                                tvEyeHealthScore.setText("--");
                                tvEyeHealthScore.setTextColor(0xFF27AE60);
                                tvScoreStatus.setText("Complete tests");
                            }
                        }
                    } catch (Exception e) {
                        // Fallback to placeholder
                        tvEyeHealthScore.setText("--");
                        tvScoreStatus.setText("Complete tests");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded())
                    return;
                requireActivity().runOnUiThread(() -> {
                    tvEyeHealthScore.setText("--");
                    tvScoreStatus.setText("Complete tests");
                });
            }
        });
    }

    private void loadProfileData() {
        // Get the selected profile name from SessionManager
        String profileName = sessionManager.getSelectedProfileName();

        // If no profile selected, use the user's name
        if (profileName == null || profileName.isEmpty()) {
            profileName = sessionManager.getUserName();
        }

        // If still empty, use default
        if (profileName == null || profileName.isEmpty()) {
            profileName = "User";
        }

        tvGreeting.setText("Hello, " + profileName + "! 👋");
    }

    private void loadRecentActivityFromDatabase() {
        int profileId = sessionManager.getSelectedProfileId();

        if (profileId <= 0) {
            // No profile selected, use local data
            loadRecentActivityFromLocal();
            return;
        }

        // Try to load from database
        apiManager.getRecentActivity(profileId, 4, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.optBoolean("success", false)) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            JSONArray activities = data.optJSONArray("activities");
                            if (activities != null && activities.length() > 0) {
                                displayRecentActivities(activities);
                                return;
                            }
                        }
                    }
                    // Fallback to local if no data from server
                    loadRecentActivityFromLocal();
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing recent activity", e);
                    loadRecentActivityFromLocal();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load recent activity: " + errorMessage);
                // Fallback to local data
                loadRecentActivityFromLocal();
            }
        });
    }

    private void displayRecentActivities(JSONArray activities) {
        if (containerRecentActivity == null || !isAdded())
            return;

        requireActivity().runOnUiThread(() -> {
            // Hide empty state
            if (emptyRecentActivity != null) {
                emptyRecentActivity.setVisibility(View.GONE);
            }

            // Clear previous dynamic views
            for (int i = containerRecentActivity.getChildCount() - 1; i >= 0; i--) {
                View child = containerRecentActivity.getChildAt(i);
                if (child.getId() != R.id.emptyRecentActivity) {
                    containerRecentActivity.removeViewAt(i);
                }
            }

            try {
                for (int i = 0; i < activities.length(); i++) {
                    JSONObject activity = activities.getJSONObject(i);
                    String name = activity.optString("name", "");
                    String type = activity.optString("type", "test");
                    String score = activity.optString("score", "");

                    addRecentActivityItem(name, type, score);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error displaying activities", e);
            }
        });
    }

    private void loadRecentActivityFromLocal() {
        if (containerRecentActivity == null || !isAdded())
            return;

        requireActivity().runOnUiThread(() -> {
            List<String> recentTests = testManager.getRecentCompletedTests();

            if (recentTests.isEmpty()) {
                // Show empty state
                if (emptyRecentActivity != null) {
                    emptyRecentActivity.setVisibility(View.VISIBLE);
                }
            } else {
                // Hide empty state
                if (emptyRecentActivity != null) {
                    emptyRecentActivity.setVisibility(View.GONE);
                }

                // Clear previous dynamic views
                for (int i = containerRecentActivity.getChildCount() - 1; i >= 0; i--) {
                    View child = containerRecentActivity.getChildAt(i);
                    if (child.getId() != R.id.emptyRecentActivity) {
                        containerRecentActivity.removeViewAt(i);
                    }
                }

                // Add completed tests (show max 4 most recent)
                int maxToShow = Math.min(recentTests.size(), 4);
                for (int i = 0; i < maxToShow; i++) {
                    addRecentActivityItem(recentTests.get(i), "test", "");
                }
            }
        });
    }

    private void addRecentActivityItem(String name, String type, String score) {
        if (!isAdded())
            return;

        LinearLayout itemLayout = new LinearLayout(requireContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 12, 0, 12);

        // Icon
        TextView iconView = new TextView(requireContext());
        iconView.setText(getActivityIcon(name, type));
        iconView.setTextSize(20);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (36 * getResources().getDisplayMetrics().density),
                (int) (36 * getResources().getDisplayMetrics().density)));
        iconView.setGravity(Gravity.CENTER);

        // Text container
        LinearLayout textContainer = new LinearLayout(requireContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textContainerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textContainerParams.setMarginStart((int) (12 * getResources().getDisplayMetrics().density));
        textContainer.setLayoutParams(textContainerParams);

        // Name
        TextView nameView = new TextView(requireContext());
        nameView.setText(name);
        nameView.setTextColor(0xFF1F2937);
        nameView.setTextSize(14);
        nameView.setTypeface(null, Typeface.BOLD);
        textContainer.addView(nameView);

        // Status/Score text
        TextView statusView = new TextView(requireContext());
        String statusText = "Completed";
        if (score != null && !score.isEmpty()) {
            statusText = "Score: " + score;
        }
        statusView.setText(statusText + " ✓");
        statusView.setTextColor(0xFF10B981);
        statusView.setTextSize(12);
        textContainer.addView(statusView);

        // Check icon
        ImageView checkIcon = new ImageView(requireContext());
        checkIcon.setImageResource(R.drawable.ic_check_circle);
        checkIcon.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density)));
        checkIcon.setColorFilter(0xFF10B981);

        itemLayout.addView(iconView);
        itemLayout.addView(textContainer);
        itemLayout.addView(checkIcon);

        containerRecentActivity.addView(itemLayout);
    }

    private String getActivityIcon(String name, String type) {
        if ("exercise".equals(type)) {
            return "🏃";
        } else if ("game".equals(type)) {
            return "🎮";
        }

        // Test icons
        if (name.contains("Distance"))
            return "👁️";
        if (name.contains("Near"))
            return "👓";
        if (name.contains("Color"))
            return "🌈";
        if (name.contains("Astigmatism"))
            return "◐";
        if (name.contains("Contrast"))
            return "◑";
        if (name.contains("Visual Field"))
            return "🔍";
        return "✓";
    }
}
