package com.simats.optovision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.optovision.adapters.ProfileAdapter;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.models.Profile;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileSelectionActivity extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener {

    private static final String TAG = "ProfileSelectionActivity";
    private RecyclerView rvProfiles;
    private LinearLayout btnAddProfile;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    private ApiManager apiManager;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selection);

        apiManager = ApiManager.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    private void initViews() {
        rvProfiles = findViewById(R.id.rvProfiles);
        btnAddProfile = findViewById(R.id.btnAddProfile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profiles...");
        progressDialog.setCancelable(false);
    }

    private void setupRecyclerView() {
        profileList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(profileList, this);
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(profileAdapter);
    }

    private void loadProfiles() {
        int userId = sessionManager.getUserId();

        if (userId <= 0) {
            // Not logged in, redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        progressDialog.show();

        apiManager.getProfiles(userId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean success = response.getBoolean("success");

                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray profilesArray = data.getJSONArray("profiles");

                        profileList.clear();

                        for (int i = 0; i < profilesArray.length(); i++) {
                            JSONObject p = profilesArray.getJSONObject(i);
                            String profileImage = null;
                            if (p.has("profile_image") && !p.isNull("profile_image")) {
                                profileImage = p.getString("profile_image");
                                if (profileImage.equals("null") || profileImage.isEmpty()) {
                                    profileImage = null;
                                }
                            }
                            Log.d(TAG, "Profile: " + p.getString("name") + " image: " + profileImage);
                            Profile profile = new Profile(
                                    p.getInt("profile_id"),
                                    p.getString("name"),
                                    p.getInt("age"),
                                    p.optString("right_eye", "N/A"),
                                    p.optString("left_eye", "N/A"),
                                    profileImage);
                            profileList.add(profile);
                        }

                        profileAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ProfileSelectionActivity.this,
                                response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error: " + e.getMessage());
                    Toast.makeText(ProfileSelectionActivity.this,
                            "Error loading profiles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Load Profiles Failed: " + errorMessage);
                Toast.makeText(ProfileSelectionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSelectionActivity.this, AddProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onProfileClick(Profile profile) {
        // Save selected profile with all data for app-wide use
        sessionManager.setSelectedProfile(
                profile.getId(),
                profile.getName(),
                profile.getAge(),
                profile.getRightEyeVision(),
                profile.getLeftEyeVision());

        Toast.makeText(this, "Welcome, " + profile.getName() + "!", Toast.LENGTH_SHORT).show();

        // Navigate to Dashboard with this profile's data
        Intent intent = new Intent(ProfileSelectionActivity.this, DashboardActivity.class);
        intent.putExtra("profile_selected", true);
        startActivity(intent);
        finish();
    }
}
