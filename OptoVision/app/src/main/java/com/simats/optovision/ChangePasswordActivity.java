package com.simats.optovision;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    private ImageView btnBack;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView btnToggleCurrentPassword, btnToggleNewPassword, btnToggleConfirmPassword;
    private TextView tvReq8Chars, tvReqUppercase, tvReqLowercase, tvReqNumber, tvReqSpecial;
    private Button btnCancel, btnUpdatePassword;

    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private ApiManager apiManager;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        apiManager = ApiManager.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnToggleCurrentPassword = findViewById(R.id.btnToggleCurrentPassword);
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        tvReq8Chars = findViewById(R.id.tvReq8Chars);
        tvReqUppercase = findViewById(R.id.tvReqUppercase);
        tvReqLowercase = findViewById(R.id.tvReqLowercase);
        tvReqNumber = findViewById(R.id.tvReqNumber);
        tvReqSpecial = findViewById(R.id.tvReqSpecial);

        btnCancel = findViewById(R.id.btnCancel);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Password visibility toggles
        btnToggleCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCurrentPasswordVisible = !isCurrentPasswordVisible;
                togglePasswordVisibility(etCurrentPassword, isCurrentPasswordVisible);
            }
        });

        btnToggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNewPasswordVisible = !isNewPasswordVisible;
                togglePasswordVisibility(etNewPassword, isNewPasswordVisible);
            }
        });

        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                togglePasswordVisibility(etConfirmPassword, isConfirmPasswordVisible);
            }
        });

        // Real-time password validation
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    changePassword();
                }
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(null);
        } else {
            editText.setTransformationMethod(new PasswordTransformationMethod());
        }
        editText.setSelection(editText.getText().length());
    }

    private void validatePasswordStrength(String password) {
        int greenColor = ContextCompat.getColor(this, android.R.color.holo_green_dark);
        int redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);

        // At least 8 characters
        if (password.length() >= 8) {
            tvReq8Chars.setText("✓  At least 8 characters");
            tvReq8Chars.setTextColor(greenColor);
        } else {
            tvReq8Chars.setText("✕  At least 8 characters");
            tvReq8Chars.setTextColor(redColor);
        }

        // One uppercase letter
        if (password.matches(".*[A-Z].*")) {
            tvReqUppercase.setText("✓  One uppercase letter");
            tvReqUppercase.setTextColor(greenColor);
        } else {
            tvReqUppercase.setText("✕  One uppercase letter");
            tvReqUppercase.setTextColor(redColor);
        }

        // One lowercase letter
        if (password.matches(".*[a-z].*")) {
            tvReqLowercase.setText("✓  One lowercase letter");
            tvReqLowercase.setTextColor(greenColor);
        } else {
            tvReqLowercase.setText("✕  One lowercase letter");
            tvReqLowercase.setTextColor(redColor);
        }

        // One number
        if (password.matches(".*[0-9].*")) {
            tvReqNumber.setText("✓  One number");
            tvReqNumber.setTextColor(greenColor);
        } else {
            tvReqNumber.setText("✕  One number");
            tvReqNumber.setTextColor(redColor);
        }

        // One special character
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            tvReqSpecial.setText("✓  One special character");
            tvReqSpecial.setTextColor(greenColor);
        } else {
            tvReqSpecial.setText("✕  One special character");
            tvReqSpecial.setTextColor(redColor);
        }
    }

    private boolean validateInputs() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        }

        // Check password strength
        if (newPassword.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            Toast.makeText(this, "Password must contain an uppercase letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[a-z].*")) {
            Toast.makeText(this, "Password must contain a lowercase letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[0-9].*")) {
            Toast.makeText(this, "Password must contain a number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            Toast.makeText(this, "Password must contain a special character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        int userId = sessionManager.getUserId();

        progressDialog.show();

        apiManager.changePassword(userId, currentPassword, newPassword, confirmPassword,
                new ApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(ChangePasswordActivity.this, "Error parsing response", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Change Password Failed: " + errorMessage);
                        Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
