package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ColorVisionQuestionActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvTestingEye, tvCloseEye, tvQuestionNumber;
    private ImageView ivIshiharaPlate;
    private TextView tvLevelInfo;
    private TextView btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;

    // Game State
    private boolean isRightEye = true;
    private int currentQuestion = 1;
    private int totalQuestions = 5;
    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    // Fixed test data for each level (correct answer, drawable resource)
    private int[] correctAnswers = { 12, 8, 5, 74, 3 };
    private int[] plateImages = {
            R.drawable.ishihara_12,
            R.drawable.ishihara_8,
            R.drawable.ishihara_5,
            R.drawable.ishihara_74,
            R.drawable.ishihara_3
    };

    // Wrong options for each level (3 wrong answers each)
    private int[][] wrongOptions = {
            { 17, 21, 15 }, // For 12
            { 3, 6, 9 }, // For 8
            { 2, 7, 9 }, // For 5
            { 21, 71, 24 }, // For 74
            { 8, 6, 5 } // For 3
    };

    private int currentCorrectNumber;
    private List<Integer> currentOptions;

    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_vision_question);

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
        ivIshiharaPlate = findViewById(R.id.ivIshiharaPlate);
        tvLevelInfo = findViewById(R.id.tvLevelInfo);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnOption1.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption1.getText().toString()), btnOption1));
        btnOption2.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption2.getText().toString()), btnOption2));
        btnOption3.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption3.getText().toString()), btnOption3));
        btnOption4.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption4.getText().toString()), btnOption4));
    }

    private void loadQuestion() {
        // Update header
        if (isRightEye) {
            tvTestingEye.setText("Testing RIGHT Eye");
            tvCloseEye.setText("Close your LEFT eye");
        } else {
            tvTestingEye.setText("Testing LEFT Eye");
            tvCloseEye.setText("Close your RIGHT eye");
        }

        tvQuestionNumber.setText("Question " + currentQuestion + " of " + totalQuestions);

        // Update progress
        int progress = (currentQuestion * 100) / totalQuestions;
        progressBar.setProgress(progress);

        // Update level info
        tvLevelInfo.setText("🎨 Level " + currentQuestion + "/5: Testing color perception");

        // Get data for current question
        int levelIndex = currentQuestion - 1;
        currentCorrectNumber = correctAnswers[levelIndex];

        // Set the Ishihara plate image
        ivIshiharaPlate.setImageResource(plateImages[levelIndex]);

        // Generate options (1 correct + 3 wrong)
        currentOptions = new ArrayList<>();
        currentOptions.add(currentCorrectNumber);
        for (int wrongOption : wrongOptions[levelIndex]) {
            currentOptions.add(wrongOption);
        }
        Collections.shuffle(currentOptions);

        btnOption1.setText(String.valueOf(currentOptions.get(0)));
        btnOption2.setText(String.valueOf(currentOptions.get(1)));
        btnOption3.setText(String.valueOf(currentOptions.get(2)));
        btnOption4.setText(String.valueOf(currentOptions.get(3)));

        // Reset button backgrounds
        resetOptionBackgrounds();
    }

    private void checkAnswer(int selectedNumber, TextView selectedButton) {
        boolean isCorrect = selectedNumber == currentCorrectNumber;

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
            highlightCorrectAnswer();
        }
        selectedButton.setTextColor(getResources().getColor(android.R.color.white, getTheme()));

        // Disable all buttons temporarily
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

    private void highlightCorrectAnswer() {
        String correctStr = String.valueOf(currentCorrectNumber);
        if (btnOption1.getText().toString().equals(correctStr)) {
            btnOption1.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption1.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption2.getText().toString().equals(correctStr)) {
            btnOption2.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption2.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption3.getText().toString().equals(correctStr)) {
            btnOption3.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption3.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption4.getText().toString().equals(correctStr)) {
            btnOption4.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption4.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        }
    }

    private void resetOptionBackgrounds() {
        btnOption1.setBackgroundResource(R.drawable.bg_option_button);
        btnOption2.setBackgroundResource(R.drawable.bg_option_button);
        btnOption3.setBackgroundResource(R.drawable.bg_option_button);
        btnOption4.setBackgroundResource(R.drawable.bg_option_button);

        btnOption1.setTextColor(0xFF333333);
        btnOption2.setTextColor(0xFF333333);
        btnOption3.setTextColor(0xFF333333);
        btnOption4.setTextColor(0xFF333333);
    }

    private void setOptionsEnabled(boolean enabled) {
        btnOption1.setEnabled(enabled);
        btnOption2.setEnabled(enabled);
        btnOption3.setEnabled(enabled);
        btnOption4.setEnabled(enabled);
    }

    private void onEyeTestComplete() {
        if (isRightEye) {
            // Right eye complete, go back to instructions for left eye
            Intent intent = new Intent(this, ColorVisionTestActivity.class);
            intent.putExtra("showLeftEye", true);
            intent.putExtra("rightEyeScore", rightEyeScore);
            startActivity(intent);
            finish();
        } else {
            // Both eyes complete, go to results
            Intent intent = new Intent(this, ColorVisionResultActivity.class);
            intent.putExtra("rightEyeScore", rightEyeScore);
            intent.putExtra("leftEyeScore", leftEyeScore);
            startActivity(intent);
            finish();
        }
    }
}
