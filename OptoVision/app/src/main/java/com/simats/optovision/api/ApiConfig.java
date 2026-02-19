package com.simats.optovision.api;

/**
 * Centralized API configuration for OptoVision app
 * Change the BASE_URL to your server's IP address
 */
public class ApiConfig {

    // =============================================
    // CHANGE THIS TO YOUR SERVER IP ADDRESS
    // =============================================
    private static final String IP_ADDRESS = "10.71.234.175"; // Your local IP
    private static final String PORT = "80"; // Your Apache/XAMPP port
    private static final String FOLDER = "optovision_API"; // Folder name in htdocs

    // Base URL constructed from above settings
    public static final String BASE_URL = "http://" + IP_ADDRESS + ":" + PORT + "/" + FOLDER + "/";

    // Authentication Endpoints
    public static final String LOGIN = BASE_URL + "login.php";
    public static final String REGISTER = BASE_URL + "register.php";
    public static final String FORGOT_PASSWORD = BASE_URL + "forgot_password.php";
    public static final String VERIFY_OTP = BASE_URL + "verify_otp.php";
    public static final String RESET_PASSWORD = BASE_URL + "reset_password.php";

    // Profile Endpoints
    public static final String GET_PROFILES = BASE_URL + "get_profiles.php";
    public static final String ADD_PROFILE = BASE_URL + "add_profile.php";
    public static final String GET_PROFILE = BASE_URL + "get_profile.php";
    public static final String UPDATE_PROFILE = BASE_URL + "update_profile.php";
    public static final String UPLOAD_PROFILE_PICTURE = BASE_URL + "upload_profile_picture.php";
    public static final String GET_PROFILE_PICTURE = BASE_URL + "get_profile_picture.php";
    public static final String SAVE_VISION_DETAILS = BASE_URL + "save_vision_details.php";
    public static final String CHANGE_PASSWORD = BASE_URL + "change_password.php";

    // Test Result Endpoints
    public static final String SAVE_TEST_RESULT = BASE_URL + "save_test_result.php";
    public static final String GET_TEST_RESULTS = BASE_URL + "get_test_results.php";
    public static final String GET_TEST_SESSIONS = BASE_URL + "get_test_sessions.php";
    public static final String GET_RECENT_ACTIVITY = BASE_URL + "get_recent_activity.php";
    public static final String CLEAR_TEST_RESULTS = BASE_URL + "clear_test_results.php";

    // Exercise Endpoints
    public static final String LOG_EXERCISE = BASE_URL + "log_exercise.php";

    // Game Endpoints
    public static final String LOG_GAME = BASE_URL + "log_game.php";

    // Report Endpoints
    public static final String GENERATE_REPORT = BASE_URL + "generate_report.php";
    public static final String GET_REPORTS = BASE_URL + "get_reports.php";
    public static final String GET_REPORT_BY_ID = BASE_URL + "get_report_by_id.php";
    public static final String GET_LATEST_REPORT = BASE_URL + "get_latest_report.php";

    // Seed Data (run once)
    public static final String SEED_DATA = BASE_URL + "seed_data.php";

    // Request timeout in milliseconds
    public static final int REQUEST_TIMEOUT = 30000;

    // For easy IP address update
    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getIpAddress() {
        return IP_ADDRESS;
    }
}
