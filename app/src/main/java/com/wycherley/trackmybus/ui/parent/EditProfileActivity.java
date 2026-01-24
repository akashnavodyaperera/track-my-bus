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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.repositories.AuthRepository;
import com.wycherley.trackmybus.repositories.UserRepository;
import com.wycherley.trackmybus.utils.ImageHelper;

import java.io.File;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private ImageView ivProfileImage;
    private ImageButton btnEditPhoto;
    private TextInputEditText etName, etEmail, etPhone;
    private TextView tvRole;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private User currentUser;
    private String currentUserId;

    private Uri imageUri;
    private Uri cameraImageUri;
    private String newProfileImageBase64 = null;

    // Activity Result Launchers
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        authRepository = AuthRepository.getInstance();
        userRepository = UserRepository.getInstance();

        initViews();
        initActivityResultLaunchers();
        loadUserData();
        setupButtons();
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        tvRole = findViewById(R.id.tvRole);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        processNewImage(uri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        imageUri = cameraImageUri;
                        processNewImage(cameraImageUri);
                    }
                }
        );

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
        if (firebaseUser == null) {
            finish();
            return;
        }

        currentUserId = firebaseUser.getUid();
        showLoading(true);

        userRepository.getUserById(currentUserId, new UserRepository.OnUserLoadListener() {
            @Override
            public void onUserLoaded(User user) {
                showLoading(false);
                currentUser = user;
                displayUserData(user);
                loadProfileImage();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayUserData(User user) {
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhoneNumber());
        tvRole.setText(user.getRole().toString());
    }

    private void loadProfileImage() {
        userRepository.loadProfileImageBase64(currentUserId,
                new UserRepository.OnBase64LoadListener() {
                    @Override
                    public void onImageLoaded(String base64Image) {
                        displayBase64Image(base64Image);
                    }

                    @Override
                    public void onNoImage() {
                        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Failed to load image: " + error);
                        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
                    }
                });
    }

    private void displayBase64Image(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = ImageHelper.base64ToBitmap(base64Image);
            if (bitmap != null) {
                ivProfileImage.setImageBitmap(bitmap);
            } else {
                ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
            }
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
        }
    }

    private void setupButtons() {
        btnEditPhoto.setOnClickListener(v -> showImagePickerDialog());

        btnSave.setOnClickListener(v -> saveProfileChanges());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Remove Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Photo")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            checkCameraPermissionAndOpen();
                            break;
                        case 1:
                            checkStoragePermissionAndOpen();
                            break;
                        case 2:
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

    private void processNewImage(Uri imageUri) {
        Log.d(TAG, "Processing new image...");

        // Convert to Base64 immediately
        String base64Image = ImageHelper.imageUriToBase64(this, imageUri);

        if (base64Image == null) {
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            return;
        }

        int sizeKB = ImageHelper.getBase64SizeKB(base64Image);
        if (sizeKB > 900) {
            Toast.makeText(this, "Image too large. Please use a smaller image.", Toast.LENGTH_LONG).show();
            return;
        }

        // Store for saving later
        newProfileImageBase64 = base64Image;

        // Display the new image
        displayBase64Image(base64Image);

        Toast.makeText(this, "Image ready. Click 'Save Changes' to update.", Toast.LENGTH_SHORT).show();
    }

    private void removeProfilePhoto() {
        newProfileImageBase64 = ""; // Empty string means remove
        ivProfileImage.setImageResource(R.drawable.ic_person_placeholder);
        Toast.makeText(this, "Photo will be removed when you save.", Toast.LENGTH_SHORT).show();
    }

    private void saveProfileChanges() {
        String newName = etName.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        // Validation
        if (newName.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (newPhone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        showLoading(true);
        btnSave.setEnabled(false);

        // Update user object
        currentUser.setName(newName);
        currentUser.setPhoneNumber(newPhone);

        // Save user data
        userRepository.updateUser(currentUser, new UserRepository.OnUpdateCompleteListener() {
            @Override
            public void onSuccess(String message) {
                // If image was changed, save it too
                if (newProfileImageBase64 != null) {
                    saveProfileImage();
                } else {
                    showLoading(false);
                    btnSave.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this,
                            "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    // Return to previous screen
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                btnSave.setEnabled(true);
                Toast.makeText(EditProfileActivity.this,
                        "Failed to update profile: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProfileImage() {
        if (newProfileImageBase64.isEmpty()) {
            // Remove image
            userRepository.removeProfileImageBase64(currentUserId,
                    new UserRepository.OnUpdateCompleteListener() {
                        @Override
                        public void onSuccess(String message) {
                            showLoading(false);
                            btnSave.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            showLoading(false);
                            btnSave.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated but failed to remove photo", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
        } else {
            // Save new image using the repository method instead of accessing private usersRef
            userRepository.updateProfileImageBase64(currentUserId, newProfileImageBase64,
                    new UserRepository.OnUpdateCompleteListener() {
                        @Override
                        public void onSuccess(String message) {
                            showLoading(false);
                            btnSave.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            showLoading(false);
                            btnSave.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated but failed to save photo: " + error, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}