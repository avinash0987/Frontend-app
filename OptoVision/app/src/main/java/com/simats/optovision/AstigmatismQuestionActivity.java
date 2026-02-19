package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.views.AnalogClockView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AstigmatismQuestionActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvTestingEye, tvCloseEye, tvQuestionNumber;
    private AnalogClockView clockView;
    private TextView tvLevelInfo;
    private TextView btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;

    // Game State
    private boolean isRightEye = true;
    private int currentQuestion = 1;
    private int totalQuestions = 5;
    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    // Fixed times for each level (hour, minute)
    private int[][] clockTimes = {
            { 3, 0 }, // 3:00
            { 6, 30 }, // 6:30
            { 9, 15 }, // 9:15
            { 12, 0 }, // 12:00
            { 7, 45 } // 7:45
    };

    private String currentCorrectTime;
    private List<String> currentOptions;

    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astigmatism_question);

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
        clockView = findViewById(R.id.clockView);
        tvLevelInfo = findViewById(R.id.tvLevelInfo);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1.getText().toString(), btnOption1));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2.getText().toString(), btnOption2));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3.getText().toString(), btnOption3));
        btnOption4.setOnClickListener(v -> checkAnswer(btnOption4.getText().toString(), btnOption4));
    }

    private void loadQuestion() {
        // Update header
        if (isRightEye) {
            tvTestingEye.setText("👁 RIGHT");
            tvCloseEye.setText("Close your LEFT eye");
        } else {
            tvTestingEye.setText("👁 LEFT");
            tvCloseEye.setText("Close your RIGHT eye");
        }

        tvQuestionNumber.setText("Question " + currentQuestion + " of " + totalQuestions);

        // Update progress
        int progress = (currentQuestion * 100) / totalQuestions;
        progressBar.setProgress(progress);

        // Update level info
        tvLevelInfo.setText("👁 Level " + currentQuestion + "/5: Clock reading test for astigmatism");

        // Get time for current question
        int levelIndex = currentQuestion - 1;
        int hour = clockTimes[levelIndex][0];
        int minute = clockTimes[levelIndex][1];

        // Set the clock
        clockView.setTime(hour, minute);

        // Format correct answer
        currentCorrectTime = formatTime(hour, minute);

        // Generate options (1 correct + 3 wrong)
        currentOptions = generateTimeOptions(hour, minute);

        btnOption1.setText(currentOptions.get(0));
        btnOption2.setText(currentOptions.get(1));
        btnOption3.setText(currentOptions.get(2));
        btnOption4.setText(currentOptions.get(3));

        // Reset button backgrounds
        resetOptionBackgrounds();
    }

    private String formatTime(int hour, int minute) {
        if (hour == 0)
            hour = 12;
        return String.format("%d:%02d", hour, minute);
    }

    private List<String> generateTimeOptions(int correctHour, int correctMinute) {
        List<String> options = new ArrayList<>();
        String correctTime = formatTime(correctHour, correctMinute);
        options.add(correctTime);

        // Generate 3 wrong options (similar times)
        int[] minuteVariations = { 0, 15, 30, 45 };

        while (options.size() < 4) {
            int wrongHour = correctHour + random.nextInt(3) - 1;
            if (wrongHour <= 0)
                wrongHour = 12;
            if (wrongHour > 12)
                wrongHour = 1;

            int wrongMinute = minuteVariations[random.nextInt(4)];
            String wrongTime = formatTime(wrongHour, wrongMinute);

            if (!options.contains(wrongTime)) {
                options.add(wrongTime);
            }
        }

        Collections.shuffle(options);
        return options;
    }

    private void checkAnswer(String selectedTime, TextView selectedButton) {
        boolean isCorrect = selectedTime.equals(currentCorrectTime);

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
        if (btnOption1.getText().toString().equals(currentCorrectTime)) {
            btnOption1.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption1.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption2.getText().toString().equals(currentCorrectTime)) {
            btnOption2.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption2.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption3.getText().toString().equals(currentCorrectTime)) {
            btnOption3.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption3.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption4.getText().toString().equals(currentCorrectTime)) {
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
            Intent intent = new Intent(this, AstigmatismTestActivity.class);
            intent.putExtra("showLeftEye", true);
            intent.putExtra("rightEyeScore", rightEyeScore);
            startActivity(intent);
            finish();
        } else {
            // Both eyes complete, go to results
            Intent intent = new Intent(this, AstigmatismResultActivity.class);
            intent.putExtra("rightEyeScore", rightEyeScore);
            intent.putExtra("leftEyeScore", leftEyeScore);
            startActivity(intent);
            finish();
        }
    }
}
