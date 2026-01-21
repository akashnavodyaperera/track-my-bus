package com.wycherley.trackmybus.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private DatabaseReference usersRef;
    private StorageReference storageRef;

    private UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF);

        try {
            storageRef = FirebaseStorage.getInstance().getReference();
            Log.d(TAG, "Firebase Storage initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase Storage", e);
        }
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

    // ==================== PROFILE IMAGE METHODS ====================

    // Upload profile image to Firebase Storage
    public void uploadProfileImage(String userId, Uri imageUri, OnImageUploadListener listener) {
        if (storageRef == null) {
            listener.onFailure("Firebase Storage not initialized. Enable it in Firebase Console.");
            return;
        }

        Log.d(TAG, "Uploading image for user: " + userId);
        StorageReference profileImagesRef = storageRef.child("profile_images/" + userId + ".jpg");

        profileImagesRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImagesRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateProfileImageUrl(userId, imageUrl, listener);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to get download URL", e);
                                listener.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // Update profile image URL in database
    private void updateProfileImageUrl(String userId, String imageUrl, OnImageUploadListener listener) {
        usersRef.child(userId).child("profileImageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile image URL updated");
                    listener.onSuccess(imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update profile image URL", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // Remove profile image
    public void removeProfileImage(String userId, OnUpdateCompleteListener listener) {
        StorageReference profileImageRef = storageRef.child("profile_images/" + userId + ".jpg");
        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    usersRef.child(userId).child("profileImageUrl").setValue("")
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "Profile image removed");
                                listener.onSuccess("Image removed");
                            })
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    usersRef.child(userId).child("profileImageUrl").setValue("")
                            .addOnSuccessListener(aVoid2 -> listener.onSuccess("Image removed"))
                            .addOnFailureListener(e2 -> listener.onFailure(e2.getMessage()));
                });
    }

    // ==================== MY BUS METHODS (PARENT) ====================

    // Set "My Bus" for parent (saves to Firebase)
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

    // Get "My Bus" ID for parent
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

    // Remove "My Bus" assignment
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

    // Check if user has My Bus assigned
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