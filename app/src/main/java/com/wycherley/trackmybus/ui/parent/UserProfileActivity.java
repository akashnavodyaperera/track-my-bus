package com.wycherley.trackmybus.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.UserRepository;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.ui.auth.LoginActivity;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvRole;
    private Button btnLogout;
    private AuthRepository authRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        authRepository = AuthRepository.getInstance();
        userRepository = UserRepository.getInstance();

        // Set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        loadUserData();
        setupLogout();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            userRepository.getUserById(userId, new UserRepository.OnUserLoadListener() {
                @Override
                public void onUserLoaded(User user) {
                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());
                    tvPhone.setText(user.getPhoneNumber());
                    tvRole.setText("Role: " + user.getRole().toString());
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}