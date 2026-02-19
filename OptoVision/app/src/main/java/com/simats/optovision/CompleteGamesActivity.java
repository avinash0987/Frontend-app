package com.simats.optovision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

/**
 * Activity shown after completing all 6 tests.
 * Prompts user to complete games for the final AI report.
 */
public class CompleteGamesActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private GameCompletionManager gameCompletionManager;
    private TextView tvGamesProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_games);

        initManagers();
        initViews();
        updateProgress();
    }

    private void initManagers() {
        sessionManager = new SessionManager(this);
        int profileId = sessionManager.getSelectedProfileId();
        gameCompletionManager = new GameCompletionManager(this, profileId);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnStartGames = findViewById(R.id.btnStartGames);
        TextView btnSkip = findViewById(R.id.btnSkip);
        tvGamesProgress = findViewById(R.id.tvGamesProgress);

        btnBack.setOnClickListener(v -> finish());

        btnStartGames.setOnClickListener(v -> {
            // Mark that user has started games (for this session)
            getSharedPreferences("GameFlow", MODE_PRIVATE).edit()
                    .putBoolean("started_games_" + sessionManager.getSelectedProfileId(), true)
                    .apply();

            // Navigate to games
            Intent intent = new Intent(this, GamesActivity.class);
            intent.putExtra("from_test_completion", true);
            startActivity(intent);
            finish();
        });

        btnSkip.setOnClickListener(v -> {
            // Go back to dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void updateProgress() {
        int completed = gameCompletionManager.getCompletedGamesCount();
        tvGamesProgress.setText(completed + "/4 completed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgress();

        // Only check for completed games if user has actually started playing games
        // If hasStartedGames is false, this is the first time showing the screen
        boolean hasStartedGames = getSharedPreferences("GameFlow", MODE_PRIVATE)
                .getBoolean("started_games_" + sessionManager.getSelectedProfileId(), false);

        // If all games completed AND user has actually started games, go to report
        if (hasStartedGames && gameCompletionManager.areAllGamesCompleted()) {
            // Clear the flag for next session
            getSharedPreferences("GameFlow", MODE_PRIVATE).edit()
                    .remove("started_games_" + sessionManager.getSelectedProfileId())
                    .apply();

            Intent intent = new Intent(this, ReportGeneratingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }
}
