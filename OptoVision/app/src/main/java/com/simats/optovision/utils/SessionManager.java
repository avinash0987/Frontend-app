package com.simats.optovision.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session manager to store and retrieve user data
 */
public class SessionManager {

    private static final String PREF_NAME = "OptoVisionSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_SELECTED_PROFILE_ID = "selectedProfileId";
    private static final String KEY_SELECTED_PROFILE_NAME = "selectedProfileName";
    private static final String KEY_SELECTED_PROFILE_AGE = "selectedProfileAge";
    private static final String KEY_SELECTED_PROFILE_RIGHT_EYE = "selectedProfileRightEye";
    private static final String KEY_SELECTED_PROFILE_LEFT_EYE = "selectedProfileLeftEye";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Create login session
    public void createLoginSession(int userId, String userName, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    // Check if logged in
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get user ID
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // Get user name
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    // Get user email
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    // Set selected profile with full data
    public void setSelectedProfile(int profileId, String profileName, int age, String rightEye, String leftEye) {
        editor.putInt(KEY_SELECTED_PROFILE_ID, profileId);
        editor.putString(KEY_SELECTED_PROFILE_NAME, profileName);
        editor.putInt(KEY_SELECTED_PROFILE_AGE, age);
        editor.putString(KEY_SELECTED_PROFILE_RIGHT_EYE, rightEye);
        editor.putString(KEY_SELECTED_PROFILE_LEFT_EYE, leftEye);
        editor.apply();
    }

    // Get selected profile ID
    public int getSelectedProfileId() {
        return prefs.getInt(KEY_SELECTED_PROFILE_ID, -1);
    }

    // Get selected profile name
    public String getSelectedProfileName() {
        return prefs.getString(KEY_SELECTED_PROFILE_NAME, "");
    }

    // Get selected profile age
    public int getSelectedProfileAge() {
        return prefs.getInt(KEY_SELECTED_PROFILE_AGE, 0);
    }

    // Get selected profile right eye vision
    public String getSelectedProfileRightEye() {
        return prefs.getString(KEY_SELECTED_PROFILE_RIGHT_EYE, "N/A");
    }

    // Get selected profile left eye vision
    public String getSelectedProfileLeftEye() {
        return prefs.getString(KEY_SELECTED_PROFILE_LEFT_EYE, "N/A");
    }

    // Logout - clear session
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
