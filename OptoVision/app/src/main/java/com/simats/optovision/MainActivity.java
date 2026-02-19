package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSessionAndNavigate();
            }
        }, SPLASH_DELAY);
    }

    private void checkSessionAndNavigate() {
        SessionManager sessionManager = new SessionManager(this);
        Intent intent;

        if (sessionManager.isLoggedIn()) {
            // Check if a profile is already selected
            if (sessionManager.getSelectedProfileId() > 0) {
                // User has a profile selected, go directly to Dashboard
                intent = new Intent(MainActivity.this, DashboardActivity.class);
            } else {
                // User is logged in but no profile selected, go to Profile Selection
                intent = new Intent(MainActivity.this, ProfileSelectionActivity.class);
            }
        } else {
            // User is not logged in, navigate to Login
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close Splash Screen so user can't go back to it
    }
}