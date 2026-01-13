package com.wycherley.trackmybus.utils;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wycherley.trackmybus.models.BusDriver;

/**
 * Utility class to seed initial data into Firebase
 * Run this once to populate your database with sample drivers
 */
public class FirebaseDataSeeder {
    private static final String TAG = "FirebaseDataSeeder";

    public interface OnSeedCompleteListener {
        void onComplete(boolean success, String message);
    }

    /**
     * Seeds the database with sample bus driver data
     */
    public static void seedBusDrivers(OnSeedCompleteListener listener) {
        DatabaseReference driversRef = FirebaseDatabase.getInstance()
                .getReference("bus_drivers");

        // Create sample drivers
        BusDriver[] drivers = {
                new BusDriver("WP NA - 8965", "Mr. Surendra Rajapaksha",
                        "Negombo", "Wycherley International School",
                        "+94771234567", "surendra@example.com"),

                new BusDriver("WP NA - 8966", "Mr. Kamal Silva",
                        "Colombo", "Wycherley International School",
                        "+94772345678", "kamal@example.com"),

                new BusDriver("WP NA - 8967", "Mr. Nimal Fernando",
                        "Gampaha", "Wycherley International School",
                        "+94773456789", "nimal@example.com"),

                new BusDriver("WP KA - 1234", "Mr. Anil Perera",
                        "Kandy", "Wycherley International School",
                        "+94774567890", "anil@example.com"),

                new BusDriver("WP CO - 5678", "Mr. Sisira Kumara",
                        "Kurunegala", "Wycherley International School",
                        "+94775678901", "sisira@example.com"),

                new BusDriver("WP JA - 9012", "Mr. Priyantha Dias",
                        "Ja-Ela", "Wycherley International School",
                        "+94776789012", "priyantha@example.com"),

                new BusDriver("WP WA - 3456", "Mr. Bandula Wijesinghe",
                        "Wattala", "Wycherley International School",
                        "+94777890123", "bandula@example.com"),

                new BusDriver("WP KE - 7890", "Mr. Ajith Samaraweera",
                        "Kelaniya", "Wycherley International School",
                        "+94778901234", "ajith@example.com")
        };

        // Counter to track completion
        final int[] completed = {0};
        final int total = drivers.length;

        // Add each driver
        for (BusDriver driver : drivers) {
            String driverId = driversRef.push().getKey();
            if (driverId != null) {
                driver.setId(driverId);
                driversRef.child(driverId).setValue(driver)
                        .addOnSuccessListener(aVoid -> {
                            completed[0]++;
                            Log.d(TAG, "Driver added: " + driver.getBusNumber() +
                                    " (" + completed[0] + "/" + total + ")");

                            if (completed[0] == total) {
                                listener.onComplete(true,
                                        "Successfully added " + total + " drivers!");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding driver: " + e.getMessage());
                            listener.onComplete(false,
                                    "Error: " + e.getMessage());
                        });
            }
        }
    }

    /**
     * Clears all bus drivers from the database
     * USE WITH CAUTION!
     */
    public static void clearAllDrivers(OnSeedCompleteListener listener) {
        DatabaseReference driversRef = FirebaseDatabase.getInstance()
                .getReference("bus_drivers");

        driversRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "All drivers cleared");
                    listener.onComplete(true, "All drivers cleared successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error clearing drivers: " + e.getMessage());
                    listener.onComplete(false, "Error: " + e.getMessage());
                });
    }
}