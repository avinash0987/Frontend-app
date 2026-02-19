package com.simats.optovision.fragments;

import android.app.AlertDialog;
import com.simats.optovision.utils.DialogUtils;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.simats.optovision.AstigmatismTestActivity;
import com.simats.optovision.ColorVisionTestActivity;
import com.simats.optovision.ContrastTestActivity;
import com.simats.optovision.DistanceVisonTest;
import com.simats.optovision.NearVisionTestActivity;
import com.simats.optovision.R;
import com.simats.optovision.VisualFieldTestActivity;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;
import com.simats.optovision.utils.TestCompletionManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class TestsFragment extends Fragment {

    private static final String TAG = "TestsFragment";

    private TestCompletionManager testManager;
    private SessionManager sessionManager;
    private ApiManager apiManager;

    // Progress badge
    private TextView tvProgress;

    // Progress bars
    private ProgressBar progressDistanceVision, progressNearVision, progressColorVision;
    private ProgressBar progressAstigmatism, progressContrast, progressVisualField;

    // Lock icons
    private ImageView lockDistanceVision, lockNearVision, lockColorVision;
    private ImageView lockAstigmatism, lockContrast, lockVisualField;

    // Action buttons
    private Button btnRetakeTests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        sessionManager = new SessionManager(requireContext());
        apiManager = ApiManager.getInstance(requireContext());

        int profileId = sessionManager.getSelectedProfileId();
        testManager = new TestCompletionManager(requireContext(), profileId);

        // Initialize UI elements
        initViews(view);

        // Setup click listeners
        setupClickListeners(view);

        // Sync from database on first load
        syncTestStatusFromDatabase();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh UI and sync from database when returning to this fragment
        int profileId = sessionManager.getSelectedProfileId();
        testManager = new TestCompletionManager(requireContext(), profileId);
        syncTestStatusFromDatabase();
    }

    private void initViews(View view) {
        // Progress badge
        tvProgress = view.findViewById(R.id.tvProgress);

        // Progress bars
        progressDistanceVision = view.findViewById(R.id.progressDistanceVision);
        progressNearVision = view.findViewById(R.id.progressNearVision);
        progressColorVision = view.findViewById(R.id.progressColorVision);
        progressAstigmatism = view.findViewById(R.id.progressAstigmatism);
        progressContrast = view.findViewById(R.id.progressContrast);
        progressVisualField = view.findViewById(R.id.progressVisualField);

        // Lock icons
        lockDistanceVision = view.findViewById(R.id.lockDistanceVision);
        lockNearVision = view.findViewById(R.id.lockNearVision);
        lockColorVision = view.findViewById(R.id.lockColorVision);
        lockAstigmatism = view.findViewById(R.id.lockAstigmatism);
        lockContrast = view.findViewById(R.id.lockContrast);
        lockVisualField = view.findViewById(R.id.lockVisualField);

        // Action buttons
        btnRetakeTests = view.findViewById(R.id.btnRetakeTests);
    }

    private void setupClickListeners(View view) {
        // Distance Vision Test - always unlocked (Test 1)
        CardView cardDistanceVision = view.findViewById(R.id.cardDistanceVision);
        if (cardDistanceVision != null) {
            cardDistanceVision.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DistanceVisonTest.class);
                startActivity(intent);
            });
        }

        // Near Vision Test - unlocked after Distance Vision (Test 2)
        CardView cardNearVision = view.findViewById(R.id.cardNearVision);
        if (cardNearVision != null) {
            cardNearVision.setOnClickListener(v -> {
                if (testManager.isTestUnlocked(2)) {
                    Intent intent = new Intent(getContext(), NearVisionTestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Complete Distance Vision Test first!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Color Vision Test - unlocked after Near Vision (Test 3)
        CardView cardColorVision = view.findViewById(R.id.cardColorVision);
        if (cardColorVision != null) {
            cardColorVision.setOnClickListener(v -> {
                if (testManager.isTestUnlocked(3)) {
                    Intent intent = new Intent(getContext(), ColorVisionTestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Complete Near Vision Test first!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Astigmatism Test - unlocked after Color Vision (Test 4)
        CardView cardAstigmatism = view.findViewById(R.id.cardAstigmatism);
        if (cardAstigmatism != null) {
            cardAstigmatism.setOnClickListener(v -> {
                if (testManager.isTestUnlocked(4)) {
                    Intent intent = new Intent(getContext(), AstigmatismTestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Complete Color Vision Test first!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Contrast Sensitivity Test - unlocked after Astigmatism (Test 5)
        CardView cardContrastSensitivity = view.findViewById(R.id.cardContrastSensitivity);
        if (cardContrastSensitivity != null) {
            cardContrastSensitivity.setOnClickListener(v -> {
                if (testManager.isTestUnlocked(5)) {
                    Intent intent = new Intent(getContext(), ContrastTestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Complete Astigmatism Test first!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Visual Field Test - unlocked after Contrast Sensitivity (Test 6)
        CardView cardVisualField = view.findViewById(R.id.cardVisualField);
        if (cardVisualField != null) {
            cardVisualField.setOnClickListener(v -> {
                if (testManager.isTestUnlocked(6)) {
                    Intent intent = new Intent(getContext(), VisualFieldTestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Complete Contrast Sensitivity Test first!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Retake Tests button
        if (btnRetakeTests != null) {
            btnRetakeTests.setOnClickListener(v -> showRetakeConfirmationDialog());
        }
    }

    private void showRetakeConfirmationDialog() {
        AlertDialog d = new AlertDialog.Builder(requireContext())
                .setTitle("Retake All Tests")
                .setMessage(
                        "This will reset all your tests for this profile. Your previous results will still be saved in history. Are you sure you want to retake all tests?")
                .setPositiveButton("Yes, Retake", (dialog, which) -> {
                    retakeAllTests();
                })
                .setNegativeButton("Cancel", null)
                .show();
        DialogUtils.styleWhite(d);
    }

    private void retakeAllTests() {
        int profileId = sessionManager.getSelectedProfileId();

        // Reset local SharedPreferences for tests
        testManager.resetAllTests();

        // Also reset games for a fresh session
        GameCompletionManager gameManager = new GameCompletionManager(requireContext(), profileId);
        gameManager.resetAllGames();

        // Clear the game flow flag
        requireContext().getSharedPreferences("GameFlow", android.content.Context.MODE_PRIVATE)
                .edit().remove("started_games_" + profileId).apply();

        // Clear from database (optional - depends on backend support)
        if (profileId > 0) {
            apiManager.clearTestResults(profileId, new ApiManager.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, "Test results cleared from database");
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Tests reset! Start from Distance Vision Test.",
                                    Toast.LENGTH_SHORT).show();
                            updateTestsUI();
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Failed to clear test results from database: " + errorMessage);
                    // Still update UI since local data is cleared
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Tests reset locally. Start from Distance Vision Test.",
                                    Toast.LENGTH_SHORT).show();
                            updateTestsUI();
                        });
                    }
                }
            });
        } else {
            // No profile, just update UI
            Toast.makeText(getContext(), "Tests reset! Start from Distance Vision Test.", Toast.LENGTH_SHORT).show();
            updateTestsUI();
        }
    }

    private void syncTestStatusFromDatabase() {
        int profileId = sessionManager.getSelectedProfileId();

        if (profileId <= 0) {
            Log.w(TAG, "No profile selected, using local data only");
            updateTestsUI();
            return;
        }

        apiManager.getTestResults(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded())
                    return;

                try {
                    if (response.optBoolean("success", false)) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            JSONArray results = data.optJSONArray("results");
                            testManager.syncFromDatabase(results);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error syncing test status from database", e);
                }

                requireActivity().runOnUiThread(() -> updateTestsUI());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to sync test status: " + errorMessage);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateTestsUI());
                }
            }
        });
    }

    private void updateTestsUI() {
        // Update progress badge
        int completedCount = testManager.getCompletedTestCount();
        tvProgress.setText(completedCount + " of 6\ncompleted");

        // Update each test card
        updateTestCard(1, testManager.isTestCompleted(1), testManager.isTestUnlocked(1),
                progressDistanceVision, lockDistanceVision);
        updateTestCard(2, testManager.isTestCompleted(2), testManager.isTestUnlocked(2),
                progressNearVision, lockNearVision);
        updateTestCard(3, testManager.isTestCompleted(3), testManager.isTestUnlocked(3),
                progressColorVision, lockColorVision);
        updateTestCard(4, testManager.isTestCompleted(4), testManager.isTestUnlocked(4),
                progressAstigmatism, lockAstigmatism);
        updateTestCard(5, testManager.isTestCompleted(5), testManager.isTestUnlocked(5),
                progressContrast, lockContrast);
        updateTestCard(6, testManager.isTestCompleted(6), testManager.isTestUnlocked(6),
                progressVisualField, lockVisualField);

        // Update button visibility
        if (btnRetakeTests != null) {
            btnRetakeTests.setVisibility(completedCount > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void updateTestCard(int testNumber, boolean isCompleted, boolean isUnlocked,
            ProgressBar progressBar, ImageView lockIcon) {
        if (progressBar == null || lockIcon == null)
            return;

        if (isCompleted) {
            // Test completed - show checkmark and 100% progress
            progressBar.setProgress(100);
            lockIcon.setImageResource(R.drawable.ic_check_circle);
            lockIcon.setColorFilter(0xFF10B981); // Green
            lockIcon.setVisibility(View.VISIBLE);
        } else if (isUnlocked) {
            // Test unlocked but not completed - hide lock, show 0% progress
            progressBar.setProgress(0);
            lockIcon.setVisibility(View.GONE);
        } else {
            // Test locked - show lock icon, 0% progress
            progressBar.setProgress(0);
            lockIcon.setImageResource(R.drawable.ic_lock);
            lockIcon.setColorFilter(0xFF9CA3AF); // Gray
            lockIcon.setVisibility(View.VISIBLE);
        }
    }
}
