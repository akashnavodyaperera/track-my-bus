package com.wycherley.trackmybus.utils;

public class Constants {
    // Firebase Database References
    public static final String USERS_REF = "users";
    public static final String BUSES_REF = "buses";
    public static final String ROUTES_REF = "routes";
    public static final String STUDENTS_REF = "students";
    public static final String NOTIFICATIONS_REF = "notifications";
    public static final String GPS_COORDINATES_REF = "gps_coordinates";

    // Geofence Settings
    public static final double DEFAULT_GEOFENCE_RADIUS = 100.0; // meters
    public static final long GEOFENCE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 hours

    // Update Intervals
    public static final long GPS_UPDATE_INTERVAL = 5000L; // 5 seconds
    public static final long MAP_UPDATE_INTERVAL = 3000L; // 3 seconds

    // Notification Channels
    public static final String NOTIFICATION_CHANNEL_ID = "track_my_bus_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Bus Tracking Notifications";

    // Shared Preferences
    public static final String PREFS_NAME = "TrackMyBusPrefs";
    public static final String PREF_USER_ID = "userId";
    public static final String PREF_USER_ROLE = "userRole";
    public static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    // Request Codes
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;

    // Default Values
    public static final float DEFAULT_MAP_ZOOM = 15f;
    public static final int DEFAULT_BUS_ICON_SIZE = 80;

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}