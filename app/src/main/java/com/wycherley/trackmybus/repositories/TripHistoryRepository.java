package com.wycherley.trackmybus.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wycherley.trackmybus.models.TripHistory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TripHistoryRepository {
    private static final String TAG = "TripHistoryRepository";
    private static final String TRIP_HISTORY_PATH = "trip_history";

    private static TripHistoryRepository instance;
    private final DatabaseReference tripHistoryRef;

    private TripHistoryRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tripHistoryRef = database.getReference(TRIP_HISTORY_PATH);
    }

    public static synchronized TripHistoryRepository getInstance() {
        if (instance == null) {
            instance = new TripHistoryRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface OnTripHistoryLoadListener {
        void onHistoryLoaded(List<TripHistory> trips);
        void onError(String error);
    }

    public interface OnTripSaveListener {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Save a trip record
     */
    public void saveTrip(TripHistory trip, OnTripSaveListener listener) {
        String tripId = tripHistoryRef.push().getKey();
        if (tripId != null) {
            trip.setId(tripId);
            tripHistoryRef.child(tripId).setValue(trip)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Trip saved successfully: " + tripId);
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving trip: " + e.getMessage());
                        listener.onError(e.getMessage());
                    });
        } else {
            listener.onError("Failed to generate trip ID");
        }
    }

    /**
     * Get all trip history for a specific user
     */
    public void getUserTripHistory(String userId, OnTripHistoryLoadListener listener) {
        Query query = tripHistoryRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TripHistory> trips = new ArrayList<>();
                for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                    TripHistory trip = tripSnapshot.getValue(TripHistory.class);
                    if (trip != null) {
                        trip.setId(tripSnapshot.getKey());
                        trips.add(trip);
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(trips, (t1, t2) ->
                        Long.compare(t2.getTimestamp(), t1.getTimestamp()));

                Log.d(TAG, "Loaded " + trips.size() + " trips for user: " + userId);
                listener.onHistoryLoaded(trips);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading trip history: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }