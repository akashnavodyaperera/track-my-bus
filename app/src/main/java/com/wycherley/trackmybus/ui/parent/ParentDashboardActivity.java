package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;

public class ParentDashboardActivity extends AppCompatActivity {

    private ImageView ivProfile, ivNotifications;
    private RecyclerView rvBusDrivers;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupProfileClick();
        setupRecyclerView();
        setupBottomNavigation();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        ivNotifications = findViewById(R.id.ivNotifications);
        rvBusDrivers = findViewById(R.id.rvBusDrivers);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupProfileClick() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ParentDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvBusDrivers.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set adapter with bus driver data
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_buses) {
                // TODO: Navigate to Buses
                return true;
            } else if (itemId == R.id.nav_map) {
                // TODO: Navigate to Map
                return true;
            } else if (itemId == R.id.nav_feedback) {
                // TODO: Navigate to Feedback
                return true;
            } else if (itemId == R.id.nav_about) {
                // TODO: Navigate to About
                return true;
            }
            return false;
        });
    }
}