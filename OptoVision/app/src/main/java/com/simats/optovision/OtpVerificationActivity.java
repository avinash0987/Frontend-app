package com.simats.optovision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerificationActivity";
    private LinearLayout backButton;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private Button btnVerify;
    private TextView tvResend;

    private String email;
    private int userId;
    private String verificationType; // "verification" or "password_reset"

    private ApiManager apiManager;
    private ProgressDialog progressDialog;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        apiManager = ApiManager.getInstance(this);

        // Get data from intent
        email = getIntent().getStringExtra("email");
        userId = getIntent().getIntExtra("user_id", -1);
        verificationType = getIntent().getStringExtra("type");
        if (verificationType == null)
            verificationType = "verification";

        initViews();
        setupOtpInputs();
        setupListeners();
        startResendTimer();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);
    }

    private void setupOtpInputs() {
        // Auto-focus next input
        setupOtpFocus(etOtp1, null, etOtp2);
        setupOtpFocus(etOtp2, etOtp1, etOtp3);
        setupOtpFocus(etOtp3, etOtp2, etOtp4);
        setupOtpFocus(etOtp4, etOtp3, etOtp5);
        setupOtpFocus(etOtp5, etOtp4, etOtp6);
        setupOtpFocus(etOtp6, etOtp5, null);
    }

    private void setupOtpFocus(final EditText current, final EditText prev, final EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                } else if (s.length() == 0 && prev != null) {
                    prev.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getOtpCode();
                if (otp.length() == 6) {
                    verifyOtp(otp);
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter complete OTP", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
            }
        });
    }

    private String getOtpCode() {
        return etOtp1.getText().toString() +
                etOtp2.getText().toString() +
                etOtp3.getText().toString() +
                etOtp4.getText().toString() +
                etOtp5.getText().toString() +
                etOtp6.getText().toString();
    }

    private void startResendTimer() {
        tvResend.setEnabled(false);
        tvResend.setAlpha(0.5f);

        resendTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Timer is ticking - show countdown on resend text
                tvResend.setText("Resend in " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvResend.setEnabled(true);
                tvResend.setAlpha(1.0f);
                tvResend.setText("Resend");
            }
        }.start();
    }

    private void verifyOtp(String otp) {
        progressDialog.show();

        apiManager.verifyOtp(email, otp, verificationType, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {
                        Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_SHORT).show();

                        if (verificationType.equals("password_reset")) {
                            // Navigate to reset password screen
                            JSONObject data = response.getJSONObject("data");
                            int uid = data.getInt("user_id");

                            Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("user_id", uid);
                            startActivity(intent);
                            finish();
                        } else {
                            // Email verified, go to login
                            Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error in verifyOtp: " + e.getMessage());
                    Toast.makeText(OtpVerificationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "OTP Verification Failed: " + errorMessage);
                Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        apiManager.forgotPassword(email, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        Toast.makeText(OtpVerificationActivity.this, "New code sent!", Toast.LENGTH_SHORT).show();
                        startResendTimer();
                        clearOtpFields();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.getString("message"), Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error in resendOtp: " + e.getMessage());
                    Toast.makeText(OtpVerificationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Resend OTP Failed: " + errorMessage);
                Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearOtpFields() {
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp5.setText("");
        etOtp6.setText("");
        etOtp1.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}
