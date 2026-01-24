package com.wycherley.trackmybus.ui.parent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.UserRepository;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.ui.auth.LoginActivity;
import com.wycherley.trackmybus.utils.ImageHelper;

import java.io.File;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";

    private TextView tvName, tvEmail, tvPhone, tvRole;
    private Button btnLogout, btnEditProfile;
    private ImageView ivProfileImage;
    private ImageButton btnEditPhoto;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;

    private Uri imageUri;
    private Uri cameraImageUri;

    // Activity Result Launchers
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

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
        initActivityResultLaunchers();
        loadUserData();
        setupLogout();
        setupPhotoEdit();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initActivityResultLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        uploadProfileImage(uri);
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        imageUri = cameraImageUri;
                        uploadProfileImage(cameraImageUri);
                    }
                }
        );

        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Storage permission launcher
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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

                    // Load profile image from Base64
                    loadProfileImageFromDatabase(userId);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UserProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadProfileImageFromDatabase(String userId) {
        Log.d(TAG, "Loading profile image from database...");

        userRepository.loadProfileImageBase64(userId,
                new UserRepository.OnBase64LoadListener() {
                    @Override
                    public void onImageLoaded(String base64Image) {
                        Log.d(TAG, "✅ Profile image loaded from database");
                        displayBase64Image(base64Image);
                    }

                    @Override
                    public void onNoImage() {
                        Log.d(TAG, "No profile image found");
                        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Failed to load image: " + error);
                        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
                    }
                });
    }

    private void displayBase64Image(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = ImageHelper.base64ToBitmap(base64Image);
            if (bitmap != null) {
                ivProfileImage.setImageBitmap(bitmap);
                Log.d(TAG, "Image displayed successfully");
            } else {
                Log.e(TAG, "Failed to convert Base64 to Bitmap");
                ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
            }
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
        }
    }

    private void setupPhotoEdit() {
        btnEditPhoto.setOnClickListener(v -> showImagePickerDialog());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Remove Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Photo")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Camera
                            checkCameraPermissionAndOpen();
                            break;
                        case 1: // Gallery
                            checkStoragePermissionAndOpen();
                            break;
                        case 2: // Remove
                            removeProfilePhoto();
                            break;
                    }
                });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void checkStoragePermissionAndOpen() {
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            openGallery();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "profile_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        progressBar.setVisibility(View.VISIBLE);
        btnEditPhoto.setEnabled(false);

        Log.d(TAG, "Starting image upload (Base64)...");

        // Use Base64 upload method (NO Firebase Storage needed)
        userRepository.uploadProfileImageBase64(this, userId, imageUri,
                new UserRepository.OnImageUploadListener() {
                    @Override
                    public void onSuccess(String base64Image) {
                        progressBar.setVisibility(View.GONE);
                        btnEditPhoto.setEnabled(true);

                        Log.d(TAG, "✅ Profile photo uploaded successfully");

                        // Display the image
                        displayBase64Image(base64Image);

                        Toast.makeText(UserProfileActivity.this,
                                "Profile photo updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressBar.setVisibility(View.GONE);
                        btnEditPhoto.setEnabled(true);

                        Log.e(TAG, "❌ Failed to upload photo: " + error);

                        Toast.makeText(UserProfileActivity.this,
                                "Failed to upload photo: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void removeProfilePhoto() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "Removing profile photo...");

        // Remove Base64 image from database
        userRepository.removeProfileImageBase64(userId,
                new UserRepository.OnUpdateCompleteListener() {
                    @Override
                    public void onSuccess(String message) {
                        progressBar.setVisibility(View.GONE);
                        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);

                        Log.d(TAG, "✅ Profile photo removed");
                        Toast.makeText(UserProfileActivity.this,
                                "Profile photo removed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressBar.setVisibility(View.GONE);

                        Log.e(TAG, "❌ Failed to remove photo: " + error);
                        Toast.makeText(UserProfileActivity.this,
                                "Failed to remove photo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupLogout() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivityForResult(intent, 100);
        });

        btnLogout.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh user data after editing
            loadUserData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}