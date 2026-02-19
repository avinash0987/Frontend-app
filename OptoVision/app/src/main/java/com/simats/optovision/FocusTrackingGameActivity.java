package com.simats.optovision;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.simats.optovision.utils.DialogUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONObject;

import java.util.Random;

public class FocusTrackingGameActivity extends AppCompatActivity {

    private static final int GAME_ID = 1;
    private static final long GAME_DURATION = 30000; // 30 seconds
    private static final int TARGET_SIZE = 60; // dp

    private View introScreen;
    private View gameScreen;
    private TextView tvScore;
    private TextView tvTime;
    private FrameLayout gameArea;
    private ImageView targetButton;
    private CardView gameAreaCard;

    private int score = 0;
    private CountDownTimer countDownTimer;
    private Random random = new Random();
    private boolean gameRunning = false;

    private SessionManager sessionManager;
    private GameCompletionManager gameCompletionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_tracking_game);

        sessionManager = new SessionManager(this);
        int profileId = sessionManager.getSelectedProfileId();
        gameCompletionManager = new GameCompletionManager(this, profileId);
        apiManager = ApiManager.getInstance(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        introScreen = findViewById(R.id.introScreen);
        gameScreen = findViewById(R.id.gameScreen);
        tvScore = findViewById(R.id.tvScore);
        tvTime = findViewById(R.id.tvTime);
        gameArea = findViewById(R.id.gameArea);
        targetButton = findViewById(R.id.targetButton);
        gameAreaCard = findViewById(R.id.gameAreaCard);
    }

    private void setupClickListeners() {
        // Back button
        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                if (gameRunning) {
                    showExitConfirmation();
                } else {
                    finish();
                }
            });
        }

        // Start game button
        TextView btnStartGame = findViewById(R.id.btnStartGame);
        if (btnStartGame != null) {
            btnStartGame.setOnClickListener(v -> startGame());
        }

        // Target button
        if (targetButton != null) {
            targetButton.setOnClickListener(v -> {
                if (gameRunning) {
                    onTargetClicked();
                }
            });
        }
    }

    private void startGame() {
        // Switch to game screen
        introScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);

        // Reset score
        score = 0;
        tvScore.setText("0");
        tvTime.setText("30s");
        gameRunning = true;

        // Position target initially
        gameArea.post(() -> moveTargetRandomly());

        // Start countdown timer
        countDownTimer = new CountDownTimer(GAME_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTime.setText(seconds + "s");
            }

            @Override
            public void onFinish() {
                tvTime.setText("0s");
                endGame();
            }
        }.start();
    }

    private void onTargetClicked() {
        if (!gameRunning)
            return;

        // Increment score
        score++;
        tvScore.setText(String.valueOf(score));

        // Move target to new random position
        moveTargetRandomly();
    }

    private void moveTargetRandomly() {
        if (gameArea == null || targetButton == null)
            return;

        int areaWidth = gameArea.getWidth();
        int areaHeight = gameArea.getHeight();

        if (areaWidth <= 0 || areaHeight <= 0)
            return;

        // Convert target size from dp to pixels
        float density = getResources().getDisplayMetrics().density;
        int targetSizePx = (int) (TARGET_SIZE * density);

        // Calculate random position within bounds
        int maxX = areaWidth - targetSizePx;
        int maxY = areaHeight - targetSizePx;

        if (maxX <= 0)
            maxX = 1;
        if (maxY <= 0)
            maxY = 1;

        int newX = random.nextInt(maxX);
        int newY = random.nextInt(maxY);

        // Move target
        targetButton.setX(newX);
        targetButton.setY(newY);
    }

    private void endGame() {
        gameRunning = false;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Save score locally
        gameCompletionManager.setGameCompleted(GAME_ID, score);

        // Save score to database
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId > 0) {
            apiManager.logGame(profileId, GAME_ID, score, new ApiManager.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Score saved successfully
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle error silently
                }
            });
        }

        // Show result dialog
        showResultDialog();
    }

    private void showResultDialog() {
        String message;
        String rating;

        if (score >= 25) {
            rating = "Excellent! 🌟";
            message = "Outstanding focus and reflexes!";
        } else if (score >= 20) {
            rating = "Great! 👏";
            message = "Very good eye-hand coordination!";
        } else if (score >= 15) {
            rating = "Good! 👍";
            message = "Nice work! Keep practicing!";
        } else if (score >= 10) {
            rating = "Fair 🙂";
            message = "You can improve with practice!";
        } else {
            rating = "Keep Trying! 💪";
            message = "Practice makes perfect!";
        }

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage("Score: " + score + " taps\n\n" + rating + "\n" + message)
                .setPositiveButton("Done", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Play Again", (dialog, which) -> {
                    // Reset and start again
                    introScreen.setVisibility(View.VISIBLE);
                    gameScreen.setVisibility(View.GONE);
                })
                .setCancelable(false)
                .show();
        DialogUtils.styleWhite(d);
    }

    private void showExitConfirmation() {
        AlertDialog d2 = new AlertDialog.Builder(this)
                .setTitle("Exit Game?")
                .setMessage("Your progress will be lost. Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish();
                })
                .setNegativeButton("Continue", null)
                .show();
        DialogUtils.styleWhite(d2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (gameRunning) {
            showExitConfirmation();
        } else {
            super.onBackPressed();
        }
    }
}
