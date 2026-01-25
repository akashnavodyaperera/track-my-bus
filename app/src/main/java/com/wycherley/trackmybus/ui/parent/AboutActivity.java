package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;

public class AboutActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Button btnContactSupport;
    private TextView tvTerms, tvPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupBottomNavigation();
        setupButtons();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);
    }

    private void setupButtons() {
        // Contact Support Button
        btnContactSupport.setOnClickListener(v -> {
            openEmailSupport();
        });

        // Terms of Service
        tvTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show();
            // You can open a web page or show a dialog here
            // openWebPage("https://wycherley.lk/terms");
        });

        // Privacy Policy
        tvPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            // You can open a web page or show a dialog here
            // openWebPage("https://wycherley.lk/privacy");
        });
    }

    private void openEmailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@wycherley.lk"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Track My Bus - Support Request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Support Team,\n\nI need help with:\n\n");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWebPage(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No browser app installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_about);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, ParentDashboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_buses) {
                Intent intent = new Intent(this, MyBusActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_map) {
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_feedback) {
                Toast.makeText(this, "Feedback coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_about) {
                return true; // Already on About page
            }
            return false;
        });
    }
}