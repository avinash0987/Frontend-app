package com.simats.optovision;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.simats.optovision.api.ApiConfig;
import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ImageView btnBack, btnEdit;
    private EditText etFullName, etDateOfBirth, etGender;
    private EditText etEmail, etPhone;
    private EditText etStreetAddress, etCity, etCountry;
    private Button btnCancel, btnSaveChanges;

    private Calendar selectedDate = Calendar.getInstance();
    private String selectedGender = "";
    private int profileId = -1;

    private ApiManager apiManager;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    // Profile image
    private ImageView ivProfileImage;
    private TextView tvProfileInitials;
    private FrameLayout profileImageContainer;
    private Uri cameraImageUri;
    private Bitmap selectedImageBitmap;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiManager = ApiManager.getInstance(this);
        sessionManager = new SessionManager(this);

        // Get profile ID from intent
        profileId = getIntent().getIntExtra("profile_id", -1);

        registerLaunchers();
        initViews();
        setupListeners();
        loadProfileData();
    }

    private void registerLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            processSelectedImage(imageUri);
                        }
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        processSelectedImage(cameraImageUri);
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);

        // Basic Information
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etGender = findViewById(R.id.etGender);

        // Contact Information
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Address Information
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);

        // Profile image
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileInitials = findViewById(R.id.tvProfileInitials);
        profileImageContainer = findViewById(R.id.profileImageContainer);

        // Buttons
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        etDateOfBirth.setFocusable(false);

        etGender.setOnClickListener(v -> showGenderPicker());
        etGender.setFocusable(false);

        btnCancel.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProfile();
            }
        });

        if (profileImageContainer != null) {
            profileImageContainer.setOnClickListener(v -> showImagePickerDialog());
        }
    }

    // ---- Image Picker ----

    private void showImagePickerDialog() {
        String[] options = { "Take Photo", "Choose from Gallery" };
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);
                cameraLauncher.launch(cameraImageUri);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PROFILE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null)
                inputStream.close();

            if (bitmap == null) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Resize
            int maxSize = 512;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            if (scale < 1) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) (width * scale), (int) (height * scale), true);
            }

            selectedImageBitmap = bitmap;

            // Show circular preview
            Bitmap circularBitmap = getCircularBitmap(bitmap);
            ivProfileImage.setImageBitmap(circularBitmap);
            ivProfileImage.setVisibility(View.VISIBLE);
            tvProfileInitials.setVisibility(View.GONE);

            // Upload immediately
            uploadProfilePicture(profileId);

        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
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

    // ---- Load existing profile picture ----

    private void loadProfilePicture() {
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
                },
                error -> Log.e(TAG, "Failed to load profile picture"));

        Volley.newRequestQueue(this).add(request);
    }

    private void loadImageFromUrl(String imageUrl) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(imageUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                if (bitmap != null) {
                    Bitmap circularBitmap = getCircularBitmap(bitmap);
                    runOnUiThread(() -> {
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

    // ---- Date & Gender Pickers ----

    private void loadProfileData() {
        if (profileId <= 0) {
            Toast.makeText(this, "Invalid profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog.setMessage("Loading profile...");
        progressDialog.show();

        apiManager.getProfile(profileId, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        populateFields(data);
                        loadProfilePicture();
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error: " + e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Load Profile Failed: " + errorMessage);
                Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(JSONObject data) throws JSONException {
        if (data.has("name") && !data.isNull("name")) {
            etFullName.setText(data.getString("name"));
        }
        if (data.has("date_of_birth") && !data.isNull("date_of_birth")) {
            etDateOfBirth.setText(data.getString("date_of_birth"));
        }
        if (data.has("gender") && !data.isNull("gender")) {
            selectedGender = data.getString("gender");
            String displayGender = selectedGender.substring(0, 1).toUpperCase() + selectedGender.substring(1);
            etGender.setText(displayGender);
        }
        if (data.has("email") && !data.isNull("email")) {
            etEmail.setText(data.getString("email"));
        }
        if (data.has("phone") && !data.isNull("phone")) {
            etPhone.setText(data.getString("phone"));
        }
        if (data.has("street_address") && !data.isNull("street_address")) {
            etStreetAddress.setText(data.getString("street_address"));
        }
        if (data.has("city") && !data.isNull("city")) {
            etCity.setText(data.getString("city"));
        }
        if (data.has("country") && !data.isNull("country")) {
            etCountry.setText(data.getString("country"));
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDateOfBirth.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showGenderPicker() {
        String[] genders = { "Male", "Female", "Other" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        builder.setItems(genders, (dialog, which) -> {
            selectedGender = genders[which].toLowerCase();
            etGender.setText(genders[which]);
        });
        AlertDialog d2 = builder.show();
        com.simats.optovision.utils.DialogUtils.styleWhite(d2);
    }

    // ---- Validation & Save ----

    private boolean validateInputs() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Name is required");
            etFullName.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String streetAddress = etStreetAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();

        // Calculate age from DOB
        int age = 0;
        if (!dateOfBirth.isEmpty()) {
            Calendar today = Calendar.getInstance();
            age = today.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
        }
        if (age <= 0) {
            age = 25; // Default age
        }

        progressDialog.setMessage("Saving profile...");
        progressDialog.show();

        apiManager.updateProfile(profileId, name, age, selectedGender, email, phone,
                dateOfBirth, streetAddress, city, country, new ApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(EditProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Update Profile Failed: " + errorMessage);
                        Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---- Image Upload ----

    private void uploadProfilePicture(int profileId) {
        if (selectedImageBitmap == null || profileId <= 0)
            return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageData = baos.toByteArray();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                ApiConfig.UPLOAD_PROFILE_PICTURE,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(new String(response.data));
                        if (jsonResponse.optBoolean("success", false)) {
                            runOnUiThread(() -> Toast.makeText(this,
                                    "Profile picture updated!", Toast.LENGTH_SHORT).show());
                        } else {
                            String message = jsonResponse.optString("message", "Upload failed");
                            runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing upload response", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Upload error: " + error.getMessage());
                    runOnUiThread(() -> Toast.makeText(this,
                            "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
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

        Volley.newRequestQueue(this).add(multipartRequest);
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
