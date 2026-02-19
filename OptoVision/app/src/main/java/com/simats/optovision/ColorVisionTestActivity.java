package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ColorVisionTestActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView btnStartEye;

    private boolean showLeftEye = false;
    private int rightEyeScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_vision_test);

        // Check if we should show left eye instructions
        showLeftEye = getIntent().getBooleanExtra("showLeftEye", false);
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);

        initViews();
        setupClickListeners();
        updateUI();

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void initViews() {
        tvBack = findViewById(R.id.tvBack);
        btnStartEye = findViewById(R.id.btnStartRightEye);
    }

    private void setupClickListeners() {
        // Back button
        if (tvBack != null) {
            tvBack.setOnClickListener(v -> finish());
        }

        // Start button
        if (btnStartEye != null) {
            btnStartEye.setOnClickListener(v -> {
                Intent intent = new Intent(this, ColorVisionQuestionActivity.class);
                intent.putExtra("isRightEye", !showLeftEye);
                intent.putExtra("rightEyeScore", rightEyeScore);
                startActivity(intent);
                finish();
            });
        }
    }

    private void updateUI() {
        if (showLeftEye) {
            // Update button text for left eye
            if (btnStartEye != null) {
                btnStartEye.setText("Start with Left Eye");
            }
        } else {
            // Default: Right eye
            if (btnStartEye != null) {
                btnStartEye.setText("Start with Right Eye");
            }
        }
    }
}
