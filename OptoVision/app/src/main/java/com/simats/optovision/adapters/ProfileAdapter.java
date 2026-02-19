package com.simats.optovision.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.optovision.R;
import com.simats.optovision.api.ApiConfig;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.models.Profile;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private static final String TAG = "ProfileAdapter";
    private List<Profile> profileList;
    private OnProfileClickListener listener;
    // Executor for background image loading
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnProfileClickListener {
        void onProfileClick(Profile profile);
    }

    public ProfileAdapter(List<Profile> profileList, OnProfileClickListener listener) {
        this.profileList = profileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.bind(profile);
    }

    @Override
    public int getItemCount() {
        return profileList != null ? profileList.size() : 0;
    }

    public void updateProfiles(List<Profile> newProfiles) {
        this.profileList = newProfiles;
        notifyDataSetChanged();
    }

    public void addProfile(Profile profile) {
        profileList.add(profile);
        notifyItemInserted(profileList.size() - 1);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout profileCardContainer;
        private TextView tvProfileName;
        private TextView tvProfileAge;
        private TextView tvProfileVision;
        private ImageView ivProfileImage;
        private TextView tvProfileInitials;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCardContainer = itemView.findViewById(R.id.profileCardContainer);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            tvProfileAge = itemView.findViewById(R.id.tvProfileAge);
            tvProfileVision = itemView.findViewById(R.id.tvProfileVision);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvProfileInitials = itemView.findViewById(R.id.tvProfileInitials);
        }

        public void bind(Profile profile) {
            tvProfileName.setText(profile.getName());
            tvProfileAge.setText(profile.getAgeText());
            tvProfileVision.setText(profile.getVisionText());

            // Set initials
            tvProfileInitials.setText(profile.getInitials());

            // Reset to default state
            ivProfileImage.setVisibility(View.GONE);
            tvProfileInitials.setVisibility(View.VISIBLE);
            ivProfileImage.setTag(null); // Reset tag

            // Load profile image if available
            String profileImg = profile.getProfileImage();

            if (profileImg != null && !profileImg.isEmpty()) {
                fetchAndLoadProfileImage(profile.getId(), profileImg);
            }

            profileCardContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProfileClick(profile);
                }
            });
        }

        private void fetchAndLoadProfileImage(int profileId, String originalProfileImg) {
            // Tag the ImageView to prevent recycling issues
            // Use profileId as a stable tag base
            ivProfileImage.setTag(profileId);

            // Step 1: Use ApiManager to get the image URL
            ApiManager.getInstance(itemView.getContext()).getProfilePicture(profileId, new ApiManager.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (ivProfileImage.getTag() == null || !ivProfileImage.getTag().equals(profileId)) {
                        return; // View was recycled
                    }

                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                String imageUrl = data.optString("image_url", "");
                                if (!imageUrl.isEmpty()) {
                                    // Construct full URL
                                    String fullUrl;
                                    if (imageUrl.startsWith("http")) {
                                        fullUrl = imageUrl;
                                    } else {
                                        fullUrl = ApiConfig.BASE_URL + imageUrl;
                                    }

                                    Log.d(TAG, "Loading image from: " + fullUrl);
                                    downloadImage(fullUrl, profileId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile picture response", e);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Failed to fetch profile picture URL: " + errorMessage);
                }
            });
        }

        private void downloadImage(String urlString, int profileId) {
            executorService.execute(() -> {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    if (bitmap != null) {
                        // Create circular bitmap
                        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
                        Bitmap squareBitmap = Bitmap.createBitmap(bitmap,
                                (bitmap.getWidth() - size) / 2,
                                (bitmap.getHeight() - size) / 2,
                                size, size);

                        Bitmap circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(circularBitmap);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        BitmapShader shader = new BitmapShader(squareBitmap, Shader.TileMode.CLAMP,
                                Shader.TileMode.CLAMP);
                        paint.setShader(shader);
                        float radius = size / 2f;
                        canvas.drawCircle(radius, radius, radius, paint);

                        // Update UI on main thread
                        mainHandler.post(() -> {
                            // Check tag again to ensure view hasn't been recycled
                            if (ivProfileImage.getTag() != null && ivProfileImage.getTag().equals(profileId)) {
                                ivProfileImage.setImageBitmap(circularBitmap);
                                ivProfileImage.setVisibility(View.VISIBLE);
                                tvProfileInitials.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error downloading image: " + e.getMessage());
                }
            });
        }
    }
}
