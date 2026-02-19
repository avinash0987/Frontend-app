package com.simats.optovision;

import android.app.AlertDialog;
import com.simats.optovision.utils.DialogUtils;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AgeVisionActivity extends AppCompatActivity {

    private static final String TAG = "AgeVisionActivity";
    private ImageView btnBack;
    private EditText etAge, etWearGlasses, etLastExam, etRightExam;
    private EditText etRightEyeCondition, etLeftEyeCondition;
    private Button btnCancel, btnSave;

    private int profileId;
    private Calendar selectedExamDate = Calendar.getInstance();
    private String selectedGlasses = "";
    private String selectedRightEyeCondition = "";
    private String selectedLeftEyeCondition = "";

    // Eye condition presets with display text and stored values
    private final String[] eyeConditionLabels = {
            "Normal vision (6/6)",
            "Slightly blurry (6/9)",
            "Need glasses for reading (6/12)",
            "Need glasses always (6/18)",
            "Strong prescription (6/24+)",
            "Don't know / Never tested"
    };

    private final String[] eyeConditionValues = {
            "6/6",
            "6/9",
            "6/12",
            "6/18",
            "6/24+",
            "Unknown"
    };

    private ApiManager apiManager;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_vision);

        apiManager = ApiManager.getInstance(this);
        sessionManager = new SessionManager(this);

        profileId = getIntent().getIntExtra("profile_id", -1);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etAge = findViewById(R.id.etAge);
        etWearGlasses = findViewById(R.id.etWearGlasses);
        etLastExam = findViewById(R.id.etLastExam);
        etRightExam = findViewById(R.id.etRightExam);
        etRightEyeCondition = findViewById(R.id.etRightEyeCondition);
        etLeftEyeCondition = findViewById(R.id.etLeftEyeCondition);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving details...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etWearGlasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGlassesPicker();
            }
        });
        etWearGlasses.setFocusable(false);

        etLastExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etLastExam);
            }
        });
        etLastExam.setFocusable(false);

        etRightExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etRightExam);
            }
        });
        etRightExam.setFocusable(false);

        // Eye condition pickers
        etRightEyeCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEyeConditionPicker(true);
            }
        });
        etRightEyeCondition.setFocusable(false);

        etLeftEyeCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEyeConditionPicker(false);
            }
        });
        etLeftEyeCondition.setFocusable(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVisionDetails();
            }
        });
    }
// Show a picker for glasses/contact lenses options
    private void showGlassesPicker() {
        String[] options = { "Yes, Glasses", "Yes, Contact Lenses", "Both", "No" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you wear glasses or contact lenses?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedGlasses = options[which];
                etWearGlasses.setText(selectedGlasses);
            }
        });
        AlertDialog d = builder.show();
        DialogUtils.styleWhite(d);
    }

    private void showEyeConditionPicker(final boolean isRightEye) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isRightEye ? "Right Eye Condition" : "Left Eye Condition");
        builder.setItems(eyeConditionLabels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isRightEye) {
                    selectedRightEyeCondition = eyeConditionValues[which];
                    etRightEyeCondition.setText(eyeConditionLabels[which]);
                } else {
                    selectedLeftEyeCondition = eyeConditionValues[which];
                    etLeftEyeCondition.setText(eyeConditionLabels[which]);
                }
            }
        });
        AlertDialog d2 = builder.show();
        DialogUtils.styleWhite(d2);
    }

    private void showDatePicker(final EditText targetField) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        targetField.setText(sdf.format(cal.getTime()));
                    }
                },
                selectedExamDate.get(Calendar.YEAR),
                selectedExamDate.get(Calendar.MONTH),
                selectedExamDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveVisionDetails() {
        String ageStr = etAge.getText().toString().trim();
        String lastExamDate = etLastExam.getText().toString().trim();

        int age = 0;
        try {
            if (!ageStr.isEmpty()) {
                age = Integer.parseInt(ageStr);
            }
        } catch (NumberFormatException e) {
            etAge.setError("Please enter a valid age");
            return;
        }

        if (age > 0 && (age < 1 || age > 120)) {
            etAge.setError("Please enter a valid age (1-120)");
            etAge.requestFocus();
            return;
        }

        // Get eye condition values (default to "Unknown" if not selected)
        String rightEyePower = selectedRightEyeCondition.isEmpty() ? "Unknown" : selectedRightEyeCondition;
        String leftEyePower = selectedLeftEyeCondition.isEmpty() ? "Unknown" : selectedLeftEyeCondition;

        progressDialog.show();

        apiManager.saveVisionDetailsWithEyePower(profileId, age, selectedGlasses, lastExamDate,
                rightEyePower, leftEyePower,
                new ApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            Toast.makeText(AgeVisionActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                // Go back to profile selection
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(AgeVisionActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Save Vision Details Failed: " + errorMessage);
                        Toast.makeText(AgeVisionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
