package com.wycherley.trackmybus.models;

import java.util.ArrayList;
import java.util.List;

public class PickupPoint {
    private String pointId;
    private String name;
    private GPSCoordinate location;
    private double geofenceRadius;   // in meters
    private String scheduledTime;
    private List<String> studentIds;
    private int order;               // sequence in route

    // No-argument constructor (required for Firebase)
    public PickupPoint() {
        this.pointId = "";
        this.name = "";
        this.location = new GPSCoordinate();
        this.geofenceRadius = 100.0;
        this.scheduledTime = "";
        this.studentIds = new ArrayList<>();
        this.order = 0;
    }

    // Constructor with parameters
    public PickupPoint(String pointId, String name, GPSCoordinate location) {
        this.pointId = pointId;
        this.name = name;
        this.location = location;
        this.geofenceRadius = 100.0;
        this.scheduledTime = "";
        this.studentIds = new ArrayList<>();
        this.order = 0;
    }

    // Getters and Setters
    public String getPointId() { return pointId; }
    public void setPointId(String pointId) { this.pointId = pointId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public GPSCoordinate getLocation() { return location; }
    public void setLocation(GPSCoordinate location) { this.location = location; }

    public double getGeofenceRadius() { return geofenceRadius; }
    public void setGeofenceRadius(double geofenceRadius) { this.geofenceRadius = geofenceRadius; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public List<String> getStudentIds() { return studentIds; }
    public void setStudentIds(List<String> studentIds) { this.studentIds = studentIds; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}