package com.wycherley.trackmybus.models;

public enum BusStatus {
    ACTIVE,      // Currently on route
    INACTIVE,    // Parked/Not in service
    MAINTENANCE, // Under maintenance
    DELAYED      // Running late
}