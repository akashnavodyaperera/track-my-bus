package com.wycherley.trackmybus.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.AdminDriverAdapter;
import com.wycherley.trackmybus.models.BusDriver;
import com.wycherley.trackmybus.repositories.BusDriverRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageDriversActivity extends AppCompatActivity {

    private RecyclerView rvDrivers;
    private FloatingActionButton fabAddDriver;
    private ProgressBar progressBar;
    private AdminDriverAdapter driverAdapter;
    private BusDriverRepository driverRepository;
    private List<BusDriver> driverList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drivers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Drivers");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        driverRepository = BusDriverRepository.getInstance();

        initViews();
        setupRecyclerView();
        setupFab();
        loadDrivers();
    }

    private void initViews() {
        rvDrivers = findViewById(R.id.rvDrivers);
        fabAddDriver = findViewById(R.id.fabAddDriver);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        driverAdapter = new AdminDriverAdapter();
        rvDrivers.setLayoutManager(new LinearLayoutManager(this));
        rvDrivers.setAdapter(driverAdapter);

        // Set click listeners
        driverAdapter.setOnEditClickListener(this::editDriver);
        driverAdapter.setOnDeleteClickListener(this::confirmDeleteDriver);
    }

    private void setupFab() {
        fabAddDriver.setOnClickListener(v -> {
            Intent intent = new Intent(ManageDriversActivity.this, AddDriverActivity.class);
            startActivity(intent);
        });
    }

    private void loadDrivers() {
        showLoading(true);

        driverRepository.getAllDrivers(new BusDriverRepository.OnDriversLoadListener() {
            @Override
            public void onDriversLoaded(List<BusDriver> drivers) {
                showLoading(false);
                driverList = drivers;
                driverAdapter.setDrivers(drivers);

                if (drivers.isEmpty()) {
                    Toast.makeText(ManageDriversActivity.this,
                            "No drivers found. Add some drivers!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(ManageDriversActivity.this,
                        "Error loading drivers: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editDriver(BusDriver driver) {
        Intent intent = new Intent(ManageDriversActivity.this, EditDriverActivity.class);
        intent.putExtra("DRIVER_ID", driver.getId());
        intent.putExtra("BUS_NUMBER", driver.getBusNumber());
        intent.putExtra("DRIVER_NAME", driver.getDriverName());
        intent.putExtra("FROM_LOCATION", driver.getFromLocation());
        intent.putExtra("TO_LOCATION", driver.getToLocation());
        intent.putExtra("PHONE_NUMBER", driver.getPhoneNumber());
        intent.putExtra("EMAIL", driver.getEmail());
        startActivity(intent);
    }

    private void confirmDeleteDriver(BusDriver driver) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Driver")
                .setMessage("Are you sure you want to delete " + driver.getDriverName() +
                        " (" + driver.getBusNumber() + ")?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDriver(driver))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteDriver(BusDriver driver) {
        showLoading(true);

        driverRepository.deleteDriver(driver.getId(),
                new BusDriverRepository.OnDriverSaveListener() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        Toast.makeText(ManageDriversActivity.this,
                                "Driver deleted successfully", Toast.LENGTH_SHORT).show();
                        loadDrivers(); // Reload the list
                    }

                    @Override
                    public void onError(String error) {
                        showLoading(false);
                        Toast.makeText(ManageDriversActivity.this,
                                "Error deleting driver: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvDrivers.setVisibility(show ? View.GONE : View.VISIBLE);
        fabAddDriver.setEnabled(!show);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload drivers when returning to this activity
        loadDrivers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}