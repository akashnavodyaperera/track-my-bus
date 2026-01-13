package com.wycherley.trackmybus.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.BusDriver;
import com.wycherley.trackmybus.repositories.BusDriverRepository;

public class EditDriverActivity extends AppCompatActivity {

    private EditText etBusNumber, etDriverName, etFromLocation, etToLocation,
            etPhoneNumber, etEmail;
    private Button btnUpdateDriver;
    private ProgressBar progressBar;

    private BusDriverRepository driverRepository;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_driver);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Bus Driver");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        driverRepository = BusDriverRepository.getInstance();

        initViews();
        loadDriverData();
        setupListeners();
    }

    private void initViews() {
        etBusNumber = findViewById(R.id.etBusNumber);
        etDriverName = findViewById(R.id.etDriverName);
        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        btnUpdateDriver = findViewById(R.id.btnUpdateDriver);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadDriverData() {
        // Get data from intent
        driverId = getIntent().getStringExtra("DRIVER_ID");
        String busNumber = getIntent().getStringExtra("BUS_NUMBER");
        String driverName = getIntent().getStringExtra("DRIVER_NAME");
        String fromLocation = getIntent().getStringExtra("FROM_LOCATION");
        String toLocation = getIntent().getStringExtra("TO_LOCATION");
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        String email = getIntent().getStringExtra("EMAIL");

        // Set values
        etBusNumber.setText(busNumber);
        etDriverName.setText(driverName);
        etFromLocation.setText(fromLocation);
        etToLocation.setText(toLocation);
        etPhoneNumber.setText(phoneNumber != null ? phoneNumber : "");
        etEmail.setText(email != null ? email : "");
    }

    private void setupListeners() {
        btnUpdateDriver.setOnClickListener(v -> updateDriver());
    }

    private void updateDriver() {
        String busNumber = etBusNumber.getText().toString().trim();
        String driverName = etDriverName.getText().toString().trim();
        String fromLocation = etFromLocation.getText().toString().trim();
        String toLocation = etToLocation.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validation
        if (busNumber.isEmpty()) {
            etBusNumber.setError("Bus number is required");
            etBusNumber.requestFocus();
            return;
        }

        if (driverName.isEmpty()) {
            etDriverName.setError("Driver name is required");
            etDriverName.requestFocus();
            return;
        }

        if (fromLocation.isEmpty()) {
            etFromLocation.setError("From location is required");
            etFromLocation.requestFocus();
            return;
        }

        if (toLocation.isEmpty()) {
            etToLocation.setError("To location is required");
            etToLocation.requestFocus();
            return;
        }

        // Create updated driver object
        BusDriver driver = new BusDriver(busNumber, driverName, fromLocation, toLocation,
                phoneNumber, email);
        driver.setId(driverId);

        // Show loading
        showLoading(true);

        // Update in Firebase
        driverRepository.updateDriver(driver, new BusDriverRepository.OnDriverSaveListener() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(EditDriverActivity.this,
                        "Driver updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to previous screen
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(EditDriverActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpdateDriver.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}