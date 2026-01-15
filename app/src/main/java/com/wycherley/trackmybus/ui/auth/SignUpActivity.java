package com.wycherley.trackmybus.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.UserRole;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.ui.parent.ParentDashboardActivity;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Parent Account");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize repository
        authRepository = AuthRepository.getInstance();

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setClickListeners() {
        btnSignUp.setOnClickListener(v -> handleSignUp());
        tvLogin.setOnClickListener(v -> finish()); // Go back to login
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(name, email, phone, password, confirmPassword)) {
            return;
        }

        // Show progress
        showProgress(true);

        // Sign up as PARENT (hardcoded role)
        authRepository.signUp(email, password, name, phone, UserRole.PARENT,
                new AuthRepository.OnAuthCompleteListener() {
                    @Override
                    public void onSuccess(String message) {
                        showProgress(false);
                        Toast.makeText(SignUpActivity.this,
                                "Account created successfully!", Toast.LENGTH_SHORT).show();
                        navigateToParentDashboard();
                    }

                    @Override
                    public void onFailure(String error) {
                        showProgress(false);
                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String name, String email, String phone,
                                   String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return false;
        }

        if (name.length() < 3) {
            etName.setError("Name must be at least 3 characters");
            etName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            etPhone.setError("Enter a valid phone number (at least 10 digits)");
            etPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!show);
        etName.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPhone.setEnabled(!show);
        etPassword.setEnabled(!show);
        etConfirmPassword.setEnabled(!show);
    }

    private void navigateToParentDashboard() {
        Intent intent = new Intent(SignUpActivity.this, ParentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}