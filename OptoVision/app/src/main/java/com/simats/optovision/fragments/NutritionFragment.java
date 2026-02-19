package com.simats.optovision.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.simats.optovision.R;

public class NutritionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);

        // Set up click listeners for nutrition cards
        setupNavigation(view, R.id.cardVitaminA, com.simats.optovision.VitaminAFoodsActivity.class);
        setupNavigation(view, R.id.cardVitaminC, com.simats.optovision.VitaminCFoodsActivity.class);
        setupNavigation(view, R.id.cardVitaminE, com.simats.optovision.VitaminEFoodsActivity.class);
        setupNavigation(view, R.id.cardOmega3, com.simats.optovision.Omega3FoodsActivity.class);
        setupNavigation(view, R.id.cardDietTips, com.simats.optovision.DailyDietTipsActivity.class);

        return view;
    }

    private void setupNavigation(View view, int cardId, Class<?> activityClass) {
        CardView card = view.findViewById(cardId);
        if (card != null) {
            card.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getActivity(), activityClass);
                startActivity(intent);
            });
        }
    }
}
