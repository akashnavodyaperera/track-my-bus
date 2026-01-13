package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.BusDriverAdapter;
import com.wycherley.trackmybus.models.BusDriver;
import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {

    private ImageView ivProfile, ivNotifications, ivSearch;
    private EditText etSearchBus;
    private RecyclerView rvBusDrivers;
    private BottomNavigationView bottomNavigation;
    private BusDriverAdapter busDriverAdapter;

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
        loadSampleData();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivSearch = findViewById(R.id.ivSearch);
        etSearchBus = findViewById(R.id.etSearchBus);
        rvBusDrivers = findViewById(R.id.rvBusDrivers);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupProfileClick() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ParentDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        ivNotifications.setOnClickListener(v -> {
            // TODO: Navigate to notifications
        });

        ivSearch.setOnClickListener(v -> {
            // TODO: Implement search
            String query = etSearchBus.getText().toString();
        });
    }

    private void setupRecyclerView() {
        busDriverAdapter = new BusDriverAdapter();
        rvBusDrivers.setLayoutManager(new LinearLayoutManager(this));
        rvBusDrivers.setAdapter(busDriverAdapter);

        busDriverAdapter.setOnItemClickListener(busDriver -> {
            // TODO: Navigate to bus details or tracking
            Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void loadSampleData() {
        // Sample data - replace with Firebase data later
        List<BusDriver> drivers = new ArrayList<>();
        drivers.add(new BusDriver("WP NA - 8965", "Mr. Surendra Rajapaksha", "Negombo", "Wycherley International School"));
        drivers.add(new BusDriver("WP NA - 8966", "Mr. Kamal Silva", "Colombo", "Wycherley International School"));
        drivers.add(new BusDriver("WP NA - 8967", "Mr. Nimal Fernando", "Gampaha", "Wycherley International School"));

        busDriverAdapter.setBusDrivers(drivers);
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
                Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
                startActivity(intent);
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


