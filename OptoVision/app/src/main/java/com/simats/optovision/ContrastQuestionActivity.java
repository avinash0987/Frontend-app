package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.views.StripedPatternView;

public class ContrastQuestionActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvTestingEye, tvCloseEye, tvQuestionNumber;
    private StripedPatternView patternView;
    private TextView btnVertical, btnHorizontal;
    private ProgressBar progressBar;

    // Game State
    private boolean isRightEye = true;
    private int currentQuestion = 1;
    private int totalQuestions = 5;
    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    // Pattern configurations for each level
    // Each level: {isVertical (0=horizontal, 1=vertical), contrastLevel (0-100)}
    private int[][] patternConfigs = {
            { 1, 100 }, // Level 1: Vertical, high contrast
            { 0, 80 }, // Level 2: Horizontal, medium-high contrast
            { 1, 60 }, // Level 3: Vertical, medium contrast
            { 0, 50 }, // Level 4: Horizontal, medium-low contrast
            { 1, 40 } // Level 5: Vertical, lower contrast
    };

    private boolean currentIsVertical;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_question);

        // Get which eye we're testing
        isRightEye = getIntent().getBooleanExtra("isRightEye", true);
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);

        initViews();
        setupClickListeners();
        loadQuestion();

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void initViews() {
        tvTestingEye = findViewById(R.id.tvTestingEye);
        tvCloseEye = findViewById(R.id.tvCloseEye);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        patternView = findViewById(R.id.patternView);
        btnVertical = findViewById(R.id.btnVertical);
        btnHorizontal = findViewById(R.id.btnHorizontal);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnVertical.setOnClickListener(v -> checkAnswer(true, btnVertical));
        btnHorizontal.setOnClickListener(v -> checkAnswer(false, btnHorizontal));
    }

    private void loadQuestion() {
        // Update header
        if (isRightEye) {
            tvTestingEye.setText("Testing RIGHT");
            tvCloseEye.setText("Close your LEFT eye");
        } else {
            tvTestingEye.setText("Testing LEFT");
            tvCloseEye.setText("Close your RIGHT eye");
        }

        tvQuestionNumber.setText("Question " + currentQuestion + " of " + totalQuestions);

        // Update progress
        int progress = (currentQuestion * 100) / totalQuestions;
        progressBar.setProgress(progress);

        // Get pattern config for current question
        int levelIndex = currentQuestion - 1;
        currentIsVertical = patternConfigs[levelIndex][0] == 1;
        float contrastLevel = patternConfigs[levelIndex][1] / 100f;

        // Set the pattern
        patternView.setVertical(currentIsVertical);
        patternView.setContrastLevel(contrastLevel);
        patternView.setStripeCount(15);

        // Reset button backgrounds
        resetButtonBackgrounds();
    }

    private void checkAnswer(boolean selectedVertical, TextView selectedButton) {
        boolean isCorrect = selectedVertical == currentIsVertical;

        // Track score
        if (isCorrect) {
            if (isRightEye) {
                rightEyeScore++;
            } else {
                leftEyeScore++;
            }
            selectedButton.setBackgroundResource(R.drawable.bg_option_correct);
        } else {
            selectedButton.setBackgroundResource(R.drawable.bg_option_wrong);
            // Highlight correct answer
            if (currentIsVertical) {
                btnVertical.setBackgroundResource(R.drawable.bg_option_correct);
            } else {
                btnHorizontal.setBackgroundResource(R.drawable.bg_option_correct);
            }
        }

        // Disable buttons temporarily
        setOptionsEnabled(false);

        // Wait and move to next question
        handler.postDelayed(() -> {
            if (currentQuestion < totalQuestions) {
                currentQuestion++;
                loadQuestion();
                setOptionsEnabled(true);
            } else {
                // Finished all questions for current eye
                onEyeTestComplete();
            }
        }, 1000);
    }

    private void resetButtonBackgrounds() {
        btnVertical.setBackgroundResource(R.drawable.button_vertical);
        btnHorizontal.setBackgroundResource(R.drawable.button_horizontal);
    }

    private void setOptionsEnabled(boolean enabled) {
        btnVertical.setEnabled(enabled);
        btnHorizontal.setEnabled(enabled);
    }

    private void onEyeTestComplete() {
        if (isRightEye) {
            // Right eye complete, go back to instructions for left eye
            Intent intent = new Intent(this, ContrastTestActivity.class);
            intent.putExtra("showLeftEye", true);
            intent.putExtra("rightEyeScore", rightEyeScore);
            startActivity(intent);
            finish();
        } else {
            // Both eyes complete, go to results
            Intent intent = new Intent(this, ContrastResultActivity.class);
            intent.putExtra("rightEyeScore", rightEyeScore);
            intent.putExtra("leftEyeScore", leftEyeScore);
            startActivity(intent);
            finish();
        }
    }
}
