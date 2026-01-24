package com.wycherley.trackmybus.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.utils.Constants;
import com.wycherley.trackmybus.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private DatabaseReference usersRef;

    private UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF);
        Log.d(TAG, "UserRepository initialized");
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    // ==================== USER PROFILE METHODS ====================

    // Get user by ID
    public void getUserById(String userId, OnUserLoadListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    listener.onUserLoaded(user);
                } else {
                    listener.onError("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    // Update user profile
    public void updateUser(User user, OnUpdateCompleteListener listener) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated successfully");
                    listener.onSuccess("Profile updated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update user", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // ==================== PROFILE IMAGE METHODS (BASE64 - NO STORAGE NEEDED) ====================

    /**
     * Upload profile image as Base64 (NO Firebase Storage needed - 100% FREE!)
     * Stores image directly in Realtime Database
     */
    public void uploadProfileImageBase64(Context context, String userId, Uri imageUri,
                                         OnImageUploadListener listener) {
        Log.d(TAG, "Converting image to Base64...");

        // Convert image to Base64 string
        String base64Image = ImageHelper.imageUriToBase64(context, imageUri);

        if (base64Image == null) {
            listener.onFailure("Failed to process image");
            return;
        }

        // Check size (Firebase Realtime Database has 1MB limit per value)
        int sizeKB = ImageHelper.getBase64SizeKB(base64Image);
        Log.d(TAG, "Base64 image size: " + sizeKB + " KB");

        if (sizeKB > 900) { // Leave some margin below 1MB limit
            listener.onFailure("Image too large (" + sizeKB + " KB). Please use a smaller image.");
            return;
        }

        Log.d(TAG, "Uploading Base64 image to database...");

        // Save Base64 string to database
        updateProfileImageBase64(userId, base64Image, new OnUpdateCompleteListener() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "✅ Profile image saved to database");
                listener.onSuccess(base64Image);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "❌ Failed to save image: " + error);
                listener.onFailure(error);
            }
        });
    }

    /**
     * Update profile image Base64 string directly
     */
    public void updateProfileImageBase64(String userId, String base64Image, OnUpdateCompleteListener listener) {
        usersRef.child(userId).child("profileImageBase64").setValue(base64Image)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Profile image updated in database");
                    listener.onSuccess("Image updated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to update image", e);
                    listener.onFailure(e.getMessage());
                });
    }

    /**
     * Load profile image Base64 from database
     */
    public void loadProfileImageBase64(String userId, OnBase64LoadListener listener) {
        Log.d(TAG, "Loading profile image from database...");

        usersRef.child(userId).child("profileImageBase64").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        String base64Image = snapshot.getValue(String.class);
                        if (base64Image != null && !base64Image.isEmpty()) {
                            Log.d(TAG, "✅ Profile image loaded (" +
                                    ImageHelper.getBase64SizeKB(base64Image) + " KB)");
                            listener.onImageLoaded(base64Image);
                        } else {
                            Log.d(TAG, "Profile image is empty");
                            listener.onNoImage();
                        }
                    } else {
                        Log.d(TAG, "No profile image found");
                        listener.onNoImage();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to load image", e);
                    listener.onError(e.getMessage());
                });
    }

    /**
     * Remove profile image from database
     */
    public void removeProfileImageBase64(String userId, OnUpdateCompleteListener listener) {
        Log.d(TAG, "Removing profile image...");

        usersRef.child(userId).child("profileImageBase64").removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Profile image removed");
                    listener.onSuccess("Image removed");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to remove image", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // ==================== MY BUS METHODS (PARENT) ====================

    /**
     * Set "My Bus" for parent (saves to Firebase)
     */
    public void setMyBus(String userId, String busDriverId, OnUpdateCompleteListener listener) {
        Log.d(TAG, "Setting My Bus: " + busDriverId + " for user: " + userId);

        List<String> busIds = new ArrayList<>();
        busIds.add(busDriverId);

        usersRef.child(userId).child("assignedBusIds").setValue(busIds)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ My Bus saved to Firebase successfully");
                    listener.onSuccess("My Bus assigned successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to save My Bus to Firebase", e);
                    listener.onFailure("Failed to assign bus: " + e.getMessage());
                });
    }

    /**
     * Get "My Bus" ID for parent
     */
    public void getMyBusId(String userId, OnMyBusLoadListener listener) {
        Log.d(TAG, "Getting My Bus for user: " + userId);

        usersRef.child(userId).child("assignedBusIds").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        try {
                            GenericTypeIndicator<List<String>> typeIndicator =
                                    new GenericTypeIndicator<List<String>>() {};
                            List<String> busIds = snapshot.getValue(typeIndicator);

                            if (busIds != null && !busIds.isEmpty()) {
                                String myBusId = busIds.get(0);
                                Log.d(TAG, "✅ My Bus found: " + myBusId);
                                listener.onMyBusLoaded(myBusId);
                            } else {
                                Log.d(TAG, "No bus assigned");
                                listener.onNoBusAssigned();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing My Bus", e);
                            listener.onError(e.getMessage());
                        }
                    } else {
                        Log.d(TAG, "No bus assigned");
                        listener.onNoBusAssigned();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load My Bus", e);
                    listener.onError(e.getMessage());
                });
    }

    /**
     * Remove "My Bus" assignment
     */
    public void removeMyBus(String userId, OnUpdateCompleteListener listener) {
        Log.d(TAG, "Removing My Bus for user: " + userId);

        usersRef.child(userId).child("assignedBusIds").setValue(new ArrayList<String>())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "My Bus removed successfully");
                    listener.onSuccess("Bus unassigned");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove My Bus", e);
                    listener.onFailure(e.getMessage());
                });
    }

    /**
     * Check if user has My Bus assigned
     */
    public void hasMyBus(String userId, OnMyBusCheckListener listener) {
        usersRef.child(userId).child("assignedBusIds").get()
                .addOnSuccessListener(snapshot -> {
                    boolean hasBus = false;

                    if (snapshot.exists() && snapshot.getValue() != null) {
                        try {
                            GenericTypeIndicator<List<String>> typeIndicator =
                                    new GenericTypeIndicator<List<String>>() {};
                            List<String> busIds = snapshot.getValue(typeIndicator);
                            hasBus = (busIds != null && !busIds.isEmpty());
                        } catch (Exception e) {
                            Log.e(TAG, "Error checking My Bus", e);
                        }
                    }

                    listener.onChecked(hasBus);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to check My Bus status", e);
                    listener.onError(e.getMessage());
                });
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface OnUserLoadListener {
        void onUserLoaded(User user);
        void onError(String error);
    }

    public interface OnUpdateCompleteListener {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    public interface OnBase64LoadListener {
        void onImageLoaded(String base64Image);
        void onNoImage();
        void onError(String error);
    }

    public interface OnMyBusLoadListener {
        void onMyBusLoaded(String busDriverId);
        void onNoBusAssigned();
        void onError(String error);
    }

    public interface OnMyBusCheckListener {
        void onChecked(boolean hasBus);
        void onError(String error);
    }
}