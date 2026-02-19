package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simats.optovision.fragments.HomeFragment;
import com.simats.optovision.fragments.TestsFragment;
import com.simats.optovision.fragments.NutritionFragment;
import com.simats.optovision.fragments.HistoryFragment;
import com.simats.optovision.fragments.ProfileFragment;
import com.simats.optovision.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private BottomNavigationView bottomNavigationView;
    private int currentTabId = R.id.nav_home;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        // Check if coming from ProfileSelectionActivity (profile just selected)
        boolean profileJustSelected = getIntent().getBooleanExtra("profile_selected", false);

        // ALWAYS show profile selection on app open (unless profile was just selected)
        if (!profileJustSelected) {
            Log.d(TAG, "App opened - redirecting to Profile Selection");
            Intent intent = new Intent(this, ProfileSelectionActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        int profileId = sessionManager.getSelectedProfileId();
        Log.d(TAG, "Profile selected: " + sessionManager.getSelectedProfileName() + " (ID: " + profileId + ")");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            currentTabId = R.id.nav_home;
        }

        // Handle back press - go to Home tab first, then exit
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentTabId != R.id.nav_home) {
                    // Not on Home tab - navigate to Home first
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else {
                    // Already on Home - exit the app
                    finish();
                }
            }
        });

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_tests) {
                selectedFragment = new TestsFragment();
            } else if (itemId == R.id.nav_nutrition) {
                selectedFragment = new NutritionFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                currentTabId = itemId; // Track current tab
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // Method to switch to a specific tab programmatically
    public void switchToTab(int tabId) {
        bottomNavigationView.setSelectedItemId(tabId);
    }
}
