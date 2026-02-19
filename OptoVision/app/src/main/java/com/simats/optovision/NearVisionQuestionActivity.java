package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NearVisionQuestionActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvTestingEye, tvCloseEye, tvQuestionNumber;
    private TextView tvWord, tvLevelInfo;
    private TextView btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;

    // Game State
    private boolean isRightEye = true;
    private int currentQuestion = 1;
    private int totalQuestions = 5;
    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    // 30 Word sets with similar looking alternatives (randomly selected for each
    // level)
    private String[][] allWordSets = {
            { "READING", "LEADING", "HEADING", "BEADING" },
            { "VISION", "FUSION", "LESION", "LEGION" },
            { "CLARITY", "CHARITY", "RARITY", "PARITY" },
            { "FOCUS", "LOCUS", "BOGUS", "LOTUS" },
            { "SIGHT", "LIGHT", "FIGHT", "NIGHT" },
            { "BRAIN", "DRAIN", "TRAIN", "GRAIN" },
            { "CLEAR", "CLEAN", "CREAM", "GLEAM" },
            { "SHARP", "SHARK", "SHARE", "SHAPE" },
            { "BRIGHT", "FRIGHT", "FLIGHT", "SLIGHT" },
            { "LETTER", "BETTER", "SETTER", "GETTER" },
            { "CLOSE", "CHOSE", "CLONE", "THOSE" },
            { "OPTIC", "TOPIC", "LOGIC", "MAGIC" },
            { "CHART", "CHANT", "CHARM", "CHAIR" },
            { "POINT", "JOINT", "PRINT", "PAINT" },
            { "BLIND", "BLEND", "BLOND", "BRAND" },
            { "FRAME", "FLAME", "BLAME", "SHAME" },
            { "GLARE", "FLARE", "GLAZE", "BLARE" },
            { "SCREEN", "STREAM", "SCREAM", "SCHEME" },
            { "FLASH", "CLASH", "CRASH", "SLASH" },
            { "DEPTH", "DEATH", "DEALT", "DEALT" },
            { "WATCH", "MATCH", "CATCH", "PATCH" },
            { "FLOAT", "BLOAT", "GLOAT", "TOAST" },
            { "TRACE", "BRACE", "GRACE", "PLACE" },
            { "RANGE", "CHANGE", "ORANGE", "MANGE" },
            { "SCALE", "STALE", "SHALE", "WHALE" },
            { "PLANE", "PLANT", "PLATE", "PLACE" },
            { "SPARK", "SMART", "START", "STARK" },
            { "DREAM", "CREAM", "STREAM", "GLEAM" },
            { "PRIME", "CRIME", "GRIME", "CLIMB" },
            { "SPORT", "SHORT", "SNORT", "STORM" }
    };

    // Track which word sets have been used in this session
    private List<Integer> usedWordSetIndices = new ArrayList<>();

    // Word sizes decreasing for each level
    private int[] wordSizes = { 48, 36, 28, 22, 16 }; // Decreasing sizes for levels 1-5

    private String currentCorrectAnswer;
    private List<String> currentOptions;

    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_vision_question);

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
        tvWord = findViewById(R.id.tvWord);
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

    private String[] getRandomWordSet() {
        // Find an unused word set
        int randomIndex;
        int attempts = 0;
        do {
            randomIndex = random.nextInt(allWordSets.length);
            attempts++;
            // After 50 attempts, reset used indices to avoid infinite loop
            if (attempts > 50) {
                usedWordSetIndices.clear();
            }
        } while (usedWordSetIndices.contains(randomIndex) && attempts <= 50);

        usedWordSetIndices.add(randomIndex);
        return allWordSets[randomIndex];
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
        tvLevelInfo.setText("👁 Level " + currentQuestion + "/5: Testing near vision");

        // Get a random word set for this question
        String[] currentWordSet = getRandomWordSet();
        currentCorrectAnswer = currentWordSet[0]; // First word is always correct
        tvWord.setText(currentCorrectAnswer);

        // Set word size based on level (smaller for higher levels = harder)
        tvWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, wordSizes[currentQuestion - 1]);

        // Shuffle options
        currentOptions = new ArrayList<>();
        for (String word : currentWordSet) {
            currentOptions.add(word);
        }
        Collections.shuffle(currentOptions);

        btnOption1.setText(currentOptions.get(0));
        btnOption2.setText(currentOptions.get(1));
        btnOption3.setText(currentOptions.get(2));
        btnOption4.setText(currentOptions.get(3));

        // Reset button backgrounds
        resetOptionBackgrounds();
    }

    private void checkAnswer(String selectedAnswer, TextView selectedButton) {
        boolean isCorrect = selectedAnswer.equals(currentCorrectAnswer);

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
        if (btnOption1.getText().toString().equals(currentCorrectAnswer)) {
            btnOption1.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption1.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption2.getText().toString().equals(currentCorrectAnswer)) {
            btnOption2.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption2.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption3.getText().toString().equals(currentCorrectAnswer)) {
            btnOption3.setBackgroundResource(R.drawable.bg_option_correct);
            btnOption3.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else if (btnOption4.getText().toString().equals(currentCorrectAnswer)) {
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
            Intent intent = new Intent(this, NearVisionTestActivity.class);
            intent.putExtra("showLeftEye", true);
            intent.putExtra("rightEyeScore", rightEyeScore);
            startActivity(intent);
            finish();
        } else {
            // Both eyes complete, go to results
            Intent intent = new Intent(this, NearVisionResultActivity.class);
            intent.putExtra("rightEyeScore", rightEyeScore);
            intent.putExtra("leftEyeScore", leftEyeScore);
            startActivity(intent);
            finish();
        }
    }
}
