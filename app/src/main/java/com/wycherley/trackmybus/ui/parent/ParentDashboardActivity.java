package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.BusDriverAdapter;
import com.wycherley.trackmybus.models.BusDriver;
import com.wycherley.trackmybus.repositories.BusDriverRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParentDashboardActivity extends AppCompatActivity {

    private ImageView ivProfile, ivNotifications, ivSearch;
    private EditText etSearchBus;
    private RecyclerView rvBusDrivers;
    private BottomNavigationView bottomNavigation;
    private ProgressBar progressBar;

    private BusDriverAdapter busDriverAdapter;
    private BusDriverRepository driverRepository;

    private List<BusDriver> allDrivers = new ArrayList<>();
    private List<BusDriver> filteredDrivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        driverRepository = BusDriverRepository.getInstance();

        initViews();
        setupProfileClick();
        setupRecyclerView();
        setupBottomNavigation();
        setupSearch();
        loadDriversFromFirebase();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivSearch = findViewById(R.id.ivSearch);
        etSearchBus = findViewById(R.id.etSearchBus);
        rvBusDrivers = findViewById(R.id.rvBusDrivers);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Add a ProgressBar to your layout or create programmatically
        progressBar = new ProgressBar(this);
        // You should add this to your XML layout instead
    }

    private void setupProfileClick() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ParentDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        ivNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show();
        });

        ivSearch.setOnClickListener(v -> {
            performSearch();
        });
    }

    private void setupRecyclerView() {
        busDriverAdapter = new BusDriverAdapter();
        rvBusDrivers.setLayoutManager(new LinearLayoutManager(this));
        rvBusDrivers.setAdapter(busDriverAdapter);

        busDriverAdapter.setOnItemClickListener(busDriver -> {
            // Navigate to map with selected driver info
            Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
            intent.putExtra("DRIVER_ID", busDriver.getId());
            intent.putExtra("BUS_NUMBER", busDriver.getBusNumber());
            intent.putExtra("DRIVER_NAME", busDriver.getDriverName());
            startActivity(intent);
        });
    }

    private void setupSearch() {
        etSearchBus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDrivers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch() {
        String query = etSearchBus.getText().toString().trim();
        if (!query.isEmpty()) {
            filterDrivers(query);
        } else {
            Toast.makeText(this, "Please enter a bus number", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterDrivers(String query) {
        if (query.isEmpty()) {
            filteredDrivers = new ArrayList<>(allDrivers);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredDrivers = allDrivers.stream()
                    .filter(driver ->
                            driver.getBusNumber().toLowerCase().contains(lowerCaseQuery) ||
                                    driver.getDriverName().toLowerCase().contains(lowerCaseQuery) ||
                                    driver.getFromLocation().toLowerCase().contains(lowerCaseQuery)
                    )
                    .collect(Collectors.toList());
        }
        busDriverAdapter.setBusDrivers(filteredDrivers);

        if (filteredDrivers.isEmpty() && !query.isEmpty()) {
            Toast.makeText(this, "No drivers found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDriversFromFirebase() {
        showLoading(true);

        driverRepository.getAllDrivers(new BusDriverRepository.OnDriversLoadListener() {
            @Override
            public void onDriversLoaded(List<BusDriver> drivers) {
                showLoading(false);
                allDrivers = drivers;
                filteredDrivers = new ArrayList<>(drivers);
                busDriverAdapter.setBusDrivers(filteredDrivers);

                if (drivers.isEmpty()) {
                    Toast.makeText(ParentDashboardActivity.this,
                            "No drivers available. Please add drivers first.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(ParentDashboardActivity.this,
                        "Error loading drivers: " + error,
                        Toast.LENGTH_LONG).show();

                // Load sample data as fallback
                loadSampleData();
            }
        });
    }

    private void loadSampleData() {
        // Fallback sample data if Firebase fails
        List<BusDriver> drivers = new ArrayList<>();
        drivers.add(new BusDriver("WP NA - 8965", "Mr. Surendra Rajapaksha",
                "Negombo", "Wycherley International School"));
        drivers.add(new BusDriver("WP NA - 8966", "Mr. Kamal Silva",
                "Colombo", "Wycherley International School"));
        drivers.add(new BusDriver("WP NA - 8967", "Mr. Nimal Fernando",
                "Gampaha", "Wycherley International School"));

        allDrivers = drivers;
        filteredDrivers = new ArrayList<>(drivers);
        busDriverAdapter.setBusDrivers(filteredDrivers);
    }

    private void showLoading(boolean show) {
        // Implement loading indicator
        // You can add a ProgressBar to your layout and show/hide it here
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        rvBusDrivers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_buses) {
                Toast.makeText(this, "Buses view coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_map) {
                Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_feedback) {
                Toast.makeText(this, "Feedback coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_about) {
                Toast.makeText(this, "About coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadDriversFromFirebase();
    }
}