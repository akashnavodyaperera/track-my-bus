package com.wycherley.trackmybus.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wycherley.trackmybus.models.BusDriver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusDriverRepository {
    private static final String TAG = "BusDriverRepository";
    private static final String DRIVERS_PATH = "bus_drivers";

    private static BusDriverRepository instance;
    private final DatabaseReference driversRef;

    private BusDriverRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driversRef = database.getReference(DRIVERS_PATH);
    }

    public static synchronized BusDriverRepository getInstance() {
        if (instance == null) {
            instance = new BusDriverRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface OnDriversLoadListener {
        void onDriversLoaded(List<BusDriver> drivers);
        void onError(String error);
    }

    public interface OnDriverSaveListener {
        void onSuccess();
        void onError(String error);
    }

    // Load all drivers
    public void getAllDrivers(OnDriversLoadListener listener) {
        driversRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BusDriver> drivers = new ArrayList<>();
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    BusDriver driver = driverSnapshot.getValue(BusDriver.class);
                    if (driver != null) {
                        driver.setId(driverSnapshot.getKey());
                        drivers.add(driver);
                    }
                }
                Log.d(TAG, "Loaded " + drivers.size() + " drivers from Firebase");
                listener.onDriversLoaded(drivers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading drivers: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    // Load drivers once (no real-time updates)
    public void getDriversOnce(OnDriversLoadListener listener) {
        driversRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BusDriver> drivers = new ArrayList<>();
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    BusDriver driver = driverSnapshot.getValue(BusDriver.class);
                    if (driver != null) {
                        driver.setId(driverSnapshot.getKey());
                        drivers.add(driver);
                    }
                }
                Log.d(TAG, "Loaded " + drivers.size() + " drivers from Firebase");
                listener.onDriversLoaded(drivers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading drivers: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    // Add a new driver
    public void addDriver(BusDriver driver, OnDriverSaveListener listener) {
        String driverId = driversRef.push().getKey();
        if (driverId != null) {
            driver.setId(driverId);
            driversRef.child(driverId).setValue(driver)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Driver added successfully: " + driverId);
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding driver: " + e.getMessage());
                        listener.onError(e.getMessage());
                    });
        } else {
            listener.onError("Failed to generate driver ID");
        }
    }

    // Update existing driver
    public void updateDriver(BusDriver driver, OnDriverSaveListener listener) {
        if (driver.getId() == null) {
            listener.onError("Driver ID is null");
            return;
        }

        driversRef.child(driver.getId()).setValue(driver)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Driver updated successfully: " + driver.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating driver: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    // Delete driver
    public void deleteDriver(String driverId, OnDriverSaveListener listener) {
        driversRef.child(driverId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Driver deleted successfully: " + driverId);
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting driver: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }


    public void rateDriver(String driverId, String userId, int rating, OnDriverSaveListener listener) {
        if (rating < 1 || rating > 5) {
            listener.onError("Rating must be between 1 and 5");
            return;
        }

        driversRef.child(driverId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                BusDriver driver = snapshot.getValue(BusDriver.class);
                if (driver != null) {
                    // Get existing ratings
                    Map<String, Integer> userRatings = driver.getUserRatings();
                    if (userRatings == null) {
                        userRatings = new HashMap<>();
                    }

                    // Add/update user's rating
                    userRatings.put(userId, rating);

                    // Calculate new average
                    int total = 0;
                    for (Integer r : userRatings.values()) {
                        total += r;
                    }
                    double average = (double) total / userRatings.size();

                    // Update driver
                    driver.setUserRatings(userRatings);
                    driver.setAverageRating(Math.round(average * 10.0) / 10.0); // Round to 1 decimal
                    driver.setTotalRatings(userRatings.size());

                    // Save to Firebase
                    driversRef.child(driverId).setValue(driver)
                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                            .addOnFailureListener(e -> listener.onError(e.getMessage()));
                }
            } else {
                listener.onError("Driver not found");
            }
        }).addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    /**
     * Get user's rating for a driver
     */
    public void getUserRating(String driverId, String userId, OnUserRatingLoadListener listener) {
        driversRef.child(driverId).child("userRatings").child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Integer rating = snapshot.getValue(Integer.class);
                        listener.onRatingLoaded(rating != null ? rating : 0);
                    } else {
                        listener.onRatingLoaded(0);
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Add callback interface
    public interface OnUserRatingLoadListener {
        void onRatingLoaded(int rating);
        void onError(String error);
    }

    // Search drivers by bus number
    public void searchDriverByBusNumber(String busNumber, OnDriversLoadListener listener) {
        driversRef.orderByChild("busNumber")
                .equalTo(busNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<BusDriver> drivers = new ArrayList<>();
                        for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                            BusDriver driver = driverSnapshot.getValue(BusDriver.class);
                            if (driver != null) {
                                driver.setId(driverSnapshot.getKey());
                                drivers.add(driver);
                            }
                        }
                        listener.onDriversLoaded(drivers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }
}