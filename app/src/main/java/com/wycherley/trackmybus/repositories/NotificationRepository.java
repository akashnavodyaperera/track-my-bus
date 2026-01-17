package com.wycherley.trackmybus.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wycherley.trackmybus.models.Notification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    private static final String NOTIFICATIONS_PATH = "notifications";

    private static NotificationRepository instance;
    private final DatabaseReference notificationsRef;

    private NotificationRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notificationsRef = database.getReference(NOTIFICATIONS_PATH);
    }

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface OnNotificationsLoadListener {
        void onNotificationsLoaded(List<Notification> notifications);
        void onError(String error);
    }

    public interface OnNotificationActionListener {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Get all notifications for a specific user
     */
    public void getUserNotifications(String userId, OnNotificationsLoadListener listener) {
        Query query = notificationsRef.orderByChild("userId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot notifSnapshot : snapshot.getChildren()) {
                    Notification notification = notifSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        notification.setId(notifSnapshot.getKey());
                        notifications.add(notification);
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(notifications, (n1, n2) ->
                        Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                Log.d(TAG, "Loaded " + notifications.size() + " notifications for user: " + userId);
                listener.onNotificationsLoaded(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading notifications: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    /**
     * Get unread notifications count
     */
    public void getUnreadCount(String userId, OnUnreadCountListener listener) {
        Query query = notificationsRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = 0;
                for (DataSnapshot notifSnapshot : snapshot.getChildren()) {
                    Notification notification = notifSnapshot.getValue(Notification.class);
                    if (notification != null && !notification.isRead()) {
                        unreadCount++;
                    }
                }
                listener.onCountLoaded(unreadCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCountLoaded(0);
            }
        });
    }

    public interface OnUnreadCountListener {
        void onCountLoaded(int count);
    }

    /**
     * Add a new notification
     */
    public void addNotification(Notification notification, OnNotificationActionListener listener) {
        String notificationId = notificationsRef.push().getKey();
        if (notificationId != null) {
            notification.setId(notificationId);
            notificationsRef.child(notificationId).setValue(notification)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Notification added successfully");
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding notification: " + e.getMessage());
                        listener.onError(e.getMessage());
                    });
        } else {
            listener.onError("Failed to generate notification ID");
        }
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(String notificationId, OnNotificationActionListener listener) {
        notificationsRef.child(notificationId).child("read").setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notification marked as read");
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error marking notification as read: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    /**
     * Delete a notification
     */
    public void deleteNotification(String notificationId, OnNotificationActionListener listener) {
        notificationsRef.child(notificationId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notification deleted successfully");
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting notification: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    /**
     * Clear all notifications for a user
     */
    public void clearAllNotifications(String userId, OnNotificationActionListener listener) {
        Query query = notificationsRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notifSnapshot : snapshot.getChildren()) {
                    notifSnapshot.getRef().removeValue();
                }
                Log.d(TAG, "All notifications cleared for user: " + userId);
                listener.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error clearing notifications: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }

    /**
     * Send arrival notification to parent
     */
    public void sendArrivalNotification(String userId, String busNumber, String location) {
        Notification notification = new Notification(
                userId,
                "Track My Bus",
                "Your Child is safely arrive to the school",
                "ARRIVAL",
                busNumber,
                null
        );

        addNotification(notification, new OnNotificationActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Arrival notification sent");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to send arrival notification: " + error);
            }
        });
    }
}