package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.BusDriver;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.BusDriverRepository;
import com.wycherley.trackmybus.repositories.UserRepository;

public class MyBusActivity extends AppCompatActivity {
    private static final String TAG = "MyBusActivity";

    private LinearLayout layoutNoBus, layoutBusDetails;
    private TextView tvBusNumber, tvDriverName, tvFromLocation, tvToLocation,
            tvPhoneNumber, tvEmail, tvStarRating;
    private ImageView ivDriverProfile;
    private Button btnSelectBus, btnTrackBus, btnChangeBus;
    private BottomNavigationView bottomNavigation;

    private UserRepository userRepository;
    private BusDriverRepository busDriverRepository;
    private String currentUserId;
    private BusDriver selectedBus;

    private RatingBar ratingBar;
    private Button btnSubmitRating;
    private TextView tvRatingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bus);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize repositories
        userRepository = UserRepository.getInstance();
        busDriverRepository = BusDriverRepository.getInstance();
        currentUserId = AuthRepository.getInstance().getCurrentUser().getUid();

        initViews();
        loadMyBusFromFirebase();
        setupBottomNavigation();
    }

    private void initViews() {
        layoutNoBus = findViewById(R.id.layoutNoBus);
        layoutBusDetails = findViewById(R.id.layoutBusDetails);
        tvBusNumber = findViewById(R.id.tvBusNumber);
        tvDriverName = findViewById(R.id.tvDriverName);
        tvFromLocation = findViewById(R.id.tvFromLocation);
        tvToLocation = findViewById(R.id.tvToLocation);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmail = findViewById(R.id.tvEmail);
        ivDriverProfile = findViewById(R.id.ivDriverProfile);
        btnSelectBus = findViewById(R.id.btnSelectBus);
        btnTrackBus = findViewById(R.id.btnTrackBus);
        btnChangeBus = findViewById(R.id.btnChangeBus);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        tvRatingInfo = findViewById(R.id.tvRatingInfo);
        tvStarRating = findViewById(R.id.tvStarRating);
    }

    private void loadMyBusFromFirebase() {
        Log.d(TAG, "Loading My Bus from Firebase...");

        userRepository.getMyBusId(currentUserId, new UserRepository.OnMyBusLoadListener() {
            @Override
            public void onMyBusLoaded(String busDriverId) {
                Log.d(TAG, "✅ My Bus ID: " + busDriverId);
                loadBusDriverDetails(busDriverId);
            }

            @Override
            public void onNoBusAssigned() {
                Log.d(TAG, "No bus assigned");
                showNoBusSelected();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error loading My Bus: " + error);
                showNoBusSelected();
                Toast.makeText(MyBusActivity.this,
                        "Error loading bus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBusDriverDetails(String busDriverId) {
        busDriverRepository.getDriversOnce(new BusDriverRepository.OnDriversLoadListener() {
            @Override
            public void onDriversLoaded(java.util.List<BusDriver> drivers) {
                for (BusDriver driver : drivers) {
                    if (driver.getId().equals(busDriverId)) {
                        selectedBus = driver;
                        Log.d(TAG, "✅ Bus details loaded: " + driver.getBusNumber());
                        showBusDetails();
                        return;
                    }
                }
                Log.e(TAG, "❌ Bus driver not found in database");
                showNoBusSelected();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error loading bus details: " + error);
                showNoBusSelected();
            }
        });
    }

    private void showBusDetails() {
        layoutNoBus.setVisibility(View.GONE);
        layoutBusDetails.setVisibility(View.VISIBLE);

        tvBusNumber.setText(selectedBus.getBusNumber());
        tvDriverName.setText(selectedBus.getDriverName());
        tvFromLocation.setText(selectedBus.getFromLocation());
        tvToLocation.setText(selectedBus.getToLocation());

        // Display star rating in the blue card
        if (selectedBus.getTotalRatings() > 0) {
            tvStarRating.setText(selectedBus.getStarString());
            tvStarRating.setVisibility(View.VISIBLE);
        } else {
            tvStarRating.setText("☆☆☆☆☆");
            tvStarRating.setVisibility(View.VISIBLE);
        }

        // Show contact info if available
        if (selectedBus.getPhoneNumber() != null && !selectedBus.getPhoneNumber().isEmpty()) {
            tvPhoneNumber.setText(selectedBus.getPhoneNumber());
            tvPhoneNumber.setVisibility(View.VISIBLE);
        } else {
            tvPhoneNumber.setVisibility(View.GONE);
        }

        if (selectedBus.getEmail() != null && !selectedBus.getEmail().isEmpty()) {
            tvEmail.setText(selectedBus.getEmail());
            tvEmail.setVisibility(View.VISIBLE);
        } else {
            tvEmail.setVisibility(View.GONE);
        }

        btnTrackBus.setOnClickListener(v -> {
            Intent intent = new Intent(MyBusActivity.this, MapActivity.class);
            intent.putExtra("DRIVER_ID", selectedBus.getId());
            intent.putExtra("BUS_NUMBER", selectedBus.getBusNumber());
            intent.putExtra("DRIVER_NAME", selectedBus.getDriverName());
            startActivity(intent);
        });

        btnChangeBus.setOnClickListener(v -> {
            Intent intent = new Intent(MyBusActivity.this, ParentDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Display rating info
        if (selectedBus.getTotalRatings() > 0) {
            tvRatingInfo.setText(String.format("Average: %.1f⭐ (%d ratings)",
                    selectedBus.getAverageRating(), selectedBus.getTotalRatings()));
        } else {
            tvRatingInfo.setText("No ratings yet. Be the first to rate!");
        }

        // Load user's previous rating
        loadUserRating();

        // Setup rating submission
        btnSubmitRating.setOnClickListener(v -> submitRating());
    }

    private void showNoBusSelected() {
        layoutNoBus.setVisibility(View.VISIBLE);
        layoutBusDetails.setVisibility(View.GONE);

        btnSelectBus.setOnClickListener(v -> {
            Intent intent = new Intent(MyBusActivity.this, ParentDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_buses);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, ParentDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_buses) {
                return true;
            } else if (itemId == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            } else if (itemId == R.id.nav_feedback) {
                return true;
            } else if (itemId == R.id.nav_about) {
                return true;
            }
            return false;
        });
    }

    private void loadUserRating() {
        busDriverRepository.getUserRating(selectedBus.getId(), currentUserId,
                new BusDriverRepository.OnUserRatingLoadListener() {
                    @Override
                    public void onRatingLoaded(int rating) {
                        if (rating > 0) {
                            ratingBar.setRating(rating);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error loading rating: " + error);
                    }
                });
    }

    private void submitRating() {
        int rating = (int) ratingBar.getRating();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitRating.setEnabled(false);
        btnSubmitRating.setText("Submitting...");

        busDriverRepository.rateDriver(selectedBus.getId(), currentUserId, rating,
                new BusDriverRepository.OnDriverSaveListener() {
                    @Override
                    public void onSuccess() {
                        btnSubmitRating.setEnabled(true);
                        btnSubmitRating.setText("Submit");
                        Toast.makeText(MyBusActivity.this,
                                "Rating submitted successfully!", Toast.LENGTH_SHORT).show();

                        // Reload bus details to show updated rating
                        loadMyBusFromFirebase();
                    }

                    @Override
                    public void onError(String error) {
                        btnSubmitRating.setEnabled(true);
                        btnSubmitRating.setText("Submit");
                        Toast.makeText(MyBusActivity.this,
                                "Failed to submit rating: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyBusFromFirebase();
    }
}