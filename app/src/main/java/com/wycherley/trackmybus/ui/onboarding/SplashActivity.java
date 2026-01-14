package com.wycherley.trackmybus.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2500; // 2.5 seconds
    private static final String PREFS_NAME = "TrackMyBusPrefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Delay and then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, SPLASH_DELAY);
    }

    private void navigateToNextScreen() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean(KEY_FIRST_TIME, true);

        Intent intent;

        if (isFirstTime) {
            // First time - show tutorial
            intent = new Intent(SplashActivity.this, TutorialActivity.class);
        } else {
            // Not first time - check if user is logged in
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // User is logged in - navigate based on their role
                // For now, just go to LoginActivity which will handle auto-login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                // User not logged in - go to login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
        }

        startActivity(intent);
        finish();
    }
}