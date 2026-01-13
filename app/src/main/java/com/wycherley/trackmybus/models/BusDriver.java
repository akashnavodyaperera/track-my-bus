package com.wycherley.trackmybus.models;

public class BusDriver {
    private String id; // Firebase key
    private String busNumber;
    private String driverName;
    private String fromLocation;
    private String toLocation;
    private String phoneNumber;
    private String email;
    private boolean isActive;

    // Empty constructor required for Firebase
    public BusDriver() {
        this.isActive = true;
    }

    // Constructor with basic info
    public BusDriver(String busNumber, String driverName, String fromLocation, String toLocation) {
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.isActive = true;
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
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}