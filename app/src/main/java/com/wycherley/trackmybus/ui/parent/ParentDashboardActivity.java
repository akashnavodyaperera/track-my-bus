package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.wycherley.trackmybus.models.TripHistory;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.BusDriverRepository;
import com.wycherley.trackmybus.repositories.UserRepository;
import com.wycherley.trackmybus.utils.ParentBusPreference;
import com.wycherley.trackmybus.repositories.TripHistoryRepository;
import com.wycherley.trackmybus.adapters.TripHistoryAdapter;
import com.wycherley.trackmybus.models.TripHistory;
import com.wycherley.trackmybus.repositories.TripHistoryRepository;
import com.wycherley.trackmybus.ui.parent.AboutActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "ParentDashboard";

    private ImageView ivProfile, ivNotifications, ivSearch;
    private EditText etSearchBus;
    private RecyclerView rvBusDrivers;
    private BottomNavigationView bottomNavigation;
    private ProgressBar progressBar;

    private BusDriverAdapter busDriverAdapter;
    private BusDriverRepository driverRepository;
    private UserRepository userRepository;
    private ParentBusPreference busPreference;
    private String currentUserId;

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

        // Initialize repositories
        driverRepository = BusDriverRepository.getInstance();
        userRepository = UserRepository.getInstance();
        busPreference = new ParentBusPreference(this);
        currentUserId = AuthRepository.getInstance().getCurrentUser().getUid();

        initViews();
        setupProfileClick();
        setupRecyclerView();
        setupBottomNavigation();
        setupSearch();
        loadDriversFromFirebase();
        loadSelectedBusFromFirebase(); // Load selected bus from Firebase
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivSearch = findViewById(R.id.ivSearch);
        etSearchBus = findViewById(R.id.etSearchBus);
        rvBusDrivers = findViewById(R.id.rvBusDrivers);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupProfileClick() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ParentDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        ivNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ParentDashboardActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ivSearch.setOnClickListener(v -> {
            performSearch();
        });
    }

    private void setupRecyclerView() {
        busDriverAdapter = new BusDriverAdapter();
        rvBusDrivers.setLayoutManager(new LinearLayoutManager(this));
        rvBusDrivers.setAdapter(busDriverAdapter);

        // Handle card click - view details
        busDriverAdapter.setOnItemClickListener(busDriver -> {
            // Navigate to map with selected driver info
            Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
            intent.putExtra("DRIVER_ID", busDriver.getId());
            intent.putExtra("BUS_NUMBER", busDriver.getBusNumber());
            intent.putExtra("DRIVER_NAME", busDriver.getDriverName());
            startActivity(intent);
        });

        // Handle "Set as my bus" button click
        busDriverAdapter.setOnSetAsMyBusListener(busDriver -> {
            setAsMyBus(busDriver);
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

                Log.d(TAG, "✅ Loaded " + drivers.size() + " bus drivers");
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Log.e(TAG, "❌ Error loading drivers: " + error);
                Toast.makeText(ParentDashboardActivity.this,
                        "Error loading drivers: " + error,
                        Toast.LENGTH_LONG).show();

                // Load sample data as fallback
                loadSampleData();
            }
        });
    }

    private void loadSelectedBusFromFirebase() {
        Log.d(TAG, "Loading selected bus from Firebase...");

        userRepository.getMyBusId(currentUserId, new UserRepository.OnMyBusLoadListener() {
            @Override
            public void onMyBusLoaded(String busDriverId) {
                Log.d(TAG, "✅ My Bus loaded from Firebase: " + busDriverId);
                busDriverAdapter.setSelectedBusId(busDriverId);

                // Also save to SharedPreferences for offline access
                busPreference.setSelectedBusId(busDriverId);
            }

            @Override
            public void onNoBusAssigned() {
                Log.d(TAG, "No bus assigned yet");
                busDriverAdapter.setSelectedBusId(null);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error loading My Bus: " + error);
                // Try loading from SharedPreferences as fallback
                String cachedBusId = busPreference.getSelectedBusId();
                if (cachedBusId != null) {
                    Log.d(TAG, "Using cached bus ID: " + cachedBusId);
                    busDriverAdapter.setSelectedBusId(cachedBusId);
                }
            }
        });
    }

    private void setAsMyBus(BusDriver busDriver) {
        Log.d(TAG, "Setting as My Bus: " + busDriver.getBusNumber() + " (ID: " + busDriver.getId() + ")");

        // Show progress
        showLoading(true);

        // Save to FIREBASE FIRST (source of truth)
        userRepository.setMyBus(currentUserId, busDriver.getId(),
                new UserRepository.OnUpdateCompleteListener() {
                    @Override
                    public void onSuccess(String message) {
                        showLoading(false);
                        Log.d(TAG, "✅ Saved to Firebase successfully");

                        // Then save to SharedPreferences for offline access
                        busPreference.setSelectedBus(busDriver);
                        busPreference.setSelectedBusId(busDriver.getId());

                        // Update UI
                        busDriverAdapter.setSelectedBusId(busDriver.getId());

                        Toast.makeText(ParentDashboardActivity.this,
                                "✓ " + busDriver.getBusNumber() + " set as your bus",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        showLoading(false);
                        Log.e(TAG, "❌ Failed to save to Firebase: " + error);
                        Toast.makeText(ParentDashboardActivity.this,
                                "Failed to set bus: " + error,
                                Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(ParentDashboardActivity.this, MyBusActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_map) {
                Intent intent = new Intent(ParentDashboardActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_history) {
                Intent intent = new Intent(ParentDashboardActivity.this, HistoryActivity.class);
                return true;
            } else if (itemId == R.id.nav_about) {
                // Navigate to About Activity
                Intent intent = new Intent(ParentDashboardActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Refreshing data");

        // Refresh selected bus from Firebase when returning
        loadSelectedBusFromFirebase();

        // Refresh drivers list
        loadDriversFromFirebase();
    }
}