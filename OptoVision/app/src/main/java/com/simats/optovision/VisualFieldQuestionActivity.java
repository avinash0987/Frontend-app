package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.views.VisualFieldView;

public class VisualFieldQuestionActivity extends AppCompatActivity implements VisualFieldView.OnBlueDotTappedListener {

    // UI Elements
    private TextView tvTestingEye, tvCloseEye, tvTrialNumber;
    private VisualFieldView visualFieldView;
    private TextView btnSawIt, btnDidntSee;
    private ProgressBar progressBar;

    // Game State
    private boolean isRightEye = true;
    private int currentTrial = 1;
    private int totalTrials = 5;
    private int rightEyeScore = 0;
    private int leftEyeScore = 0;

    // Blue dot positions for each trial (0-3: top-left, top-right, bottom-left,
    // bottom-right)
    private int[] dotPositions = { 0, 1, 2, 3, 0 };

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_field_question);

        // Get which eye we're testing
        isRightEye = getIntent().getBooleanExtra("isRightEye", true);
        rightEyeScore = getIntent().getIntExtra("rightEyeScore", 0);

        initViews();
        setupClickListeners();
        loadTrial();

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
        tvTrialNumber = findViewById(R.id.tvTrialNumber);
        visualFieldView = findViewById(R.id.visualFieldView);
        btnSawIt = findViewById(R.id.btnSawIt);
        btnDidntSee = findViewById(R.id.btnDidntSee);
        progressBar = findViewById(R.id.progressBar);

        // Set listener for blue dot taps
        visualFieldView.setOnBlueDotTappedListener(this);
    }

    private void setupClickListeners() {
        btnSawIt.setOnClickListener(v -> handleSawIt());
        btnDidntSee.setOnClickListener(v -> handleDidntSee());
    }

    private void loadTrial() {
        // Update header
        if (isRightEye) {
            tvTestingEye.setText("Testing RIGHT");
            tvCloseEye.setText("Keep looking at the center • LEFT eye closed");
        } else {
            tvTestingEye.setText("Testing LEFT");
            tvCloseEye.setText("Keep looking at the center • RIGHT eye closed");
        }

        tvTrialNumber.setText("Trial " + currentTrial + " of " + totalTrials);

        // Update progress
        int progress = (currentTrial * 100) / totalTrials;
        progressBar.setProgress(progress);

        // Set the blue dot position for this trial
        int positionIndex = (currentTrial - 1) % dotPositions.length;
        visualFieldView.setBlueDotPosition(dotPositions[positionIndex]);
        visualFieldView.setShowBlueDot(true);
        visualFieldView.resetTappedState();

        // Reset button backgrounds
        resetButtonBackgrounds();

        // Enable buttons
        setButtonsEnabled(true);
    }

    @Override
    public void onBlueDotTapped() {
        // Blue dot was tapped - visual feedback is handled by the view (changes to
        // green)
        // User can now click "I Saw It" to confirm
    }

    private void handleSawIt() {
        // Check if user actually tapped the blue dot
        if (visualFieldView.isBlueDotTapped()) {
            // They saw it AND tapped it - count as success
            if (isRightEye) {
                rightEyeScore++;
            } else {
                leftEyeScore++;
            }
            btnSawIt.setBackgroundResource(R.drawable.bg_option_correct);
        } else {
            // They clicked "I Saw It" but didn't tap the dot
            Toast.makeText(this, "Tap the blue dot first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable buttons temporarily
        setButtonsEnabled(false);

        // Wait and move to next trial
        handler.postDelayed(this::moveToNextTrial, 800);
    }

    private void handleDidntSee() {
        // User didn't see the blue dot - no points
        btnDidntSee.setBackgroundResource(R.drawable.bg_option_selected);

        // Disable buttons temporarily
        setButtonsEnabled(false);

        // Wait and move to next trial
        handler.postDelayed(this::moveToNextTrial, 800);
    }

    private void moveToNextTrial() {
        if (currentTrial < totalTrials) {
            currentTrial++;
            loadTrial();
        } else {
            // Finished all trials for current eye
            onEyeTestComplete();
        }
    }

    private void resetButtonBackgrounds() {
        btnSawIt.setBackgroundResource(R.drawable.button_saw_it);
        btnDidntSee.setBackgroundResource(R.drawable.button_didnt_see);
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSawIt.setEnabled(enabled);
        btnDidntSee.setEnabled(enabled);
    }

    private void onEyeTestComplete() {
        if (isRightEye) {
            // Right eye complete, go back to instructions for left eye
            Intent intent = new Intent(this, VisualFieldTestActivity.class);
            intent.putExtra("showLeftEye", true);
            intent.putExtra("rightEyeScore", rightEyeScore);
            startActivity(intent);
            finish();
        } else {
            // Both eyes complete, go to results
            Intent intent = new Intent(this, VisualFieldResultActivity.class);
            intent.putExtra("rightEyeScore", rightEyeScore);
            intent.putExtra("leftEyeScore", leftEyeScore);
            startActivity(intent);
            finish();
        }
    }
}
