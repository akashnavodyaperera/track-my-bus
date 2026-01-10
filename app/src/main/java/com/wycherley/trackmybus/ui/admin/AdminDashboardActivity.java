package com.wycherley.trackmybus.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.ui.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextView tvWelcome, tvRole;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        authRepository = AuthRepository.getInstance();

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);

        // Get current user
        FirebaseUser user = authRepository.getCurrentUser();
        if (user != null) {
            tvWelcome.setText("Welcome, Administrator!\n\n" + user.getEmail());
            tvRole.setText("Role: ADMIN");
        }

        // Set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            authRepository.signOut();
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}