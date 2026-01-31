package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.TripHistoryAdapter;
import com.wycherley.trackmybus.models.TripHistory;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.TripHistoryRepository;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private RecyclerView rvTripHistory;
    private ProgressBar progressBar;
    private LinearLayout layoutNoHistory;  // ✅ Changed from TextView to LinearLayout
    private TextView tvTotalTrips, tvMorningTrips, tvAfternoonTrips;
    private MaterialButton btnAllTrips, btnMorningTrips, btnAfternoonTrips;
    private BottomNavigationView bottomNavigation;

    private TripHistoryAdapter historyAdapter;
    private TripHistoryRepository historyRepository;
    private String currentUserId;
    private List<TripHistory> allTrips;
    private String currentFilter = "ALL"; // ALL, MORNING, AFTERNOON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize
        historyRepository = TripHistoryRepository.getInstance();
        currentUserId = AuthRepository.getInstance().getCurrentUser().getUid();

        initViews();
        setupRecyclerView();
        setupFilterButtons();
        setupBottomNavigation();
        loadTripHistory();
        loadStatistics();
    }

    private void initViews() {
        rvTripHistory = findViewById(R.id.rvTripHistory);
        progressBar = findViewById(R.id.progressBar);
        layoutNoHistory = findViewById(R.id.tvNoHistory);  // ✅ Changed variable name
        tvTotalTrips = findViewById(R.id.tvTotalTrips);
        tvMorningTrips = findViewById(R.id.tvMorningTrips);
        tvAfternoonTrips = findViewById(R.id.tvAfternoonTrips);
        btnAllTrips = findViewById(R.id.btnAllTrips);
        btnMorningTrips = findViewById(R.id.btnMorningTrips);
        btnAfternoonTrips = findViewById(R.id.btnAfternoonTrips);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        historyAdapter = new TripHistoryAdapter();
        rvTripHistory.setLayoutManager(new LinearLayoutManager(this));
        rvTripHistory.setAdapter(historyAdapter);

        historyAdapter.setOnTripClickListener(trip -> {
            // Show trip details or navigate to map
            showTripDetails(trip);
        });
    }

    private void setupFilterButtons() {
        btnAllTrips.setOnClickListener(v -> {
            currentFilter = "ALL";
            updateFilterButtons();
            filterTrips();
        });

        btnMorningTrips.setOnClickListener(v -> {
            currentFilter = "MORNING";
            updateFilterButtons();
            filterTrips();
        });

        btnAfternoonTrips.setOnClickListener(v -> {
            currentFilter = "AFTERNOON";
            updateFilterButtons();
            filterTrips();
        });

        updateFilterButtons();
    }

    private void updateFilterButtons() {
        // Reset all buttons
        btnAllTrips.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnMorningTrips.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnAfternoonTrips.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Highlight selected button
        int selectedColor = getResources().getColor(R.color.primary_blue);
        switch (currentFilter) {
            case "ALL":
                btnAllTrips.setBackgroundColor(selectedColor);
                break;
            case "MORNING":
                btnMorningTrips.setBackgroundColor(selectedColor);
                break;
            case "AFTERNOON":
                btnAfternoonTrips.setBackgroundColor(selectedColor);
                break;
        }
    }

    private void filterTrips() {
        if (allTrips == null) return;

        List<TripHistory> filteredTrips;

        switch (currentFilter) {
            case "MORNING":
                filteredTrips = new java.util.ArrayList<>();
                for (TripHistory trip : allTrips) {
                    if (trip.isMorningTrip()) {
                        filteredTrips.add(trip);
                    }
                }
                break;
            case "AFTERNOON":
                filteredTrips = new java.util.ArrayList<>();
                for (TripHistory trip : allTrips) {
                    if (trip.isAfternoonTrip()) {
                        filteredTrips.add(trip);
                    }
                }
                break;
            default:
                filteredTrips = allTrips;
                break;
        }

        historyAdapter.setTrips(filteredTrips);

        if (filteredTrips.isEmpty()) {
            layoutNoHistory.setVisibility(View.VISIBLE);  // ✅ Changed
            rvTripHistory.setVisibility(View.GONE);
        } else {
            layoutNoHistory.setVisibility(View.GONE);  // ✅ Changed
            rvTripHistory.setVisibility(View.VISIBLE);
        }
    }

    private void loadTripHistory() {
        showLoading(true);

        // Load last 30 days of history
        historyRepository.getRecentTripHistory(currentUserId, 30,
                new TripHistoryRepository.OnTripHistoryLoadListener() {
                    @Override
                    public void onHistoryLoaded(List<TripHistory> trips) {
                        showLoading(false);
                        allTrips = trips;

                        Log.d(TAG, "Loaded " + trips.size() + " trips");
                        filterTrips();
                    }

                    @Override
                    public void onError(String error) {
                        showLoading(false);
                        Log.e(TAG, "Error loading history: " + error);
                        Toast.makeText(HistoryActivity.this,
                                "Error loading history: " + error,
                                Toast.LENGTH_LONG).show();

                        layoutNoHistory.setVisibility(View.VISIBLE);  // ✅ Changed
                        rvTripHistory.setVisibility(View.GONE);
                    }
                });
    }

    private void loadStatistics() {
        historyRepository.getTripStatistics(currentUserId,
                new TripHistoryRepository.OnTripStatsListener() {
                    @Override
                    public void onStatsLoaded(int totalTrips, int morningTrips, int afternoonTrips) {
                        tvTotalTrips.setText(String.valueOf(totalTrips));
                        tvMorningTrips.setText(String.valueOf(morningTrips));
                        tvAfternoonTrips.setText(String.valueOf(afternoonTrips));
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error loading statistics: " + error);
                    }
                });
    }

    private void showTripDetails(TripHistory trip) {
        // You can navigate to map or show a dialog with trip details
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("DRIVER_ID", trip.getDriverId());
        intent.putExtra("BUS_NUMBER", trip.getBusNumber());
        intent.putExtra("DRIVER_NAME", trip.getDriverName());
        intent.putExtra("LATITUDE", trip.getLatitude());
        intent.putExtra("LONGITUDE", trip.getLongitude());
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvTripHistory.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_history);

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
                return true;
            } else if (itemId == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTripHistory();
        loadStatistics();
    }
}