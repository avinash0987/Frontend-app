package com.simats.optovision;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.simats.optovision.utils.DialogUtils;
import androidx.cardview.widget.CardView;

public class VitaminCFoodsActivity extends AppCompatActivity {

    private static final String TAG = "VitaminCFoodsActivity";

    private ImageView btnBack;

    // Cards
    private CardView cardWhyItMatters, cardDailyTip, cardWhoBenefits;
    private CardView cardFoodSources, cardKeyBenefits, cardWarningSigns;

    // Food items
    private LinearLayout itemOranges, itemStrawberries, itemKiwi, itemBellPeppers;
    private LinearLayout itemBroccoli, itemGuava, itemLemon, itemPapaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamin_c_foods);

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
        itemOranges = findViewById(R.id.itemOranges);
        itemStrawberries = findViewById(R.id.itemStrawberries);
        itemKiwi = findViewById(R.id.itemKiwi);
        itemBellPeppers = findViewById(R.id.itemBellPeppers);
        itemBroccoli = findViewById(R.id.itemBroccoli);
        itemGuava = findViewById(R.id.itemGuava);
        itemLemon = findViewById(R.id.itemLemon);
        itemPapaya = findViewById(R.id.itemPapaya);
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
                        "Vitamin C is a powerful antioxidant that protects your eyes from free radical damage caused by UV exposure and pollution.");
            }
        });

        cardDailyTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Daily Intake Tip",
                        "Adults need about 75-90 mg of Vitamin C per day. Fresh fruits provide the best absorption.");
            }
        });

        cardKeyBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Key Benefits",
                        "Vitamin C helps maintain collagen in the cornea and reduces the risk of cataracts and macular degeneration.");
            }
        });

        cardWarningSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("Warning",
                        "Vitamin C deficiency can lead to weakened blood vessels in eyes. Consult a healthcare professional if symptoms persist.");
            }
        });

        // Food item click listeners
        itemOranges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Oranges", "53 mg per 100g",
                        "Oranges are classic Vitamin C sources. They also contain flavonoids that improve blood flow to the eyes and help maintain healthy vision.");
            }
        });

        itemStrawberries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Strawberries", "59 mg per 100g",
                        "Strawberries are rich in antioxidants including Vitamin C. They help protect the eyes from oxidative stress and support overall eye health.");
            }
        });

        itemKiwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Kiwi", "93 mg per 100g",
                        "Kiwi contains more Vitamin C than oranges! It also has lutein and zeaxanthin which protect against age-related eye conditions.");
            }
        });

        itemBellPeppers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Bell Peppers", "128 mg per 100g",
                        "Bell peppers, especially red ones, are packed with Vitamin C and beta-carotene. They support healthy blood vessels in the eyes.");
            }
        });

        itemBroccoli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Broccoli", "89 mg per 100g",
                        "Broccoli is a nutritional powerhouse with Vitamin C, lutein, and zeaxanthin. It helps protect the retina from harmful light.");
            }
        });

        itemGuava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Guava", "228 mg per 100g",
                        "Guava has one of the highest Vitamin C contents among fruits! It provides excellent antioxidant protection for eye tissues.");
            }
        });

        itemLemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Lemon", "53 mg per 100g",
                        "Lemons are refreshing sources of Vitamin C. Add lemon to water or food to boost your daily intake and support eye health.");
            }
        });

        itemPapaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Papaya", "62 mg per 100g",
                        "Papaya contains Vitamin C along with Vitamin A and E. This combination provides comprehensive protection for your eyes.");
            }
        });
    }

    private void showInfo(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    private void showFoodDetails(String foodName, String vitaminContent, String benefits) {
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(foodName)
                .setMessage("Vitamin C Content: " + vitaminContent + "\n\n" + benefits)
                .setPositiveButton("Got it", null)
                .show();
        DialogUtils.styleWhite(d);
    }
}
