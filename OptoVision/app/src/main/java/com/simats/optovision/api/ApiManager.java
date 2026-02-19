package com.simats.optovision.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Singleton class for handling all API requests using Volley
 */
public class ApiManager {

    private static final String TAG = "ApiManager";
    private static ApiManager instance;
    private RequestQueue requestQueue;
    private static Context context;

    private ApiManager(Context ctx) {
        context = ctx.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // Callback interface for API responses
    public interface ApiCallback {
        void onSuccess(JSONObject response);

        void onError(String errorMessage);
    }

    // Generic POST request method
    public void postRequest(String url, JSONObject params, final ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error. Please try again.";
                        if (error.networkResponse != null) {
                            errorMessage = "Error: " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        Log.e(TAG, "API Error: " + url + " | " + errorMessage, error);
                        callback.onError(errorMessage);
                    }
                });

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                ApiConfig.REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request);
    }

    // ============================================
    // Authentication Methods
    // ============================================

    public void login(String email, String password, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            postRequest(ApiConfig.LOGIN, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in login: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void register(String fullName, String email, String phone,
            String password, String confirmPassword, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("full_name", fullName);
            params.put("email", email);
            params.put("phone", phone);
            params.put("password", password);
            params.put("confirm_password", confirmPassword);
            postRequest(ApiConfig.REGISTER, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in register: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void forgotPassword(String email, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            postRequest(ApiConfig.FORGOT_PASSWORD, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in forgotPassword: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void verifyOtp(String email, String otp, String type, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("otp", otp);
            params.put("type", type);
            postRequest(ApiConfig.VERIFY_OTP, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in verifyOtp: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void resetPassword(int userId, String newPassword, String confirmPassword, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("new_password", newPassword);
            params.put("confirm_password", confirmPassword);
            postRequest(ApiConfig.RESET_PASSWORD, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in resetPassword: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Profile Methods
    // ============================================

    public void getProfiles(int userId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            postRequest(ApiConfig.GET_PROFILES, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getProfiles: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void addProfile(int userId, String name, int age, String gender,
            String email, String phone, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("name", name);
            params.put("age", age);
            params.put("gender", gender);
            params.put("email", email);
            params.put("phone", phone);
            postRequest(ApiConfig.ADD_PROFILE, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in addProfile: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void saveVisionDetails(int profileId, int age, String wearGlasses,
            String lastExamDate, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("age", age);
            params.put("wear_glasses", wearGlasses);
            params.put("last_exam_date", lastExamDate);
            postRequest(ApiConfig.SAVE_VISION_DETAILS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in saveVisionDetails: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void saveVisionDetailsWithEyePower(int profileId, int age, String wearGlasses,
            String lastExamDate, String rightEyePower, String leftEyePower, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("age", age);
            params.put("wear_glasses", wearGlasses);
            params.put("last_exam_date", lastExamDate);
            params.put("right_eye_power", rightEyePower);
            params.put("left_eye_power", leftEyePower);
            postRequest(ApiConfig.SAVE_VISION_DETAILS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in saveVisionDetailsWithEyePower: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getProfile(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_PROFILE, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getProfile: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getProfilePicture(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_PROFILE_PICTURE, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getProfilePicture: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void updateProfile(int profileId, String name, int age, String gender,
            String email, String phone, String dateOfBirth, String streetAddress,
            String city, String country, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("name", name);
            params.put("age", age);
            params.put("gender", gender);
            params.put("email", email);
            params.put("phone", phone);
            params.put("date_of_birth", dateOfBirth);
            params.put("street_address", streetAddress);
            params.put("city", city);
            params.put("country", country);
            postRequest(ApiConfig.UPDATE_PROFILE, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in updateProfile: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void changePassword(int userId, String currentPassword, String newPassword,
            String confirmPassword, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("current_password", currentPassword);
            params.put("new_password", newPassword);
            params.put("confirm_password", confirmPassword);
            postRequest(ApiConfig.CHANGE_PASSWORD, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in changePassword: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Test Result Methods
    // ============================================

    public void saveTestResult(int profileId, int testId, double rightEyeScore,
            double leftEyeScore, String status, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("test_id", testId);
            params.put("right_eye_score", rightEyeScore);
            params.put("left_eye_score", leftEyeScore);
            params.put("status", status);
            postRequest(ApiConfig.SAVE_TEST_RESULT, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in saveTestResult: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getTestResults(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_TEST_RESULTS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getTestResults: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // Get test sessions (grouped by date - for History tab)
    public void getTestSessions(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_TEST_SESSIONS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getTestSessions: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getRecentActivity(int profileId, int limit, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("limit", limit);
            postRequest(ApiConfig.GET_RECENT_ACTIVITY, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getRecentActivity: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void clearTestResults(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.CLEAR_TEST_RESULTS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in clearTestResults: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Exercise Methods
    // ============================================

    public void logExercise(int profileId, int exerciseId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("exercise_id", exerciseId);
            postRequest(ApiConfig.LOG_EXERCISE, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in logExercise: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Game Methods
    // ============================================

    public void logGame(int profileId, int gameId, int score, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            params.put("game_id", gameId);
            params.put("score", score);
            postRequest(ApiConfig.LOG_GAME, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in logGame: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Report Methods
    // ============================================

    public void generateReport(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GENERATE_REPORT, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in generateReport: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getReports(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_REPORTS, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getReports: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getReportById(int reportId, int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("report_id", reportId);
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_REPORT_BY_ID, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getReportById: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    public void getLatestReport(int profileId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("profile_id", profileId);
            postRequest(ApiConfig.GET_LATEST_REPORT, params, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error in getLatestReport: " + e.getMessage());
            callback.onError("Error creating request");
        }
    }

    // ============================================
    // Utility Methods
    // ============================================

    public void seedDatabase(ApiCallback callback) {
        JSONObject params = new JSONObject();
        postRequest(ApiConfig.SEED_DATA, params, callback);
    }
}
