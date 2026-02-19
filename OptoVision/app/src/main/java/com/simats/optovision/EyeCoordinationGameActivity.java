package com.simats.optovision;

import android.content.Intent;
import com.simats.optovision.utils.DialogUtils;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONObject;

import java.util.Random;

public class EyeCoordinationGameActivity extends AppCompatActivity {

    private static final int GAME_ID = 4;
    private static final long GAME_DURATION = 30000; // 30 seconds
    private static final int TARGET_SIZE_DP = 70;

    private View introScreen;
    private View gameScreen;
    private FrameLayout gameArea;
    private View targetButton;
    private TextView tvScore;
    private TextView tvTime;

    private int score = 0;
    private CountDownTimer countDownTimer;
    private Random random = new Random();
    private boolean gameRunning = false;

    private int gameAreaWidth = 0;
    private int gameAreaHeight = 0;
    private int targetSizePx;

    private SessionManager sessionManager;
    private GameCompletionManager gameCompletionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_coordination_game);

        sessionManager = new SessionManager(this);
        int profileId = sessionManager.getSelectedProfileId();
        gameCompletionManager = new GameCompletionManager(this, profileId);
        apiManager = ApiManager.getInstance(this);

        // Convert dp to px
        float density = getResources().getDisplayMetrics().density;
        targetSizePx = (int) (TARGET_SIZE_DP * density);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        introScreen = findViewById(R.id.introScreen);
        gameScreen = findViewById(R.id.gameScreen);
        gameArea = findViewById(R.id.gameArea);
        targetButton = findViewById(R.id.targetButton);
        tvScore = findViewById(R.id.tvScore);
        tvTime = findViewById(R.id.tvTime);
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

        // Target click listener
        if (targetButton != null) {
            targetButton.setOnClickListener(v -> onTargetClicked());
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

        // Wait for layout to get dimensions
        gameArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameArea.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                gameAreaWidth = gameArea.getWidth();
                gameAreaHeight = gameArea.getHeight();

                // Start the game
                gameRunning = true;
                moveTargetRandomly();
                startTimer();
            }
        });
    }

    private void startTimer() {
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

        score++;
        tvScore.setText(String.valueOf(score));
        moveTargetRandomly();
    }

    private void moveTargetRandomly() {
        if (gameAreaWidth <= 0 || gameAreaHeight <= 0)
            return;

        // Calculate padding
        int padding = (int) (16 * getResources().getDisplayMetrics().density);

        // Calculate max positions (accounting for target size and padding)
        int maxX = gameAreaWidth - targetSizePx - (padding * 2);
        int maxY = gameAreaHeight - targetSizePx - (padding * 2);

        if (maxX <= 0)
            maxX = 1;
        if (maxY <= 0)
            maxY = 1;

        // Generate random position
        int newX = random.nextInt(maxX);
        int newY = random.nextInt(maxY);

        // Apply position
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) targetButton.getLayoutParams();
        params.leftMargin = newX;
        params.topMargin = newY;
        params.gravity = 0; // Remove gravity to use margins
        targetButton.setLayoutParams(params);
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
            message = "Outstanding eye coordination!";
        } else if (score >= 20) {
            rating = "Great! 👏";
            message = "Very good tracking skills!";
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

        // Check if all games are completed
        boolean allGamesCompleted = gameCompletionManager.areAllGamesCompleted();
        String positiveButtonText = allGamesCompleted ? "View Report" : "Done";

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage("Your Score: " + score + " taps\n\n" + rating + "\n" + message)
                .setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (allGamesCompleted) {
                        // Navigate to report generation
                        Intent intent = new Intent(this, ReportGeneratingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    finish();
                })
                .setNegativeButton("Play Again", (dialog, which) -> {
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
