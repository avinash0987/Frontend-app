package com.simats.optovision;

import android.graphics.Color;
import com.simats.optovision.utils.DialogUtils;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simats.optovision.api.ApiManager;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryVisionGameActivity extends AppCompatActivity {

    private static final int GAME_ID = 3;
    private static final long GAME_DURATION = 30000; // 30 seconds
    private static final int GRID_SIZE = 4;
    private static final int TOTAL_CARDS = 16;
    private static final int TOTAL_PAIRS = 8;

    // Emojis for the memory cards (8 pairs)
    private static final String[] CARD_SYMBOLS = {
            "👁️", "🌈", "🎯", "⭐",
            "🌸", "🍀", "🔮", "💎"
    };

    private View introScreen;
    private View gameScreen;
    private GridLayout gridLayout;
    private TextView tvScore;
    private TextView tvMoves;
    private TextView tvTime;

    private CardView[] cardViews = new CardView[TOTAL_CARDS];
    private TextView[] cardTexts = new TextView[TOTAL_CARDS];
    private String[] cardSymbols = new String[TOTAL_CARDS];
    private boolean[] cardRevealed = new boolean[TOTAL_CARDS];
    private boolean[] cardMatched = new boolean[TOTAL_CARDS];

    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private boolean isProcessing = false;

    private int score = 0;
    private int moves = 0;
    private int matchedPairs = 0;
    private CountDownTimer countDownTimer;
    private boolean gameRunning = false;
    private long timeRemaining = GAME_DURATION;

    private SessionManager sessionManager;
    private GameCompletionManager gameCompletionManager;
    private ApiManager apiManager;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_vision_game);

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
        gridLayout = findViewById(R.id.gridLayout);
        tvScore = findViewById(R.id.tvScore);
        tvMoves = findViewById(R.id.tvMoves);
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
    }

    private void startGame() {
        // Switch to game screen
        introScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);

        // Reset game state
        score = 0;
        moves = 0;
        matchedPairs = 0;
        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;

        tvScore.setText("0");
        tvMoves.setText("0");
        tvTime.setText("30s");

        // Initialize cards
        initializeCards();
        gameRunning = true;

        // Start countdown timer
        countDownTimer = new CountDownTimer(GAME_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000);
                tvTime.setText(seconds + "s");
            }

            @Override
            public void onFinish() {
                tvTime.setText("0s");
                endGame(false);
            }
        }.start();
    }

    private void initializeCards() {
        gridLayout.removeAllViews();

        // Create pairs of symbols
        List<String> symbols = new ArrayList<>();
        for (String symbol : CARD_SYMBOLS) {
            symbols.add(symbol);
            symbols.add(symbol); // Add twice for pairs
        }
        Collections.shuffle(symbols);

        // Get grid dimensions - account for padding and margins
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        float density = getResources().getDisplayMetrics().density;
        // Screen padding (16dp * 2) + Card padding (16dp * 2) + margins between cards
        // (8dp * 5)
        int totalPadding = (int) ((32 + 32 + 40) * density);
        int cardSize = (screenWidth - totalPadding) / GRID_SIZE;

        // Create cards
        for (int i = 0; i < TOTAL_CARDS; i++) {
            final int cardIndex = i;
            cardSymbols[i] = symbols.get(i);
            cardRevealed[i] = false;
            cardMatched[i] = false;

            // Create CardView
            CardView cardView = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardSize;
            params.height = cardSize;
            params.setMargins(4, 4, 4, 4);
            cardView.setLayoutParams(params);
            cardView.setRadius(12 * getResources().getDisplayMetrics().density);
            cardView.setCardElevation(4 * getResources().getDisplayMetrics().density);
            cardView.setCardBackgroundColor(Color.parseColor("#7E57C2"));

            // Create TextView for the symbol
            TextView textView = new TextView(this);
            textView.setLayoutParams(new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(24);
            textView.setText("?");
            textView.setTextColor(Color.parseColor("#5E35B1"));

            cardView.addView(textView);
            cardView.setOnClickListener(v -> onCardClicked(cardIndex));

            cardViews[i] = cardView;
            cardTexts[i] = textView;

            gridLayout.addView(cardView);
        }
    }

    private void onCardClicked(int cardIndex) {
        if (!gameRunning || isProcessing)
            return;
        if (cardRevealed[cardIndex] || cardMatched[cardIndex])
            return;

        // Reveal the card
        revealCard(cardIndex);

        if (firstCardIndex == -1) {
            // First card selected
            firstCardIndex = cardIndex;
        } else if (secondCardIndex == -1 && cardIndex != firstCardIndex) {
            // Second card selected
            secondCardIndex = cardIndex;
            moves++;
            tvMoves.setText(String.valueOf(moves));
            isProcessing = true;

            // Check for match
            handler.postDelayed(() -> {
                checkForMatch();
            }, 800);
        }
    }

    private void revealCard(int cardIndex) {
        cardRevealed[cardIndex] = true;
        cardViews[cardIndex].setCardBackgroundColor(Color.WHITE);
        cardTexts[cardIndex].setText(cardSymbols[cardIndex]);
        cardTexts[cardIndex].setTextSize(28);
    }

    private void hideCard(int cardIndex) {
        cardRevealed[cardIndex] = false;
        cardViews[cardIndex].setCardBackgroundColor(Color.parseColor("#7E57C2"));
        cardTexts[cardIndex].setText("?");
        cardTexts[cardIndex].setTextColor(Color.parseColor("#5E35B1"));
        cardTexts[cardIndex].setTextSize(24);
    }

    private void checkForMatch() {
        if (firstCardIndex == -1 || secondCardIndex == -1) {
            isProcessing = false;
            return;
        }

        if (cardSymbols[firstCardIndex].equals(cardSymbols[secondCardIndex])) {
            // Match found!
            cardMatched[firstCardIndex] = true;
            cardMatched[secondCardIndex] = true;
            matchedPairs++;
            score += 10; // 10 points per match
            tvScore.setText(String.valueOf(score));

            // Check if all pairs matched
            if (matchedPairs >= TOTAL_PAIRS) {
                endGame(true);
                return;
            }
        } else {
            // No match, hide cards
            hideCard(firstCardIndex);
            hideCard(secondCardIndex);
        }

        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;
    }

    private void endGame(boolean allMatched) {
        gameRunning = false;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Calculate final score based on pairs matched and time/moves
        int finalScore = score;
        if (allMatched) {
            // Bonus for completing all pairs
            int timeBonus = (int) (timeRemaining / 1000);
            finalScore += timeBonus;
        }

        // Save score locally
        gameCompletionManager.setGameCompleted(GAME_ID, finalScore);

        // Save score to database
        int profileId = sessionManager.getSelectedProfileId();
        if (profileId > 0) {
            apiManager.logGame(profileId, GAME_ID, finalScore, new ApiManager.ApiCallback() {
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
        showResultDialog(allMatched, finalScore);
    }

    private void showResultDialog(boolean allMatched, int finalScore) {
        String title;
        String message;
        String rating;

        if (allMatched) {
            title = "🎉 Congratulations!";
            rating = "Excellent! 🌟";
            int timeTaken = 30 - (int) (timeRemaining / 1000);
            message = "You matched all pairs in " + timeTaken + " seconds!\n" +
                    "Moves: " + moves + "\n" +
                    "Score: " + finalScore;
        } else {
            title = "⏰ Time's Up!";
            if (matchedPairs >= 6) {
                rating = "Great! 👏";
            } else if (matchedPairs >= 4) {
                rating = "Good! 👍";
            } else if (matchedPairs >= 2) {
                rating = "Fair 🙂";
            } else {
                rating = "Keep Trying! 💪";
            }
            message = "Pairs matched: " + matchedPairs + " / " + TOTAL_PAIRS + "\n" +
                    "Moves: " + moves + "\n" +
                    "Score: " + finalScore;
        }

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message + "\n\n" + rating)
                .setPositiveButton("Done", (dialog, which) -> {
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
        handler.removeCallbacksAndMessages(null);
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
