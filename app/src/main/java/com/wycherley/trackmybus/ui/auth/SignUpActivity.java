package com.wycherley.trackmybus.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.UserRole;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.ui.parent.ParentDashboardActivity;
import com.wycherley.trackmybus.utils.EmailValidator;

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

        // Setup real-time validation
        setupEmailValidation();

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

    private void setupEmailValidation() {
        // Real-time email validation as user types
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (!email.isEmpty() && email.contains("@")) {
                    if (!EmailValidator.isValid(email)) {
                        // Check if there's a suggested correction
                        String suggestion = EmailValidator.suggestCorrection(email);
                        if (suggestion != null) {
                            etEmail.setError("Did you mean " + suggestion + "?");
                        } else {
                            etEmail.setError(EmailValidator.getErrorMessage(email));
                        }
                    } else {
                        etEmail.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Validate when focus is lost
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = etEmail.getText().toString().trim();
                if (!email.isEmpty() && !EmailValidator.isValid(email)) {
                    String suggestion = EmailValidator.suggestCorrection(email);
                    if (suggestion != null) {
                        showEmailCorrectionDialog(email, suggestion);
                    } else {
                        etEmail.setError(EmailValidator.getErrorMessage(email));
                    }
                }
            }
        });
    }

    private void showEmailCorrectionDialog(String typed, String suggestion) {
        new AlertDialog.Builder(this)
                .setTitle("Did you mean this?")
                .setMessage("You typed: " + typed + "\n\nDid you mean: " + suggestion + "?")
                .setPositiveButton("Yes, use " + suggestion, (dialog, which) -> {
                    etEmail.setText(suggestion);
                    etEmail.setError(null);
                })
                .setNegativeButton("No, keep my email", null)
                .show();
    }

    private void setClickListeners() {
        btnSignUp.setOnClickListener(v -> handleSignUp());
        tvLogin.setOnClickListener(v -> finish());
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

                        // Handle specific Firebase errors
                        String errorMessage = error;
                        if (error.contains("email address is already in use")) {
                            errorMessage = "This email is already registered. Please login instead.";
                        } else if (error.contains("network error")) {
                            errorMessage = "Network error. Please check your internet connection.";
                        }

                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String name, String email, String phone,
                                   String password, String confirmPassword) {
        // Name validation
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

        // Only allow letters and spaces in name
        if (!name.matches("[a-zA-Z ]+")) {
            etName.setError("Name can only contain letters and spaces");
            etName.requestFocus();
            return false;
        }

        // Email validation with EmailValidator utility
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!EmailValidator.isValid(email)) {
            String suggestion = EmailValidator.suggestCorrection(email);
            if (suggestion != null) {
                etEmail.setError("Did you mean " + suggestion + "?");
            } else {
                etEmail.setError(EmailValidator.getErrorMessage(email));
            }
            etEmail.requestFocus();
            return false;
        }

        // Phone validation
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        // Remove any spaces, hyphens, or parentheses
        String cleanPhone = phone.replaceAll("[\\s()-]", "");

        // Check if it's a valid Sri Lankan number or international format
        if (cleanPhone.length() < 10) {
            etPhone.setError("Phone number must be at least 10 digits");
            etPhone.requestFocus();
            return false;
        }

        // Only allow numbers, spaces, hyphens, parentheses, and + for country code
        if (!phone.matches("[0-9\\s()+-]+")) {
            etPhone.setError("Invalid phone number format");
            etPhone.requestFocus();
            return false;
        }

        // Password validation
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

        // Strong password check (optional but recommended)
        if (!password.matches(".*[A-Za-z].*")) {
            etPassword.setError("Password must contain at least one letter");
            etPassword.requestFocus();
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            etPassword.setError("Password must contain at least one number");
            etPassword.requestFocus();
            return false;
        }

        // Confirm password validation
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