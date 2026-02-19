package com.simats.optovision.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.simats.optovision.AgeVisionActivity;
import com.simats.optovision.ChangePasswordActivity;
import com.simats.optovision.EditProfileActivity;
import com.simats.optovision.LoginActivity;
import com.simats.optovision.ProfileSelectionActivity;

import com.simats.optovision.R;
import com.simats.optovision.api.ApiConfig;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvProfileInitials;
    private ImageView ivProfileImage;
    private FrameLayout profileImageContainer;

    private TextView btnSwitchProfile;
    private SessionManager sessionManager;
    private ApiManager apiManager;

    private Uri cameraImageUri;
    private Bitmap currentProfileBitmap;

    // Activity result launchers for camera and gallery
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register gallery picker
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            processAndUploadImage(imageUri);
                        }
                    }
                });

        // Register camera capture
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        processAndUploadImage(cameraImageUri);
                    }
                });

        // Register permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        apiManager = ApiManager.getInstance(requireContext());

        initViews(view);
        loadProfileData();
        loadProfilePicture();

        setupMenuItems(view);

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvProfileInitials = view.findViewById(R.id.tvProfileInitials);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        profileImageContainer = view.findViewById(R.id.profileImageContainer);

        btnSwitchProfile = view.findViewById(R.id.btnSwitchProfile);

        // Setup profile image click
        // Setup profile image click
        if (profileImageContainer != null) {
            profileImageContainer.setOnClickListener(v -> {
                if (currentProfileBitmap != null) {
                    showFullImageDialog();
                } else {
                    // If no image loaded yet, maybe allow adding one? Or just ignore.
                    // For now, if they want to ADD, they can go to Edit Profile or we can keep the
                    // old behavior if null.
                    // User asked to view full image, implying one exists.
                    // If we want to allow adding if explicit "user" icon is there, we can check.
                    // But simpler to just do nothing or toast if empty.
                    // Let's fallback to picker if no image is set, as that might be useful for
                    // first-time setup.
                    showImagePickerDialog();
                }
            });
        }
    }

    private void loadProfileData() {
        // Get the selected profile name
        String profileName = sessionManager.getSelectedProfileName();
        String userEmail = sessionManager.getUserEmail();

        // If no profile selected, use user name
        if (profileName == null || profileName.isEmpty()) {
            profileName = sessionManager.getUserName();
        }

        // Set defaults if empty
        if (profileName == null || profileName.isEmpty()) {
            profileName = "User";
        }
        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = "user@example.com";
        }

        tvUserName.setText(profileName);
        tvUserEmail.setText(userEmail);

        // Set initials
        String initials = getInitials(profileName);
        if (tvProfileInitials != null) {
            tvProfileInitials.setText(initials);
        }

    }

    private void loadProfilePicture() {
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId <= 0)
            return;

        JSONObject params = new JSONObject();
        try {
            params.put("profile_id", profileId);
        } catch (Exception e) {
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                ApiConfig.GET_PROFILE_PICTURE, params,
                response -> {
                    if (!isAdded())
                        return;
                    requireActivity().runOnUiThread(() -> {
                        try {
                            if (response.optBoolean("success", false)) {
                                JSONObject data = response.optJSONObject("data");
                                if (data != null) {
                                    String imageUrl = data.optString("image_url", "");
                                    if (!imageUrl.isEmpty()) {
                                        loadImageFromUrl(ApiConfig.BASE_URL + imageUrl);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading profile picture", e);
                        }
                    });
                },
                error -> Log.e(TAG, "Failed to load profile picture"));

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void loadImageFromUrl(String imageUrl) {
        // Load image in background thread
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(imageUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                if (bitmap != null && isAdded()) {
                    currentProfileBitmap = bitmap; // Store original bitmap
                    Bitmap circularBitmap = getCircularBitmap(bitmap);
                    requireActivity().runOnUiThread(() -> {
                        if (ivProfileImage != null) {
                            ivProfileImage.setImageBitmap(circularBitmap);
                            ivProfileImage.setVisibility(View.VISIBLE);
                            if (tvProfileInitials != null) {
                                tvProfileInitials.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading image from URL", e);
            }
        }).start();
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(
                Bitmap.createScaledBitmap(bitmap, size, size, true),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);
        return output;
    }

    private void showFullImageDialog() {
        if (currentProfileBitmap == null)
            return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext(),
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageBitmap(currentProfileBitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);

        // Allow clicking image to close
        imageView.setOnClickListener(v -> {
            // We need the dialog instance to dismiss.
            // Since we can't easily access it here before creation, strictly relying on
            // back button
            // or we can structure it differently.
            // But let's add a close button or just rely on system back/outside touch if not
            // fullscreen.
        });

        // Use a FrameLayout to hold the image and maybe a close button
        FrameLayout layout = new FrameLayout(requireContext());
        layout.setBackgroundColor(android.graphics.Color.BLACK);
        layout.addView(imageView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        builder.setView(layout);
        android.app.AlertDialog dialog = builder.create();

        // Allow dismissing by clicking the image
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showImagePickerDialog() {
        String[] options = { "Take Photo", "Choose from Gallery" };
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpen();
                    } else {
                        openGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        com.simats.optovision.utils.DialogUtils.styleWhite(d);
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider", photoFile);
                cameraLauncher.launch(cameraImageUri);
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PROFILE_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void processAndUploadImage(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null)
                inputStream.close();

            if (bitmap == null) {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Resize to reasonable size
            int maxSize = 512;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            if (scale < 1) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) (width * scale), (int) (height * scale), true);
            }

            // Show circular preview immediately
            currentProfileBitmap = bitmap; // Update current bitmap for full screen view
            Bitmap circularBitmap = getCircularBitmap(bitmap);
            if (ivProfileImage != null) {
                ivProfileImage.setImageBitmap(circularBitmap);
                ivProfileImage.setVisibility(View.VISIBLE);
                if (tvProfileInitials != null) {
                    tvProfileInitials.setVisibility(View.GONE);
                }
            }

            // Upload to server
            uploadProfilePicture(bitmap);

        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfilePicture(Bitmap bitmap) {
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId <= 0)
            return;

        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageData = baos.toByteArray();

        // Create multipart request
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                ApiConfig.UPLOAD_PROFILE_PICTURE,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(new String(response.data));
                        if (jsonResponse.optBoolean("success", false)) {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> Toast
                                        .makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Upload failed");
                            if (isAdded()) {
                                requireActivity().runOnUiThread(
                                        () -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing upload response", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Upload error: " + error.getMessage());
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> Toast
                                .makeText(getContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("profile_id", String.valueOf(profileId));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("profile_image", new DataPart("profile.jpg", imageData, "image/jpeg"));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(multipartRequest);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "U";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1 && parts[0].length() >= 2) {
            return parts[0].substring(0, 2).toUpperCase();
        } else {
            return parts[0].substring(0, 1).toUpperCase();
        }
    }

    private void setupMenuItems(View view) {
        // Switch Profile
        if (btnSwitchProfile != null) {
            btnSwitchProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ProfileSelectionActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }

        // Age & Vision Details
        CardView cardAgeVision = view.findViewById(R.id.cardAgeVision);
        if (cardAgeVision != null) {
            cardAgeVision.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AgeVisionActivity.class);
                intent.putExtra("profile_id", sessionManager.getSelectedProfileId());
                startActivity(intent);
            });
        }

        // Edit Profile
        CardView cardEditProfile = view.findViewById(R.id.cardEditProfile);
        if (cardEditProfile != null) {
            cardEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("profile_id", sessionManager.getSelectedProfileId());
                startActivity(intent);
            });
        }

        // Change Password
        CardView cardChangePassword = view.findViewById(R.id.cardChangePassword);
        if (cardChangePassword != null) {
            cardChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            });
        }

        // Help & FAQ
        CardView cardHelp = view.findViewById(R.id.cardHelp);
        if (cardHelp != null) {
            cardHelp.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Help & FAQ coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        // About
        CardView cardAbout = view.findViewById(R.id.cardAbout);
        if (cardAbout != null) {
            cardAbout.setOnClickListener(v -> {
                showAboutDialog();
            });
        }

        // Logout
        CardView cardLogout = view.findViewById(R.id.cardLogout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                showLogoutConfirmation();
            });
        }
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("About OptoVision")
                .setMessage(
                        "OptoVision v1.0.0\n\nA comprehensive vision testing and eye health monitoring application.\n\nDeveloped for SIMATS University.\n\n© 2026 OptoVision Team")
                .setPositiveButton("OK", null)
                .show();
        com.simats.optovision.utils.DialogUtils.styleWhite(d);
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog d2 = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Cancel", null)
                .show();
        com.simats.optovision.utils.DialogUtils.styleWhite(d2);
    }

    private void logout() {
        // Clear session using SessionManager
        sessionManager.logout();

        // Navigate to Login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        loadProfilePicture();
    }

    // ---- Inner class: Volley Multipart Request ----
    public static class VolleyMultipartRequest extends Request<NetworkResponse> {
        private final Response.Listener<NetworkResponse> mListener;
        private final Response.ErrorListener mErrorListener;
        private final String boundary = "apiclient-" + System.currentTimeMillis();
        private final String lineEnd = "\r\n";
        private final String twoHyphens = "--";

        public VolleyMultipartRequest(int method, String url,
                Response.Listener<NetworkResponse> listener,
                Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data;boundary=" + boundary;
        }

        @Override
        public byte[] getBody() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                // Add string params
                Map<String, String> params = getParams();
                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        bos.write((twoHyphens + boundary + lineEnd).getBytes());
                        bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd)
                                .getBytes());
                        bos.write((lineEnd).getBytes());
                        bos.write((entry.getValue() + lineEnd).getBytes());
                    }
                }

                // Add file data
                Map<String, DataPart> byteData = getByteData();
                if (byteData != null) {
                    for (Map.Entry<String, DataPart> entry : byteData.entrySet()) {
                        DataPart dataPart = entry.getValue();
                        bos.write((twoHyphens + boundary + lineEnd).getBytes());
                        bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey()
                                + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd).getBytes());
                        bos.write(("Content-Type: " + dataPart.getType() + lineEnd).getBytes());
                        bos.write((lineEnd).getBytes());
                        bos.write(dataPart.getContent());
                        bos.write((lineEnd).getBytes());
                    }
                }

                bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bos.toByteArray();
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        protected Map<String, String> getParams() {
            return null;
        }

        protected Map<String, DataPart> getByteData() {
            return null;
        }

        public static class DataPart {
            private final String fileName;
            private final byte[] content;
            private final String type;

            public DataPart(String name, byte[] data, String mimeType) {
                this.fileName = name;
                this.content = data;
                this.type = mimeType;
            }

            public String getFileName() {
                return fileName;
            }

            public byte[] getContent() {
                return content;
            }

            public String getType() {
                return type;
            }
        }
    }
}
