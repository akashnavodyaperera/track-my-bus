package com.wycherley.trackmybus.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripHistory {
    private String id;
    private String driverId;
    private String busNumber;
    private String driverName;
    private String userId; // Student/Parent user ID
    private String tripType; // "MORNING_DROPOFF" or "AFTERNOON_PICKUP"
    private long timestamp; // When the trip occurred
    private String location; // Where drop-off/pickup happened
    private double latitude;
    private double longitude;
    private String date; // Date in format "yyyy-MM-dd"

    // Empty constructor for Firebase
    public TripHistory() {
    }

    // Full constructor
    public TripHistory(String driverId, String busNumber, String driverName,
                       String userId, String tripType, long timestamp,
                       String location, double latitude, double longitude) {
        this.driverId = driverId;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.userId = userId;
        this.tripType = tripType;
        this.timestamp = timestamp;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;

        // Set date from timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.date = sdf.format(new Date(timestamp));
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Helper methods
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public boolean isMorningTrip() {
        return "MORNING_DROPOFF".equals(tripType);
    }

    public boolean isAfternoonTrip() {
        return "AFTERNOON_PICKUP".equals(tripType);
    }

    public String getTripTypeDisplay() {
        return isMorningTrip() ? "Morning Drop-off" : "Afternoon Pick-up";
    }
}