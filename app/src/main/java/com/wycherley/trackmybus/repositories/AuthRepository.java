package com.wycherley.trackmybus.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wycherley.trackmybus.models.User;
import com.wycherley.trackmybus.models.UserRole;
import com.wycherley.trackmybus.utils.Constants;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    // Private constructor (Singleton pattern)
    private AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF);
    }

    // Get singleton instance
    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    // Sign up with email and password
    public void signUp(String email, String password, String name, String phoneNumber,
                       UserRole role, OnAuthCompleteListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();

                                // Create user object
                                User user = new User(userId, email, name, phoneNumber, role);

                                Log.d(TAG, "Creating user with role: " + role);

                                // Save to database
                                saveUserToDatabase(user, listener);
                            }
                        } else {
                            Log.e(TAG, "Sign up failed", task.getException());
                            listener.onFailure(task.getException() != null ?
                                    task.getException().getMessage() : "Sign up failed");
                        }
                    }
                });
    }

    // Sign in with email and password
    public void signIn(String email, String password, OnAuthCompleteListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                Log.d(TAG, "Sign in successful: " + firebaseUser.getUid());
                                listener.onSuccess("Sign in successful");
                            }
                        } else {
                            Log.e(TAG, "Sign in failed", task.getException());
                            listener.onFailure(task.getException() != null ?
                                    task.getException().getMessage() : "Sign in failed");
                        }
                    }
                });
    }

    // Save user to database
    private void saveUserToDatabase(User user, OnAuthCompleteListener listener) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User saved to database with role: " + user.getRole());
                            listener.onSuccess("Account created successfully");
                        } else {
                            Log.e(TAG, "Failed to save user", task.getException());
                            listener.onFailure("Failed to save user data");
                        }
                    }
                });
    }

    // Sign out
    public void signOut() {
        firebaseAuth.signOut();
        Log.d(TAG, "User signed out");
    }

    // Reset password
    public void resetPassword(String email, OnAuthCompleteListener listener) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess("Password reset email sent");
                        } else {
                            listener.onFailure(task.getException() != null ?
                                    task.getException().getMessage() : "Failed to send reset email");
                        }
                    }
                });
    }

    // Callback interface
    public interface OnAuthCompleteListener {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
