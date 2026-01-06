package com.wycherley.trackmybus.models;

public class GPSCoordinate {
    private double latitude;
    private double longitude;
    private long timestamp;
    private double speed;      // km/h
    private double heading;    // degrees (0-360)
    private float accuracy;    // in meters

    // No-argument constructor (required for Firebase)
    public GPSCoordinate() {
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.timestamp = System.currentTimeMillis();
        this.speed = 0.0;
        this.heading = 0.0;
        this.accuracy = 0.0f;
    }

    // Constructor with parameters
    public GPSCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
        this.speed = 0.0;
        this.heading = 0.0;
        this.accuracy = 0.0f;
    }

    // Full constructor
    public GPSCoordinate(double latitude, double longitude, long timestamp,
                         double speed, double heading, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.speed = speed;
        this.heading = heading;
        this.accuracy = accuracy;
    }

    // Calculate distance to another coordinate (Haversine formula)
    public double distanceTo(GPSCoordinate other) {
        double earthRadius = 6371.0; // kilometers

        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.latitude)) *
                        Math.cos(Math.toRadians(other.latitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getHeading() { return heading; }
    public void setHeading(double heading) { this.heading = heading; }

    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }
}