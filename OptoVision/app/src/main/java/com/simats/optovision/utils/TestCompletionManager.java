package com.simats.optovision.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager to track test completion status and scores
 * Tests must be completed in order: Distance Vision -> Near Vision -> Color
 * Vision -> etc.
 * Scores are stored for report generation.
 * Now profile-aware: each profile has its own SharedPreferences.
 */
public class TestCompletionManager {

    private static final String PREF_PREFIX = "TestCompletion_";

    // Test completion keys
    private static final String KEY_DISTANCE_VISION_COMPLETED = "distanceVisionCompleted";
    private static final String KEY_NEAR_VISION_COMPLETED = "nearVisionCompleted";
    private static final String KEY_COLOR_VISION_COMPLETED = "colorVisionCompleted";
    private static final String KEY_ASTIGMATISM_COMPLETED = "astigmatismCompleted";
    private static final String KEY_CONTRAST_SENSITIVITY_COMPLETED = "contrastSensitivityCompleted";
    private static final String KEY_VISUAL_FIELD_COMPLETED = "visualFieldCompleted";

    // Test score keys - Right Eye
    private static final String KEY_DISTANCE_RIGHT_SCORE = "distanceRightScore";
    private static final String KEY_NEAR_RIGHT_SCORE = "nearRightScore";
    private static final String KEY_COLOR_SCORE = "colorScore";
    private static final String KEY_ASTIGMATISM_RIGHT_SCORE = "astigmatismRightScore";
    private static final String KEY_CONTRAST_RIGHT_SCORE = "contrastRightScore";
    private static final String KEY_VISUAL_FIELD_RIGHT_SCORE = "visualFieldRightScore";

    // Test score keys - Left Eye
    private static final String KEY_DISTANCE_LEFT_SCORE = "distanceLeftScore";
    private static final String KEY_NEAR_LEFT_SCORE = "nearLeftScore";
    private static final String KEY_ASTIGMATISM_LEFT_SCORE = "astigmatismLeftScore";
    private static final String KEY_CONTRAST_LEFT_SCORE = "contrastLeftScore";
    private static final String KEY_VISUAL_FIELD_LEFT_SCORE = "visualFieldLeftScore";

    // Timestamps for recent activity
    private static final String KEY_DISTANCE_COMPLETED_TIME = "distanceCompletedTime";
    private static final String KEY_NEAR_COMPLETED_TIME = "nearCompletedTime";
    private static final String KEY_COLOR_COMPLETED_TIME = "colorCompletedTime";
    private static final String KEY_ASTIGMATISM_COMPLETED_TIME = "astigmatismCompletedTime";
    private static final String KEY_CONTRAST_COMPLETED_TIME = "contrastCompletedTime";
    private static final String KEY_VISUAL_FIELD_COMPLETED_TIME = "visualFieldCompletedTime";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int profileId;

    public TestCompletionManager(Context context, int profileId) {
        this.profileId = profileId;
        String prefName = PREF_PREFIX + profileId;
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // =================== Distance Vision Test ===================
    public void setDistanceVisionCompleted(boolean completed) {
        editor.putBoolean(KEY_DISTANCE_VISION_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_DISTANCE_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isDistanceVisionCompleted() {
        return prefs.getBoolean(KEY_DISTANCE_VISION_COMPLETED, false);
    }

    public void setDistanceVisionScores(int rightScore, int leftScore) {
        editor.putInt(KEY_DISTANCE_RIGHT_SCORE, rightScore);
        editor.putInt(KEY_DISTANCE_LEFT_SCORE, leftScore);
        editor.apply();
    }

    public int getDistanceRightScore() {
        return prefs.getInt(KEY_DISTANCE_RIGHT_SCORE, 0);
    }

    public int getDistanceLeftScore() {
        return prefs.getInt(KEY_DISTANCE_LEFT_SCORE, 0);
    }

    // =================== Near Vision Test ===================
    public void setNearVisionCompleted(boolean completed) {
        editor.putBoolean(KEY_NEAR_VISION_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_NEAR_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isNearVisionCompleted() {
        return prefs.getBoolean(KEY_NEAR_VISION_COMPLETED, false);
    }

    public void setNearVisionScores(int rightScore, int leftScore) {
        editor.putInt(KEY_NEAR_RIGHT_SCORE, rightScore);
        editor.putInt(KEY_NEAR_LEFT_SCORE, leftScore);
        editor.apply();
    }

    public int getNearRightScore() {
        return prefs.getInt(KEY_NEAR_RIGHT_SCORE, 0);
    }

    public int getNearLeftScore() {
        return prefs.getInt(KEY_NEAR_LEFT_SCORE, 0);
    }

    // =================== Color Vision Test ===================
    public void setColorVisionCompleted(boolean completed) {
        editor.putBoolean(KEY_COLOR_VISION_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_COLOR_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isColorVisionCompleted() {
        return prefs.getBoolean(KEY_COLOR_VISION_COMPLETED, false);
    }

    public void setColorVisionScore(int score) {
        editor.putInt(KEY_COLOR_SCORE, score);
        editor.apply();
    }

    // Add color scores with left/right for consistency
    public void setColorVisionScores(int rightScore, int leftScore) {
        editor.putInt(KEY_COLOR_SCORE, rightScore); // Use right as main
        editor.apply();
    }

    public int getColorVisionScore() {
        return prefs.getInt(KEY_COLOR_SCORE, 0);
    }

    // =================== Astigmatism Test ===================
    public void setAstigmatismCompleted(boolean completed) {
        editor.putBoolean(KEY_ASTIGMATISM_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_ASTIGMATISM_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isAstigmatismCompleted() {
        return prefs.getBoolean(KEY_ASTIGMATISM_COMPLETED, false);
    }

    public void setAstigmatismScores(int rightScore, int leftScore) {
        editor.putInt(KEY_ASTIGMATISM_RIGHT_SCORE, rightScore);
        editor.putInt(KEY_ASTIGMATISM_LEFT_SCORE, leftScore);
        editor.apply();
    }

    public int getAstigmatismRightScore() {
        return prefs.getInt(KEY_ASTIGMATISM_RIGHT_SCORE, 0);
    }

    public int getAstigmatismLeftScore() {
        return prefs.getInt(KEY_ASTIGMATISM_LEFT_SCORE, 0);
    }

    // =================== Contrast Sensitivity Test ===================
    public void setContrastSensitivityCompleted(boolean completed) {
        editor.putBoolean(KEY_CONTRAST_SENSITIVITY_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_CONTRAST_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isContrastSensitivityCompleted() {
        return prefs.getBoolean(KEY_CONTRAST_SENSITIVITY_COMPLETED, false);
    }

    public void setContrastSensitivityScores(int rightScore, int leftScore) {
        editor.putInt(KEY_CONTRAST_RIGHT_SCORE, rightScore);
        editor.putInt(KEY_CONTRAST_LEFT_SCORE, leftScore);
        editor.apply();
    }

    // Alias for backwards compatibility
    public void setContrastScores(int rightScore, int leftScore) {
        setContrastSensitivityScores(rightScore, leftScore);
    }

    public int getContrastRightScore() {
        return prefs.getInt(KEY_CONTRAST_RIGHT_SCORE, 0);
    }

    public int getContrastLeftScore() {
        return prefs.getInt(KEY_CONTRAST_LEFT_SCORE, 0);
    }

    // =================== Visual Field Test ===================
    public void setVisualFieldCompleted(boolean completed) {
        editor.putBoolean(KEY_VISUAL_FIELD_COMPLETED, completed);
        if (completed) {
            editor.putLong(KEY_VISUAL_FIELD_COMPLETED_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public boolean isVisualFieldCompleted() {
        return prefs.getBoolean(KEY_VISUAL_FIELD_COMPLETED, false);
    }

    public void setVisualFieldScores(int rightScore, int leftScore) {
        editor.putInt(KEY_VISUAL_FIELD_RIGHT_SCORE, rightScore);
        editor.putInt(KEY_VISUAL_FIELD_LEFT_SCORE, leftScore);
        editor.apply();
    }

    public int getVisualFieldRightScore() {
        return prefs.getInt(KEY_VISUAL_FIELD_RIGHT_SCORE, 0);
    }

    public int getVisualFieldLeftScore() {
        return prefs.getInt(KEY_VISUAL_FIELD_LEFT_SCORE, 0);
    }

    // =================== Test Unlock Logic ===================
    public boolean isTestUnlocked(int testNumber) {
        switch (testNumber) {
            case 1: // Distance Vision - always unlocked
                return true;
            case 2: // Near Vision - unlocked after Distance Vision
                return isDistanceVisionCompleted();
            case 3: // Color Vision - unlocked after Near Vision
                return isNearVisionCompleted();
            case 4: // Astigmatism - unlocked after Color Vision
                return isColorVisionCompleted();
            case 5: // Contrast Sensitivity - unlocked after Astigmatism
                return isAstigmatismCompleted();
            case 6: // Visual Field - unlocked after Contrast Sensitivity
                return isContrastSensitivityCompleted();
            default:
                return false;
        }
    }

    public boolean isTestCompleted(int testNumber) {
        switch (testNumber) {
            case 1:
                return isDistanceVisionCompleted();
            case 2:
                return isNearVisionCompleted();
            case 3:
                return isColorVisionCompleted();
            case 4:
                return isAstigmatismCompleted();
            case 5:
                return isContrastSensitivityCompleted();
            case 6:
                return isVisualFieldCompleted();
            default:
                return false;
        }
    }

    // =================== Count Completed Tests ===================
    public int getCompletedTestCount() {
        int count = 0;
        if (isDistanceVisionCompleted())
            count++;
        if (isNearVisionCompleted())
            count++;
        if (isColorVisionCompleted())
            count++;
        if (isAstigmatismCompleted())
            count++;
        if (isContrastSensitivityCompleted())
            count++;
        if (isVisualFieldCompleted())
            count++;
        return count;
    }

    public boolean areAllTestsCompleted() {
        return getCompletedTestCount() == 6;
    }

    // =================== Recent Activity ===================
    public List<String> getRecentCompletedTests() {
        List<TestWithTime> testsWithTime = new ArrayList<>();

        if (isDistanceVisionCompleted()) {
            testsWithTime.add(new TestWithTime("Distance Vision Test",
                    prefs.getLong(KEY_DISTANCE_COMPLETED_TIME, 0)));
        }
        if (isNearVisionCompleted()) {
            testsWithTime.add(new TestWithTime("Near Vision Test",
                    prefs.getLong(KEY_NEAR_COMPLETED_TIME, 0)));
        }
        if (isColorVisionCompleted()) {
            testsWithTime.add(new TestWithTime("Color Vision Test",
                    prefs.getLong(KEY_COLOR_COMPLETED_TIME, 0)));
        }
        if (isAstigmatismCompleted()) {
            testsWithTime.add(new TestWithTime("Astigmatism Test",
                    prefs.getLong(KEY_ASTIGMATISM_COMPLETED_TIME, 0)));
        }
        if (isContrastSensitivityCompleted()) {
            testsWithTime.add(new TestWithTime("Contrast Sensitivity Test",
                    prefs.getLong(KEY_CONTRAST_COMPLETED_TIME, 0)));
        }
        if (isVisualFieldCompleted()) {
            testsWithTime.add(new TestWithTime("Visual Field Test",
                    prefs.getLong(KEY_VISUAL_FIELD_COMPLETED_TIME, 0)));
        }

        // Sort by time (most recent first)
        testsWithTime.sort((a, b) -> Long.compare(b.time, a.time));

        // Return test names in order
        List<String> result = new ArrayList<>();
        for (TestWithTime test : testsWithTime) {
            result.add(test.name);
        }
        return result;
    }

    private static class TestWithTime {
        String name;
        long time;

        TestWithTime(String name, long time) {
            this.name = name;
            this.time = time;
        }
    }

    // =================== Sync From Database ===================
    /**
     * Sync test completion status from database results.
     * Call this when switching profiles or on fragment resume.
     * 
     * @param testResults JSONArray of test results from getTestResults API
     */
    public void syncFromDatabase(JSONArray testResults) {
        // Reset all tests first
        resetAllTests();

        if (testResults == null)
            return;

        // Mark tests as completed based on database results
        for (int i = 0; i < testResults.length(); i++) {
            try {
                JSONObject result = testResults.getJSONObject(i);
                int testId = result.optInt("test_id", -1);
                double rightScore = result.optDouble("right_eye_score", 0);
                double leftScore = result.optDouble("left_eye_score", 0);

                // Mark corresponding test as completed with scores
                switch (testId) {
                    case 1: // Distance Vision
                        setDistanceVisionCompleted(true);
                        setDistanceVisionScores((int) rightScore, (int) leftScore);
                        break;
                    case 2: // Near Vision
                        setNearVisionCompleted(true);
                        setNearVisionScores((int) rightScore, (int) leftScore);
                        break;
                    case 3: // Color Vision
                        setColorVisionCompleted(true);
                        setColorVisionScore((int) rightScore);
                        break;
                    case 4: // Astigmatism
                        setAstigmatismCompleted(true);
                        setAstigmatismScores((int) rightScore, (int) leftScore);
                        break;
                    case 5: // Contrast Sensitivity
                        setContrastSensitivityCompleted(true);
                        setContrastSensitivityScores((int) rightScore, (int) leftScore);
                        break;
                    case 6: // Visual Field
                        setVisualFieldCompleted(true);
                        setVisualFieldScores((int) rightScore, (int) leftScore);
                        break;
                }
            } catch (Exception e) {
                // Skip this result if parsing fails
            }
        }
    }

    // =================== Reset All Tests ===================
    public void resetAllTests() {
        editor.clear();
        editor.apply();
    }
}
