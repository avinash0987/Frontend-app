package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ContrastTestActivity extends AppCompatActivity {

    private TextView btnStartTest;
    private TextView tvBack;
    private boolean isLeftEyeTest = false;
    private int rightEyeScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_test);

        // Check if we're starting left eye test
        isLeftEyeTest = getIntent().getBooleanExtra("showLeftEye", false);
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);

        initViews();
        setupClickListeners();

        // Update UI for left eye if needed
        if (isLeftEyeTest) {
            btnStartTest.setText("Start with Left Eye");
        }

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });
    }

    private void initViews() {
        btnStartTest = findViewById(R.id.btnStartTest);
        tvBack = findViewById(R.id.tvBack);
    }

    private void setupClickListeners() {
        if (btnStartTest != null) {
            btnStartTest.setOnClickListener(v -> {
                Intent intent = new Intent(ContrastTestActivity.this, ContrastQuestionActivity.class);
                intent.putExtra("isRightEye", !isLeftEyeTest);
                intent.putExtra("rightEyeScore", rightEyeScore);
                startActivity(intent);
                finish();
            });
        }

        if (tvBack != null) {
            tvBack.setOnClickListener(v -> navigateBack());
        }
    }

    private void navigateBack() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
