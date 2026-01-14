package com.wycherley.trackmybus.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.TutorialAdapter;
import com.wycherley.trackmybus.models.TutorialItem;
import com.wycherley.trackmybus.ui.auth.LoginActivity;
import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnNext, btnSkip;
    private TutorialAdapter tutorialAdapter;
    private List<TutorialItem> tutorialItems;

    private static final String PREFS_NAME = "TrackMyBusPrefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupTutorialData();
        setupViewPager();
        setupDots();
        setupListeners();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void setupTutorialData() {
        tutorialItems = new ArrayList<>();

        tutorialItems.add(new TutorialItem(
                R.drawable.tutorial_01,
                "Monitor Your Child Journey At Every Stage.",
                "Keep track of your child's location in real-time."
        ));

        tutorialItems.add(new TutorialItem(
                R.drawable.tutorial_02,
                "Navigate Pickup And Drop-Off Zones With Clarity And Ease.",
                "Get notified when pickup and drop-off points are nearby and effortlessly."
        ));

        tutorialItems.add(new TutorialItem(
                R.drawable.tutorial_03,
                "Create You Account And Start The Journey",
                "Create your account, register it securely and secure school journey experience."
        ));
    }

    private void setupViewPager() {
        tutorialAdapter = new TutorialAdapter(tutorialItems);
        viewPager.setAdapter(tutorialAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
                updateButtons(position);
            }
        });
    }

    private void setupDots() {
        ImageView[] dots = new ImageView[tutorialItems.size()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[i], params);
        }

        // Set first dot as active
        if (dots.length > 0) {
            dots[0].setImageResource(R.drawable.dot_active);
        }
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.dot_active);
            } else {
                dot.setImageResource(R.drawable.dot_inactive);
            }
        }
    }

    private void updateButtons(int position) {
        if (position == tutorialItems.size() - 1) {
            // Last page
            btnNext.setText("Get Started");
            btnSkip.setVisibility(View.GONE);
        } else {
            btnNext.setText("Next");
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();

            if (currentPosition < tutorialItems.size() - 1) {
                // Go to next page
                viewPager.setCurrentItem(currentPosition + 1);
            } else {
                // Last page - finish tutorial
                finishTutorial();
            }
        });

        btnSkip.setOnClickListener(v -> finishTutorial());
    }

    private void finishTutorial() {
        // Mark that user has seen the tutorial
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_TIME, false).apply();

        // Navigate to login
        Intent intent = new Intent(TutorialActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}