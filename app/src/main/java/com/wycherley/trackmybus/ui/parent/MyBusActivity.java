package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.BusDriver;
import com.wycherley.trackmybus.utils.ParentBusPreference;

public class MyBusActivity extends AppCompatActivity {

    private LinearLayout layoutNoBus, layoutBusDetails;
    private TextView tvBusNumber, tvDriverName, tvFromLocation, tvToLocation,
            tvPhoneNumber, tvEmail;
    private ImageView ivDriverProfile;
    private Button btnSelectBus, btnTrackBus, btnChangeBus;
    private BottomNavigationView bottomNavigation;

    private ParentBusPreference busPreference;
    private BusDriver selectedBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bus);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        busPreference = new ParentBusPreference(this);

        initViews();
        loadSelectedBus();
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
    }

    private void loadSelectedBus() {
        selectedBus = busPreference.getSelectedBus();

        if (selectedBus != null) {
            showBusDetails();
        } else {
            showNoBusSelected();
        }
    }

    private void showBusDetails() {
        layoutNoBus.setVisibility(View.GONE);
        layoutBusDetails.setVisibility(View.VISIBLE);

        tvBusNumber.setText(selectedBus.getBusNumber());
        tvDriverName.setText(selectedBus.getDriverName());
        tvFromLocation.setText(selectedBus.getFromLocation());
        tvToLocation.setText(selectedBus.getToLocation());

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
            // Go back to home to select different bus
            Intent intent = new Intent(MyBusActivity.this, ParentDashboardActivity.class);
            startActivity(intent);
            finish();
        });
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
                // TODO: Feedback activity
                return true;
            } else if (itemId == R.id.nav_about) {
                // TODO: About activity
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSelectedBus();
    }
}