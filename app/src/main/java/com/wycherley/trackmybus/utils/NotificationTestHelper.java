package com.wycherley.trackmybus.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.models.Notification;
import com.wycherley.trackmybus.repositories.NotificationRepository;

/**
 * Helper class to create test notifications
 * Use this during development to test the notification system
 */
public class NotificationTestHelper {

    public interface OnTestCompleteListener {
        void onComplete(boolean success, String message);
    }

    /**
     * Create sample notifications for current user
     */
    public static void createSampleNotifications(OnTestCompleteListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onComplete(false, "User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        NotificationRepository repo = NotificationRepository.getInstance();

        // Sample notifications
        Notification[] notifications = {
                new Notification(
                        userId,
                        "Track My Bus",
                        "Your Child is safely arrive to the school",
                        "ARRIVAL",
                        "WP NA - 8965",
                        "Mr. Surendra Rajapaksha"
                ),
                new Notification(
                        userId,
                        "Track My Bus",
                        "Bus has departed from Negombo",
                        "DEPARTURE",
                        "WP NA - 8965",
                        "Mr. Surendra Rajapaksha"
                ),
                new Notification(
                        userId,
                        "Track My Bus",
                        "Bus is running 10 minutes late due to traffic",
                        "DELAY",
                        "WP NA - 8965",
                        "Mr. Surendra Rajapaksha"
                ),
                new Notification(
                        userId,
                        "Track My Bus",
                        "Your child has been picked up safely",
                        "PICKUP",
                        "WP NA - 8965",
                        "Mr. Surendra Rajapaksha"
                ),
                new Notification(
                        userId,
                        "Track My Bus",
                        "School will be closed tomorrow due to holiday",
                        "GENERAL",
                        null,
                        null
                )
        };

        final int[] completed = {0};
        final int total = notifications.length;

        for (Notification notification : notifications) {
            repo.addNotification(notification, new NotificationRepository.OnNotificationActionListener() {
                @Override
                public void onSuccess() {
                    completed[0]++;
                    if (completed[0] == total) {
                        listener.onComplete(true,
                                "Created " + total + " sample notifications!");
                    }
                }

                @Override
                public void onError(String error) {
                    listener.onComplete(false, "Error: " + error);
                }
            });
        }
    }

    /**
     * Create a single arrival notification
     */
    public static void createArrivalNotification(String busNumber, OnTestCompleteListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onComplete(false, "User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        Notification notification = new Notification(
                userId,
                "Track My Bus",
                "Your Child is safely arrive to the school",
                "ARRIVAL",
                busNumber,
                null
        );

        NotificationRepository.getInstance().addNotification(notification,
                new NotificationRepository.OnNotificationActionListener() {
                    @Override
                    public void onSuccess() {
                        listener.onComplete(true, "Arrival notification created!");
                    }

                    @Override
                    public void onError(String error) {
                        listener.onComplete(false, "Error: " + error);
                    }
                });
    }

    /**
     * Clear all notifications for current user (for testing cleanup)
     */
    public static void clearAllNotifications(OnTestCompleteListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onComplete(false, "User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        NotificationRepository.getInstance().clearAllNotifications(userId,
                new NotificationRepository.OnNotificationActionListener() {
                    @Override
                    public void onSuccess() {
                        listener.onComplete(true, "All notifications cleared!");
                    }

                    @Override
                    public void onError(String error) {
                        listener.onComplete(false, "Error: " + error);
                    }
                });
    }
}