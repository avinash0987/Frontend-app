package com.simats.optovision;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.simats.optovision.utils.DialogUtils;
import androidx.cardview.widget.CardView;

public class VitaminEFoodsActivity extends AppCompatActivity {

    private static final String TAG = "VitaminEFoodsActivity";

    private ImageView btnBack;

    // Cards
    private CardView cardWhyItMatters, cardDailyTip, cardWhoBenefits;
    private CardView cardFoodSources, cardKeyBenefits, cardWarningSigns;

    // Food items
    private LinearLayout itemAlmonds, itemSunflowerSeeds, itemHazelnuts, itemSpinach;
    private LinearLayout itemAvocado, itemPeanuts, itemOliveOil, itemWheatGerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamin_e_foods);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Cards
        cardWhyItMatters = findViewById(R.id.cardWhyItMatters);
        cardDailyTip = findViewById(R.id.cardDailyTip);
        cardWhoBenefits = findViewById(R.id.cardWhoBenefits);
        cardFoodSources = findViewById(R.id.cardFoodSources);
        cardKeyBenefits = findViewById(R.id.cardKeyBenefits);
        cardWarningSigns = findViewById(R.id.cardWarningSigns);

        // Food items
        itemAlmonds = findViewById(R.id.itemAlmonds);
        itemSunflowerSeeds = findViewById(R.id.itemSunflowerSeeds);
        itemHazelnuts = findViewById(R.id.itemHazelnuts);
        itemSpinach = findViewById(R.id.itemSpinach);
        itemAvocado = findViewById(R.id.itemAvocado);
        itemPeanuts = findViewById(R.id.itemPeanuts);
        itemOliveOil = findViewById(R.id.itemOliveOil);
        itemWheatGerm = findViewById(R.id.itemWheatGerm);
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
                        "Vitamin E is a fat-soluble antioxidant that protects cell membranes in the eyes from oxidative damage.");
            }
        });

        cardDailyTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Daily Intake Tip",
                        "Adults need about 15 mg of Vitamin E per day. Nuts and seeds are the best sources.");
            }
        });

        cardKeyBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Key Benefits",
                        "Vitamin E works synergistically with Vitamin C and other antioxidants to protect your vision as you age.");
            }
        });

        cardWarningSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Warning",
                        "Vitamin E deficiency is rare but can affect nerve and muscle function, including eye muscles.");
            }
        });

        // Food item click listeners
        itemAlmonds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Almonds", "25.6 mg per 100g",
                        "Almonds are one of the best sources of Vitamin E. Just a handful provides over 100% of your daily needs and supports healthy eye aging.");
            }
        });

        itemSunflowerSeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Sunflower Seeds", "35.2 mg per 100g",
                        "Sunflower seeds are the richest common food source of Vitamin E. They're perfect as a snack or added to salads.");
            }
        });

        itemHazelnuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Hazelnuts", "15.0 mg per 100g",
                        "Hazelnuts provide a good amount of Vitamin E along with healthy fats that help with absorption of fat-soluble vitamins.");
            }
        });

        itemSpinach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Spinach", "2.0 mg per 100g",
                        "Spinach contains Vitamin E along with lutein and zeaxanthin, providing comprehensive eye protection.");
            }
        });

        itemAvocado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Avocado", "2.1 mg per 100g",
                        "Avocados provide Vitamin E with healthy fats that enhance absorption. They also support overall eye health.");
            }
        });

        itemPeanuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Peanuts", "8.3 mg per 100g",
                        "Peanuts are an affordable source of Vitamin E. They also provide resveratrol which has additional antioxidant benefits.");
            }
        });

        itemOliveOil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Olive Oil", "14.4 mg per 100g",
                        "Extra virgin olive oil is rich in Vitamin E and polyphenols. Use it for cooking and salad dressings to boost eye health.");
            }
        });

        itemWheatGerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Wheat Germ", "15.9 mg per 100g",
                        "Wheat germ is highly nutritious with excellent Vitamin E content. Add it to smoothies, cereals, or baked goods.");
            }
        });
    }

    private void showInfo(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    private void showFoodDetails(String foodName, String vitaminContent, String benefits) {
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(foodName)
                .setMessage("Vitamin E Content: " + vitaminContent + "\n\n" + benefits)
                .setPositiveButton("Got it", null)
                .show();
        DialogUtils.styleWhite(d);
    }
}
