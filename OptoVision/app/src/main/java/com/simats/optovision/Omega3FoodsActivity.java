package com.simats.optovision;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.simats.optovision.utils.DialogUtils;
import androidx.cardview.widget.CardView;

public class Omega3FoodsActivity extends AppCompatActivity {

    private static final String TAG = "Omega3FoodsActivity";

    private ImageView btnBack;

    // Cards
    private CardView cardWhyItMatters, cardDailyTip, cardWhoBenefits;
    private CardView cardFishSources, cardPlantSources, cardKeyBenefits, cardWarningSigns;

    // Fish sources
    private LinearLayout itemSalmon, itemMackerel, itemSardines, itemTuna;

    // Plant sources
    private LinearLayout itemFlaxseeds, itemChiaSeeds, itemWalnuts, itemHempSeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omega3_foods);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Cards
        cardWhyItMatters = findViewById(R.id.cardWhyItMatters);
        cardDailyTip = findViewById(R.id.cardDailyTip);
        cardWhoBenefits = findViewById(R.id.cardWhoBenefits);
        cardFishSources = findViewById(R.id.cardFishSources);
        cardPlantSources = findViewById(R.id.cardPlantSources);
        cardKeyBenefits = findViewById(R.id.cardKeyBenefits);
        cardWarningSigns = findViewById(R.id.cardWarningSigns);

        // Fish sources
        itemSalmon = findViewById(R.id.itemSalmon);
        itemMackerel = findViewById(R.id.itemMackerel);
        itemSardines = findViewById(R.id.itemSardines);
        itemTuna = findViewById(R.id.itemTuna);

        // Plant sources
        itemFlaxseeds = findViewById(R.id.itemFlaxseeds);
        itemChiaSeeds = findViewById(R.id.itemChiaSeeds);
        itemWalnuts = findViewById(R.id.itemWalnuts);
        itemHempSeeds = findViewById(R.id.itemHempSeeds);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Card click listeners
        cardWhyItMatters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Why It Matters",
                        "Omega-3 fatty acids are essential for maintaining the lipid layer of the tear film, preventing dry eyes.");
            }
        });

        cardDailyTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Daily Intake Tip",
                        "Aim for 250-500mg of EPA and DHA daily from fish, or 1-2 tablespoons of ALA-rich seeds.");
            }
        });

        cardKeyBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Key Benefits",
                        "Omega-3s reduce inflammation in the eyes, improve tear production, and protect the retina.");
            }
        });

        cardWarningSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Warning", "Persistent dry eye symptoms should be evaluated by an eye care professional.");
            }
        });

        // Fish source click listeners
        itemSalmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Salmon", "2.3g per 100g",
                        "Salmon is one of the best sources of EPA and DHA omega-3s. Wild-caught salmon has higher omega-3 content than farmed varieties.");
            }
        });

        itemMackerel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Mackerel", "2.6g per 100g",
                        "Mackerel is rich in omega-3s and also provides vitamin D. It's an affordable option for regular consumption.");
            }
        });

        itemSardines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Sardines", "1.5g per 100g",
                        "Sardines are small but mighty sources of omega-3s. They're also low in mercury, making them safe for frequent consumption.");
            }
        });

        itemTuna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Tuna", "1.3g per 100g",
                        "Tuna provides omega-3s along with protein. Choose skipjack or light tuna for lower mercury content.");
            }
        });

        // Plant source click listeners
        itemFlaxseeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Flaxseeds", "22.8g per 100g",
                        "Flaxseeds are the richest plant source of ALA omega-3. Grind them for better absorption and add to smoothies or oatmeal.");
            }
        });

        itemChiaSeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Chia Seeds", "17.8g per 100g",
                        "Chia seeds are versatile and packed with ALA omega-3s. They also absorb water, helping with hydration.");
            }
        });

        itemWalnuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Walnuts", "9.1g per 100g",
                        "Walnuts are a convenient snack with good omega-3 content. A handful daily supports eye health.");
            }
        });

        itemHempSeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Hemp Seeds", "8.7g per 100g",
                        "Hemp seeds have a good omega-3 to omega-6 ratio. They're also complete proteins, great for vegetarians.");
            }
        });
    }

    private void showInfo(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    private void showFoodDetails(String foodName, String omega3Content, String benefits) {
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(foodName)
                .setMessage("Omega-3 Content: " + omega3Content + "\n\n" + benefits)
                .setPositiveButton("Got it", null)
                .show();
        DialogUtils.styleWhite(d);
    }
}
