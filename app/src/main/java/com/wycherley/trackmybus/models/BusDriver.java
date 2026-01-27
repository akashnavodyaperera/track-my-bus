package com.wycherley.trackmybus.models;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

public class BusDriver {
    private String id; // Firebase key
    private String busNumber;
    private String driverName;
    private String fromLocation;
    private String toLocation;
    private String phoneNumber;
    private String email;
    private boolean isActive;

    // Rating fields
    private double averageRating; // Average rating (0.0 to 5.0)
    private int totalRatings; // Total number of ratings
    private Map<String, Integer> userRatings; // userId -> rating

    // Empty constructor required for Firebase
    public BusDriver() {
        this.isActive = true;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.userRatings = new HashMap<>();
    }

    // Constructor with basic info
    public BusDriver(String busNumber, String driverName, String fromLocation, String toLocation) {
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.isActive = true;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.userRatings = new HashMap<>();
    }

    // Full constructor
    public BusDriver(String busNumber, String driverName, String fromLocation,
                     String toLocation, String phoneNumber, String email) {
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isActive = true;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.userRatings = new HashMap<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Rating getters and setters
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public Map<String, Integer> getUserRatings() {
        if (userRatings == null) {
            userRatings = new HashMap<>();
        }
        return userRatings;
    }
    public void setUserRatings(Map<String, Integer> userRatings) {
        this.userRatings = userRatings;
    }

    // Helper method to get star string - EXCLUDED from Firebase
    @Exclude
    public String getStarString() {
        if (averageRating == 0) {
            return "☆☆☆☆☆"; // Empty stars
        }

        int fullStars = (int) averageRating;
        boolean hasHalfStar = (averageRating - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();

        // Add full stars
        for (int i = 0; i < fullStars && i < 5; i++) {
            stars.append("⭐");
        }

        // Add half star if needed
        if (hasHalfStar && fullStars < 5) {
            stars.append("✨");
        }

        // Add empty stars to complete 5 stars
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }

        return stars.toString();
    }
}