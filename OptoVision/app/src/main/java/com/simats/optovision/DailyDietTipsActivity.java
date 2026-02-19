package com.simats.optovision;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.simats.optovision.utils.DialogUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DailyDietTipsActivity extends AppCompatActivity {

    private static final String TAG = "DailyDietTipsActivity";

    private ImageView btnBack;

    // Cards
    private CardView cardRememberThis, card202020, cardEyeWashing, cardQualitySleep;
    private CardView cardExercise, cardStress, cardEyeProtection;
    private CardView cardColorfulFoods, cardStayHydrated, cardLimitProcessed, cardHealthyFats;
    private CardView cardDailyChecklist, cardImportantNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_diet_tips);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Cards
        cardRememberThis = findViewById(R.id.cardRememberThis);
        card202020 = findViewById(R.id.card202020);
        cardEyeWashing = findViewById(R.id.cardEyeWashing);
        cardQualitySleep = findViewById(R.id.cardQualitySleep);
        cardExercise = findViewById(R.id.cardExercise);
        cardStress = findViewById(R.id.cardStress);
        cardEyeProtection = findViewById(R.id.cardEyeProtection);
        cardColorfulFoods = findViewById(R.id.cardColorfulFoods);
        cardStayHydrated = findViewById(R.id.cardStayHydrated);
        cardLimitProcessed = findViewById(R.id.cardLimitProcessed);
        cardHealthyFats = findViewById(R.id.cardHealthyFats);
        cardDailyChecklist = findViewById(R.id.cardDailyChecklist);
        cardImportantNote = findViewById(R.id.cardImportantNote);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Healthy Eye Habits
        card202020.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("20-20-20 Rule",
                        "This simple technique helps reduce digital eye strain:\n\n" +
                                "• Every 20 minutes of screen time\n" +
                                "• Look at something 20 feet away\n" +
                                "• For at least 20 seconds\n\n" +
                                "This relaxes the focusing muscles in your eyes and helps prevent fatigue.");
            }
        });

        cardEyeWashing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Daily Eye Washing",
                        "Proper eye hygiene is essential:\n\n" +
                                "• Splash clean, cool water on closed eyes\n" +
                                "• Gently massage around eyes with wet fingers\n" +
                                "• Pat dry with a clean towel\n" +
                                "• Do this 2-3 times daily\n\n" +
                                "This removes dust, pollutants, and refreshes tired eyes.");
            }
        });

        cardQualitySleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Quality Sleep",
                        "Sleep is when your eyes heal and regenerate:\n\n" +
                                "• Aim for 7-8 hours of uninterrupted sleep\n" +
                                "• Avoid screens 1 hour before bed\n" +
                                "• Sleep in a dark room\n" +
                                "• Use blue light filters in evening\n\n" +
                                "Poor sleep can lead to dry eyes, eye spasms, and blurred vision.");
            }
        });

        // Lifestyle Support
        cardExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Regular Exercise",
                        "Physical activity benefits your eyes too:\n\n" +
                                "• Improves blood circulation to the retina\n" +
                                "• Reduces risk of glaucoma by lowering eye pressure\n" +
                                "• Helps control diabetes (prevents diabetic retinopathy)\n" +
                                "• Aim for 30 minutes of moderate exercise daily\n\n" +
                                "Walking, swimming, and cycling are excellent choices.");
            }
        });

        cardStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Stress Management",
                        "Chronic stress affects your vision:\n\n" +
                                "• Can cause eye twitching and spasms\n" +
                                "• May lead to central serous retinopathy\n" +
                                "• Practice deep breathing exercises\n" +
                                "• Try meditation or yoga\n" +
                                "• Take regular breaks during work\n\n" +
                                "Relaxed mind = healthier eyes!");
            }
        });

        cardEyeProtection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Eye Protection",
                        "Shield your eyes from environmental hazards:\n\n" +
                                "• Wear sunglasses with UV protection outdoors\n" +
                                "• Use protective eyewear during sports\n" +
                                "• Keep screens at proper distance (arm's length)\n" +
                                "• Use anti-glare screens on computers\n" +
                                "• Avoid rubbing eyes with dirty hands\n\n" +
                                "Prevention is better than cure!");
            }
        });

        // Nutrition Guidelines
        cardColorfulFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Eat Colorful Foods",
                        "Different colored foods provide different eye nutrients:\n\n" +
                                "🟠 Orange/Yellow: Carrots, sweet potatoes, mangoes - Vitamin A\n" +
                                "🟢 Green: Spinach, broccoli, kale - Lutein & Zeaxanthin\n" +
                                "🔴 Red: Tomatoes, red peppers - Lycopene\n" +
                                "🟣 Purple: Berries, grapes - Anthocyanins\n\n" +
                                "Aim for 5+ servings of colorful fruits and vegetables daily!");
            }
        });

        cardStayHydrated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Stay Hydrated",
                        "Water is essential for eye health:\n\n" +
                                "• Maintains tear film quality\n" +
                                "• Prevents dry eye syndrome\n" +
                                "• Flushes toxins from the body\n" +
                                "• Keeps eyes lubricated\n\n" +
                                "Tips:\n" +
                                "• Drink 8-10 glasses daily\n" +
                                "• Increase intake in hot weather\n" +
                                "• Limit caffeine and alcohol");
            }
        });

        cardLimitProcessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Limit Processed Foods",
                        "Processed foods can harm your eyes:\n\n" +
                                "❌ High sugar: Increases diabetes risk (leading cause of blindness)\n" +
                                "❌ Excess salt: Raises blood pressure, damages retinal vessels\n" +
                                "❌ Trans fats: Promotes inflammation, damages blood vessels\n\n" +
                                "Better choices:\n" +
                                "✓ Whole grains instead of refined\n" +
                                "✓ Fresh fruits instead of sugary snacks\n" +
                                "✓ Nuts instead of chips");
            }
        });

        cardHealthyFats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Include Healthy Fats",
                        "Good fats are essential for eye health:\n\n" +
                                "Omega-3 fatty acids:\n" +
                                "• Salmon, mackerel, sardines\n" +
                                "• Flaxseeds, chia seeds, walnuts\n\n" +
                                "Monounsaturated fats:\n" +
                                "• Olive oil, avocados\n" +
                                "• Almonds, peanuts\n\n" +
                                "Benefits: Reduces dry eyes, protects retina, reduces inflammation.");
            }
        });

        cardDailyChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Daily Checklist",
                        "Track these habits daily for optimal eye health:\n\n" +
                                "☐ Eat 3+ different colored vegetables\n" +
                                "☐ Drink 8 glasses of water\n" +
                                "☐ Take screen breaks (20-20-20 rule)\n" +
                                "☐ Eat nuts or seeds\n" +
                                "☐ Get 7-8 hours of sleep\n" +
                                "☐ Exercise for 30+ minutes\n\n" +
                                "Consistency is key! Small daily habits create lasting eye health.");
            }
        });

        cardImportantNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Important Reminder",
                        "While diet and lifestyle are important:\n\n" +
                                "⚠️ They cannot replace professional eye care\n\n" +
                                "Schedule regular eye exams:\n" +
                                "• Every 1-2 years for adults\n" +
                                "• Annually if you wear glasses/contacts\n" +
                                "• More frequently if you have conditions like diabetes\n\n" +
                                "Contact an eye doctor immediately if you experience:\n" +
                                "• Sudden vision changes\n" +
                                "• Eye pain or redness\n" +
                                "• Flashes of light or floaters");
            }
        });
    }

    private void showDetails(String title, String message) {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Got it", null)
                .show();
        DialogUtils.styleWhite(d);
    }
}
