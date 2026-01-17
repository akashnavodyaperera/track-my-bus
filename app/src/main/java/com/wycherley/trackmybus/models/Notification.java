package com.wycherley.trackmybus.models;

public class Notification {
    private String id;
    private String userId; // Parent user ID
    private String title;
    private String message;
    private String type; // "ARRIVAL", "DEPARTURE", "DELAY", "GENERAL"
    private long timestamp;
    private boolean isRead;
    private String busNumber;
    private String driverName;

    // Empty constructor for Firebase
    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Full constructor
    public Notification(String userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Constructor with bus info
    public Notification(String userId, String title, String message, String type,
                        String busNumber, String driverName) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    // Get formatted date
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    // Get formatted time
    public String getFormattedTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}