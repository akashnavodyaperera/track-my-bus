package com.wycherley.trackmybus.models;

public class Bus {
    private String busId;
    private String busNumber;
    private String registrationNumber;
    private int capacity;
    private String driverId;
    private String driverName;
    private String driverPhone;
    private String routeId;
    private GPSCoordinate currentLocation;
    private BusStatus status;
    private long lastUpdated;

    // No-argument constructor (required for Firebase)
    public Bus() {
        this.busId = "";
        this.busNumber = "";
        this.registrationNumber = "";
        this.capacity = 0;
        this.driverId = "";
        this.driverName = "";
        this.driverPhone = "";
        this.routeId = "";
        this.currentLocation = null;
        this.status = BusStatus.INACTIVE;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Constructor with parameters
    public Bus(String busId, String busNumber, String registrationNumber, int capacity) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.driverId = "";
        this.driverName = "";
        this.driverPhone = "";
        this.routeId = "";
        this.currentLocation = null;
        this.status = BusStatus.INACTIVE;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public GPSCoordinate getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(GPSCoordinate currentLocation) { this.currentLocation = currentLocation; }

    public BusStatus getStatus() { return status; }
    public void setStatus(BusStatus status) { this.status = status; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}