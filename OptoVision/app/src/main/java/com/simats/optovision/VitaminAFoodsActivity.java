package com.simats.optovision;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.simats.optovision.utils.DialogUtils;
import androidx.cardview.widget.CardView;

public class VitaminAFoodsActivity extends AppCompatActivity {

    private static final String TAG = "VitaminAFoodsActivity";

    private ImageView btnBack;

    // Cards
    private CardView cardWhyItMatters, cardDailyTip, cardWhoNeeds;
    private CardView cardFoodSources, cardKeyBenefits, cardWarningSigns;

    // Food items
    private LinearLayout itemCarrots, itemSweetPotatoes, itemSpinach, itemKale;
    private LinearLayout itemPumpkin, itemMango, itemPapaya, itemRedPepper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamin_a_foods);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Cards
        cardWhyItMatters = findViewById(R.id.cardWhyItMatters);
        cardDailyTip = findViewById(R.id.cardDailyTip);
        cardWhoNeeds = findViewById(R.id.cardWhoNeeds);
        cardFoodSources = findViewById(R.id.cardFoodSources);
        cardKeyBenefits = findViewById(R.id.cardKeyBenefits);
        cardWarningSigns = findViewById(R.id.cardWarningSigns);

        // Food items
        itemCarrots = findViewById(R.id.itemCarrots);
        itemSweetPotatoes = findViewById(R.id.itemSweetPotatoes);
        itemSpinach = findViewById(R.id.itemSpinach);
        itemKale = findViewById(R.id.itemKale);
        itemPumpkin = findViewById(R.id.itemPumpkin);
        itemMango = findViewById(R.id.itemMango);
        itemPapaya = findViewById(R.id.itemPapaya);
        itemRedPepper = findViewById(R.id.itemRedPepper);
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
                showFoodInfo("Why It Matters",
                        "Vitamin A is crucial for maintaining healthy vision, especially in low-light conditions.");
            }
        });

        cardDailyTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodInfo("Daily Intake Tip", "Adults need about 700-900 mcg of Vitamin A per day.");
            }
        });

        cardKeyBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodInfo("Key Benefits", "Vitamin A supports night vision, immune function, and healthy skin.");
            }
        });

        cardWarningSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodInfo("Warning", "If you experience vision problems, consult an eye care professional.");
            }
        });

        // Food item click listeners
        itemCarrots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Carrots", "835 mcg per 100g",
                        "Carrots are one of the best sources of beta-carotene, which converts to Vitamin A in the body. They help improve night vision and protect the cornea.");
            }
        });

        itemSweetPotatoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Sweet Potatoes", "709 mcg per 100g",
                        "Sweet potatoes are rich in beta-carotene and provide sustained energy. They support eye health and boost immune function.");
            }
        });

        itemSpinach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Spinach", "469 mcg per 100g",
                        "Spinach contains lutein and zeaxanthin along with Vitamin A, which help protect eyes from harmful light and reduce the risk of macular degeneration.");
            }
        });

        itemKale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Kale", "681 mcg per 100g",
                        "Kale is a superfood packed with Vitamin A, C, and K. It helps maintain healthy eyes and supports overall vision health.");
            }
        });

        itemPumpkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Pumpkin", "426 mcg per 100g",
                        "Pumpkin is rich in beta-carotene and antioxidants. It helps protect eye cells from damage and supports night vision.");
            }
        });

        itemMango.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Mango", "54 mcg per 100g",
                        "Mangoes provide a delicious way to get Vitamin A. They also contain Vitamin C and fiber for overall health.");
            }
        });

        itemPapaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Papaya", "47 mcg per 100g",
                        "Papaya is rich in antioxidants and Vitamin A. It supports eye health and helps maintain healthy skin.");
            }
        });

        itemRedPepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDetails("Red Bell Pepper", "157 mcg per 100g",
                        "Red bell peppers are excellent sources of Vitamin A and C. They help protect eyes from oxidative stress.");
            }
        });
    }

    private void showFoodInfo(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    private void showFoodDetails(String foodName, String vitaminContent, String benefits) {
        // You can replace this with a dialog or navigate to a detailed screen
        androidx.appcompat.app.AlertDialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(foodName)
                .setMessage("Vitamin A Content: " + vitaminContent + "\n\n" + benefits)
                .setPositiveButton("Got it", null)
                .show();
        DialogUtils.styleWhite(d);
    }
}
