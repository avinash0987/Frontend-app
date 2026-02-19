package com.simats.optovision.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Manages game completion status per profile.
 * Similar to TestCompletionManager but for games.
 */
public class GameCompletionManager {
    private static final String PREF_PREFIX = "GameCompletion_";

    private static final String KEY_FOCUS_TRACKING = "focus_tracking_completed";
    private static final String KEY_COLOR_MATCH = "color_match_completed";
    private static final String KEY_MEMORY_VISION = "memory_vision_completed";
    private static final String KEY_EYE_COORDINATION = "eye_coordination_completed";

    private static final String KEY_FOCUS_TRACKING_SCORE = "focus_tracking_score";
    private static final String KEY_COLOR_MATCH_SCORE = "color_match_score";
    private static final String KEY_MEMORY_VISION_SCORE = "memory_vision_score";
    private static final String KEY_EYE_COORDINATION_SCORE = "eye_coordination_score";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int profileId;

    public GameCompletionManager(Context context, int profileId) {
        this.profileId = profileId;
        String prefName = PREF_PREFIX + profileId;
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Game IDs: 1=Focus Tracking, 2=Color Match, 3=Memory Vision, 4=Eye
    // Coordination

    public void setGameCompleted(int gameId, int score) {
        switch (gameId) {
            case 1:
                editor.putBoolean(KEY_FOCUS_TRACKING, true);
                editor.putInt(KEY_FOCUS_TRACKING_SCORE, score);
                break;
            case 2:
                editor.putBoolean(KEY_COLOR_MATCH, true);
                editor.putInt(KEY_COLOR_MATCH_SCORE, score);
                break;
            case 3:
                editor.putBoolean(KEY_MEMORY_VISION, true);
                editor.putInt(KEY_MEMORY_VISION_SCORE, score);
                break;
            case 4:
                editor.putBoolean(KEY_EYE_COORDINATION, true);
                editor.putInt(KEY_EYE_COORDINATION_SCORE, score);
                break;
        }
        editor.apply();
    }

    public boolean isGameCompleted(int gameId) {
        switch (gameId) {
            case 1:
                return prefs.getBoolean(KEY_FOCUS_TRACKING, false);
            case 2:
                return prefs.getBoolean(KEY_COLOR_MATCH, false);
            case 3:
                return prefs.getBoolean(KEY_MEMORY_VISION, false);
            case 4:
                return prefs.getBoolean(KEY_EYE_COORDINATION, false);
            default:
                return false;
        }
    }

    public int getGameScore(int gameId) {
        switch (gameId) {
            case 1:
                return prefs.getInt(KEY_FOCUS_TRACKING_SCORE, 0);
            case 2:
                return prefs.getInt(KEY_COLOR_MATCH_SCORE, 0);
            case 3:
                return prefs.getInt(KEY_MEMORY_VISION_SCORE, 0);
            case 4:
                return prefs.getInt(KEY_EYE_COORDINATION_SCORE, 0);
            default:
                return 0;
        }
    }

    public boolean isGameUnlocked(int gameId) {
        // Game 1 is always unlocked
        if (gameId == 1)
            return true;

        // Other games unlock when previous is completed
        return isGameCompleted(gameId - 1);
    }

    public int getCompletedGamesCount() {
        int count = 0;
        if (isGameCompleted(1))
            count++;
        if (isGameCompleted(2))
            count++;
        if (isGameCompleted(3))
            count++;
        if (isGameCompleted(4))
            count++;
        return count;
    }

    public boolean areAllGamesCompleted() {
        return getCompletedGamesCount() == 4;
    }

    public void resetAllGames() {
        editor.clear();
        editor.apply();
    }

    /**
     * Sync completion status from database results
     */
    public void syncFromDatabase(JSONArray gameResults) {
        if (gameResults == null)
            return;

        try {
            for (int i = 0; i < gameResults.length(); i++) {
                JSONObject result = gameResults.getJSONObject(i);
                int gameId = result.optInt("game_id", 0);
                int score = result.optInt("score", 0);

                if (gameId >= 1 && gameId <= 4) {
                    setGameCompleted(gameId, score);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
