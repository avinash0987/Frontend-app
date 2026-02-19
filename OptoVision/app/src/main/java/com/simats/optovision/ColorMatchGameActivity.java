package com.simats.optovision;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.simats.optovision.utils.DialogUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ColorMatchGameActivity extends AppCompatActivity {

    private static final int GAME_ID = 2;
    private static final long GAME_DURATION = 30000; // 30 seconds

    // Bright, human-understandable colors
    private static final String[][] COLORS = {
            { "RED", "#E53935" },
            { "BLUE", "#2196F3" },
            { "GREEN", "#4CAF50" },
            { "TEAL", "#009688" },
            { "ORANGE", "#FF9800" },
            { "PURPLE", "#9C27B0" },
            { "PINK", "#E91E63" },
            { "CYAN", "#00BCD4" }
    };

    private View introScreen;
    private View gameScreen;
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvColorName;
    private CardView colorBox1, colorBox2, colorBox3, colorBox4;

    private int score = 0;
    private int totalAttempts = 0;
    private CountDownTimer countDownTimer;
    private Random random = new Random();
    private boolean gameRunning = false;
    private String currentTargetColor;
    private String[] currentBoxColors = new String[4];

    private SessionManager sessionManager;
    private GameCompletionManager gameCompletionManager;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_match_game);

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
        tvColorName = findViewById(R.id.tvColorName);
        colorBox1 = findViewById(R.id.colorBox1);
        colorBox2 = findViewById(R.id.colorBox2);
        colorBox3 = findViewById(R.id.colorBox3);
        colorBox4 = findViewById(R.id.colorBox4);
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

        // Color box click listeners
        View.OnClickListener colorClickListener = v -> {
            if (!gameRunning)
                return;

            int clickedIndex = -1;
            if (v == colorBox1)
                clickedIndex = 0;
            else if (v == colorBox2)
                clickedIndex = 1;
            else if (v == colorBox3)
                clickedIndex = 2;
            else if (v == colorBox4)
                clickedIndex = 3;

            if (clickedIndex >= 0) {
                onColorClicked(clickedIndex);
            }
        };

        if (colorBox1 != null)
            colorBox1.setOnClickListener(colorClickListener);
        if (colorBox2 != null)
            colorBox2.setOnClickListener(colorClickListener);
        if (colorBox3 != null)
            colorBox3.setOnClickListener(colorClickListener);
        if (colorBox4 != null)
            colorBox4.setOnClickListener(colorClickListener);
    }

    private void startGame() {
        // Switch to game screen
        introScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);

        // Reset score
        score = 0;
        totalAttempts = 0;
        tvScore.setText("0");
        tvTime.setText("30s");
        gameRunning = true;

        // Setup first round
        setupNewRound();

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

    private void setupNewRound() {
        // Select 4 random unique colors
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < COLORS.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        // Pick first 4 colors for the boxes
        for (int i = 0; i < 4; i++) {
            currentBoxColors[i] = COLORS[indices.get(i)][0];
            setBoxColor(i, COLORS[indices.get(i)][1]);
        }

        // Pick one of the 4 colors as the target
        int targetIndex = random.nextInt(4);
        currentTargetColor = currentBoxColors[targetIndex];

        // Set the color name text with matching color
        tvColorName.setText(currentTargetColor);
        tvColorName.setTextColor(Color.parseColor(COLORS[indices.get(targetIndex)][1]));
    }

    private void setBoxColor(int boxIndex, String colorHex) {
        CardView box = null;
        switch (boxIndex) {
            case 0:
                box = colorBox1;
                break;
            case 1:
                box = colorBox2;
                break;
            case 2:
                box = colorBox3;
                break;
            case 3:
                box = colorBox4;
                break;
        }
        if (box != null) {
            box.setCardBackgroundColor(Color.parseColor(colorHex));
        }
    }

    private void onColorClicked(int boxIndex) {
        if (!gameRunning)
            return;

        totalAttempts++;
        String clickedColor = currentBoxColors[boxIndex];

        if (clickedColor.equals(currentTargetColor)) {
            // Correct answer
            score++;
            tvScore.setText(String.valueOf(score));
        }
        // Wrong answer - no penalty, just move to next round

        // Setup new round
        setupNewRound();
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
        int accuracy = totalAttempts > 0 ? (score * 100 / totalAttempts) : 0;

        if (score >= 20) {
            rating = "Excellent! 🌟";
            message = "Outstanding color recognition!";
        } else if (score >= 15) {
            rating = "Great! 👏";
            message = "Very good color matching skills!";
        } else if (score >= 10) {
            rating = "Good! 👍";
            message = "Nice work! Keep practicing!";
        } else if (score >= 5) {
            rating = "Fair 🙂";
            message = "You can improve with practice!";
        } else {
            rating = "Keep Trying! 💪";
            message = "Practice makes perfect!";
        }

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage("Correct: " + score + " out of " + totalAttempts +
                        "\nAccuracy: " + accuracy + "%\n\n" + rating + "\n" + message)
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
