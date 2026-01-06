package com.wycherley.trackmybus.models;

public class Notification {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private long timestamp;
    private boolean isRead;
    private String relatedBusId;
    private String relatedRouteId;

    // No-argument constructor (required for Firebase)
    public Notification() {
        this.notificationId = "";
        this.userId = "";
        this.title = "";
        this.message = "";
        this.type = NotificationType.INFO;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.relatedBusId = "";
        this.relatedRouteId = "";
    }

    // Constructor with parameters
    public Notification(String notificationId, String userId, String title, String message, NotificationType type) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.relatedBusId = "";
        this.relatedRouteId = "";
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getRelatedBusId() { return relatedBusId; }
    public void setRelatedBusId(String relatedBusId) { this.relatedBusId = relatedBusId; }

    public String getRelatedRouteId() { return relatedRouteId; }
    public void setRelatedRouteId(String relatedRouteId) { this.relatedRouteId = relatedRouteId; }
}