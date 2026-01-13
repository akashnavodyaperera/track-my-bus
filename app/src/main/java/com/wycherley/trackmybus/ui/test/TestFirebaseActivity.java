package com.wycherley.trackmybus.ui.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.repositories.BusDriverRepository;
import com.wycherley.trackmybus.utils.FirebaseDataSeeder;

/**
 * Testing activity to verify Firebase integration
 * Add a button in your app to navigate here for testing
 */
public class TestFirebaseActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnLoadDrivers, btnSeedData, btnClearData;
    private BusDriverRepository driverRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Firebase Test");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        driverRepository = BusDriverRepository.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        btnLoadDrivers = findViewById(R.id.btnLoadDrivers);
        btnSeedData = findViewById(R.id.btnSeedData);
        btnClearData = findViewById(R.id.btnClearData);
    }

    private void setupListeners() {
        btnLoadDrivers.setOnClickListener(v -> testLoadDrivers());
        btnSeedData.setOnClickListener(v -> testSeedData());
        btnClearData.setOnClickListener(v -> testClearData());
    }

    private void testLoadDrivers() {
        tvStatus.setText("Loading drivers from Firebase...");

        driverRepository.getDriversOnce(new BusDriverRepository.OnDriversLoadListener() {
            @Override
            public void onDriversLoaded(java.util.List<com.wycherley.trackmybus.models.BusDriver> drivers) {
                String status = "✅ SUCCESS!\n\n";
                status += "Found " + drivers.size() + " drivers:\n\n";

                for (com.wycherley.trackmybus.models.BusDriver driver : drivers) {
                    status += "• " + driver.getBusNumber() + " - " +
                            driver.getDriverName() + "\n";
                }

                tvStatus.setText(status);
                Toast.makeText(TestFirebaseActivity.this,
                        "Loaded " + drivers.size() + " drivers",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                tvStatus.setText("❌ ERROR:\n\n" + error);
                Toast.makeText(TestFirebaseActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testSeedData() {
        tvStatus.setText("Seeding sample data to Firebase...");

        FirebaseDataSeeder.seedBusDrivers((success, message) -> {
            if (success) {
                tvStatus.setText("✅ " + message + "\n\nClick 'Load Drivers' to verify.");
            } else {
                tvStatus.setText("❌ " + message);
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void testClearData() {
        tvStatus.setText("⚠️ Clearing all drivers from Firebase...");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear All Data?")
                .setMessage("This will delete ALL drivers from Firebase. Are you sure?")
                .setPositiveButton("Yes, Clear", (dialog, which) -> {
                    FirebaseDataSeeder.clearAllDrivers((success, message) -> {
                        tvStatus.setText(success ? "✅ " + message : "❌ " + message);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}