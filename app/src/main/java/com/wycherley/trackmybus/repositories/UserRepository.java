package com.wycherley.trackmybus.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.utils.Constants;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;

    private DatabaseReference usersRef;

    private UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF);
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

    // Callback interfaces
    public interface OnUserLoadListener {
        void onUserLoaded(User user);
        void onError(String error);
    }

    public interface OnUpdateCompleteListener {
        void onSuccess(String message);
        void onFailure(String error);
    }
}