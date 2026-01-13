package com.wycherley.trackmybus.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.UserRepository;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.ui.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvRole;
    private Button btnManageBuses, btnManageDrivers, btnManageRoutes,
            btnViewReports, btnLogout;

    private AuthRepository authRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authRepository = AuthRepository.getInstance();
        userRepository = UserRepository.getInstance();

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvRole = findViewById(R.id.tvRole);
        btnManageBuses = findViewById(R.id.btnManageBuses);
        btnManageDrivers = findViewById(R.id.btnManageDrivers);
        btnManageRoutes = findViewById(R.id.btnManageRoutes);
        btnViewReports = findViewById(R.id.btnViewReports);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            userRepository.getUserById(userId, new UserRepository.OnUserLoadListener() {
                @Override
                public void onUserLoaded(User user) {
                    tvWelcome.setText("Welcome, " + user.getName() + "!");
                    tvRole.setText("Role: " + user.getRole().toString());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminDashboardActivity.this,
                            "Error loading user: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupListeners() {
        btnManageBuses.setOnClickListener(v -> {
            // TODO: Navigate to Manage Buses activity
            Toast.makeText(this, "Manage Buses - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        btnManageDrivers.setOnClickListener(v -> {
            // Navigate to Manage Drivers
            Intent intent = new Intent(AdminDashboardActivity.this, ManageDriversActivity.class);
            startActivity(intent);
        });

        btnManageRoutes.setOnClickListener(v -> {
            // TODO: Navigate to Manage Routes activity
            Toast.makeText(this, "Manage Routes - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        btnViewReports.setOnClickListener(v -> {
            // TODO: Navigate to View Reports activity
            Toast.makeText(this, "View Reports - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authRepository.signOut();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}