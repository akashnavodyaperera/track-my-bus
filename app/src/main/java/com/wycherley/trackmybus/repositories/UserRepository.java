package com.wycherley.trackmybus.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.utils.Constants;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private DatabaseReference usersRef;
    private StorageReference storageRef;

    private UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF);
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

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

    // Upload profile image to Firebase Storage
    public void uploadProfileImage(String userId, Uri imageUri, OnImageUploadListener listener) {
        StorageReference profileImagesRef = storageRef.child("profile_images/" + userId + ".jpg");

        profileImagesRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    profileImagesRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                // Update user profile with image URL
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
        // Delete from Storage
        StorageReference profileImageRef = storageRef.child("profile_images/" + userId + ".jpg");
        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Update database to remove URL
                    usersRef.child(userId).child("profileImageUrl").setValue("")
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "Profile image removed");
                                listener.onSuccess("Image removed");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update database", e);
                                listener.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Even if storage deletion fails, update database
                    usersRef.child(userId).child("profileImageUrl").setValue("")
                            .addOnSuccessListener(aVoid2 -> listener.onSuccess("Image removed"))
                            .addOnFailureListener(e2 -> listener.onFailure(e2.getMessage()));
                });
    }

    // Callback interfaces
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
}