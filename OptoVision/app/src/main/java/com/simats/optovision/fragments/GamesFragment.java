package com.simats.optovision.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.simats.optovision.ColorMatchGameActivity;
import com.simats.optovision.EyeCoordinationGameActivity;
import com.simats.optovision.FocusTrackingGameActivity;
import com.simats.optovision.MemoryVisionGameActivity;
import com.simats.optovision.R;
import com.simats.optovision.utils.GameCompletionManager;
import com.simats.optovision.utils.SessionManager;

public class GamesFragment extends Fragment {

    private GameCompletionManager gameCompletionManager;
    private SessionManager sessionManager;

    private CardView cardFocusTracking;
    private CardView cardColorMatch;
    private CardView cardMemoryVision;
    private CardView cardEyeCoordination;

    private TextView tvGamesProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        sessionManager = new SessionManager(requireContext());
        int profileId = sessionManager.getSelectedProfileId();
        gameCompletionManager = new GameCompletionManager(requireContext(), profileId);

        initViews(view);
        setupClickListeners();
        updateGameStatus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh status when returning from a game
        int profileId = sessionManager.getSelectedProfileId();
        gameCompletionManager = new GameCompletionManager(requireContext(), profileId);
        updateGameStatus();
    }

    private void initViews(View view) {
        cardFocusTracking = view.findViewById(R.id.cardFocusTracking);
        cardColorMatch = view.findViewById(R.id.cardColorMatch);
        cardMemoryVision = view.findViewById(R.id.cardMemoryVision);
        cardEyeCoordination = view.findViewById(R.id.cardEyeCoordination);
        tvGamesProgress = view.findViewById(R.id.tvGamesProgress);
    }

    private void setupClickListeners() {
        if (cardFocusTracking != null) {
            cardFocusTracking.setOnClickListener(v -> {
                // Game 1 is always unlocked
                Intent intent = new Intent(getContext(), FocusTrackingGameActivity.class);
                startActivity(intent);
            });
        }

        if (cardColorMatch != null) {
            cardColorMatch.setOnClickListener(v -> {
                if (gameCompletionManager.isGameUnlocked(2)) {
                    Intent intent = new Intent(getContext(), ColorMatchGameActivity.class);
                    startActivity(intent);
                } else {
                    showLockedMessage("Complete Focus Tracking Game first!");
                }
            });
        }

        if (cardMemoryVision != null) {
            cardMemoryVision.setOnClickListener(v -> {
                if (gameCompletionManager.isGameUnlocked(3)) {
                    Intent intent = new Intent(getContext(), MemoryVisionGameActivity.class);
                    startActivity(intent);
                } else {
                    showLockedMessage("Complete Color Match Game first!");
                }
            });
        }

        if (cardEyeCoordination != null) {
            cardEyeCoordination.setOnClickListener(v -> {
                if (gameCompletionManager.isGameUnlocked(4)) {
                    Intent intent = new Intent(getContext(), EyeCoordinationGameActivity.class);
                    startActivity(intent);
                } else {
                    showLockedMessage("Complete Memory Vision Game first!");
                }
            });
        }
    }

    private void updateGameStatus() {
        int completedCount = gameCompletionManager.getCompletedGamesCount();

        // Update progress badge
        if (tvGamesProgress != null) {
            tvGamesProgress.setText(completedCount + " of 4\ncompleted");
        }

        // Update card appearances based on completion/lock status
        updateCardStatus(cardFocusTracking, 1, true);
        updateCardStatus(cardColorMatch, 2, gameCompletionManager.isGameUnlocked(2));
        updateCardStatus(cardMemoryVision, 3, gameCompletionManager.isGameUnlocked(3));
        updateCardStatus(cardEyeCoordination, 4, gameCompletionManager.isGameUnlocked(4));
    }

    private void updateCardStatus(CardView card, int gameId, boolean isUnlocked) {
        if (card == null)
            return;

        boolean isCompleted = gameCompletionManager.isGameCompleted(gameId);

        // Adjust card alpha based on lock status
        if (!isUnlocked) {
            card.setAlpha(0.5f);
        } else {
            card.setAlpha(1.0f);
        }

        // Note: To add check/lock icons, you would need to modify the XML to include
        // ImageViews for status icons and update them here. For now, we just use alpha.
    }

    private void showLockedMessage(String message) {
        Toast.makeText(getContext(), "🔒 " + message, Toast.LENGTH_SHORT).show();
    }
}
