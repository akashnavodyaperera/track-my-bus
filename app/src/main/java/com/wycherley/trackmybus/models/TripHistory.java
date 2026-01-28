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

