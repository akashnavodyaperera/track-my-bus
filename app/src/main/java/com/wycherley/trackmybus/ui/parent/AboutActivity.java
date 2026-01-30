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
    private static final String TAG = "AboutActivity";

    private BottomNavigationView bottomNavigation;
    private Button btnContactSupport;
    private TextView tvTerms, tvPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);
    }

    private void setupClickListeners() {
        // Contact Support Button
        btnContactSupport.setOnClickListener(v -> {
            // Open email app to contact support
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:info@wycherley.lk"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Track My Bus - Support Request");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        // Terms of Service
        tvTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show();
            // TODO: Open terms of service activity or web page
        });

        // Privacy Policy
        tvPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            // TODO: Open privacy policy activity or web page
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_about);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, ParentDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_buses) {
                startActivity(new Intent(this, MyBusActivity.class));
                return true;
            } else if (itemId == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_about) {
                // Already on About screen
                return true;
            }
            return false;
        });
    }
}