package com.wycherley.trackmybus.models;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String routeId;
    private String routeName;
    private String routeNumber;
    private String schoolId;
    private List<PickupPoint> pickupPoints;
    private List<GPSCoordinate> waypoints;
    private double totalDistance;    // in kilometers
    private int estimatedDuration;   // in minutes
    private String scheduleTime;     // e.g., "07:30 AM"
    private boolean isActive;

    // No-argument constructor (required for Firebase)
    public Route() {
        this.routeId = "";
        this.routeName = "";
        this.routeNumber = "";
        this.schoolId = "";
        this.pickupPoints = new ArrayList<>();
        this.waypoints = new ArrayList<>();
        this.totalDistance = 0.0;
        this.estimatedDuration = 0;
        this.scheduleTime = "";
        this.isActive = true;
    }

    // Constructor with parameters
    public Route(String routeId, String routeName, String routeNumber) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routeNumber = routeNumber;
        this.schoolId = "";
        this.pickupPoints = new ArrayList<>();
        this.waypoints = new ArrayList<>();
        this.totalDistance = 0.0;
        this.estimatedDuration = 0;
        this.scheduleTime = "";
        this.isActive = true;
    }

    // Getters and Setters
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public List<PickupPoint> getPickupPoints() { return pickupPoints; }
    public void setPickupPoints(List<PickupPoint> pickupPoints) { this.pickupPoints = pickupPoints; }

    public List<GPSCoordinate> getWaypoints() { return waypoints; }
    public void setWaypoints(List<GPSCoordinate> waypoints) { this.waypoints = waypoints; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public int getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public String getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(String scheduleTime) { this.scheduleTime = scheduleTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}