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

public class AddDriverActivity extends AppCompatActivity {

    private EditText etBusNumber, etDriverName, etFromLocation, etToLocation,
            etPhoneNumber, etEmail;
    private Button btnSaveDriver, btnAddSampleData;
    private ProgressBar progressBar;

    private BusDriverRepository driverRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Bus Driver");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        driverRepository = BusDriverRepository.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etBusNumber = findViewById(R.id.etBusNumber);
        etDriverName = findViewById(R.id.etDriverName);
        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        btnSaveDriver = findViewById(R.id.btnSaveDriver);
        btnAddSampleData = findViewById(R.id.btnAddSampleData);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnSaveDriver.setOnClickListener(v -> saveDriver());
        btnAddSampleData.setOnClickListener(v -> addSampleDrivers());
    }

    private void saveDriver() {
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

        // Create driver object
        BusDriver driver = new BusDriver(busNumber, driverName, fromLocation, toLocation,
                phoneNumber, email);

        // Show loading
        showLoading(true);

        // Save to Firebase
        driverRepository.addDriver(driver, new BusDriverRepository.OnDriverSaveListener() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(AddDriverActivity.this,
                        "Driver added successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(AddDriverActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addSampleDrivers() {
        showLoading(true);

        BusDriver[] sampleDrivers = {
                new BusDriver("WP NA - 8965", "Mr. Surendra Rajapaksha",
                        "Negombo", "Wycherley International School",
                        "+94771234567", "surendra@example.com"),
                new BusDriver("WP NA - 8966", "Mr. Kamal Silva",
                        "Colombo", "Wycherley International School",
                        "+94772345678", "kamal@example.com"),
                new BusDriver("WP NA - 8967", "Mr. Nimal Fernando",
                        "Gampaha", "Wycherley International School",
                        "+94773456789", "nimal@example.com"),
                new BusDriver("WP KA - 1234", "Mr. Anil Perera",
                        "Kandy", "Wycherley International School",
                        "+94774567890", "anil@example.com"),
                new BusDriver("WP CO - 5678", "Mr. Sisira Kumara",
                        "Kurunegala", "Wycherley International School",
                        "+94775678901", "sisira@example.com")
        };

        addDriversRecursively(sampleDrivers, 0);
    }

    private void addDriversRecursively(BusDriver[] drivers, int index) {
        if (index >= drivers.length) {
            showLoading(false);
            Toast.makeText(this,
                    "All sample drivers added successfully!", Toast.LENGTH_LONG).show();
            return;
        }

        driverRepository.addDriver(drivers[index], new BusDriverRepository.OnDriverSaveListener() {
            @Override
            public void onSuccess() {
                // Add next driver
                addDriversRecursively(drivers, index + 1);
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(AddDriverActivity.this,
                        "Error adding driver " + (index + 1) + ": " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        etBusNumber.setText("");
        etDriverName.setText("");
        etFromLocation.setText("");
        etToLocation.setText("");
        etPhoneNumber.setText("");
        etEmail.setText("");
        etBusNumber.requestFocus();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSaveDriver.setEnabled(!show);
        btnAddSampleData.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}