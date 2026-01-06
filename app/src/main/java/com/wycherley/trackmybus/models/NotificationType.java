package com.wycherley.trackmybus.models;

public enum NotificationType {
    PICKUP_APPROACHING,   // Bus is near pickup point
    DROPOFF_APPROACHING,  // Bus is near dropoff point
    BUS_DELAYED,         // Bus is running late
    ROUTE_CHANGED,       // Route has been modified
    EMERGENCY,           // Emergency alert
    INFO                 // General information
}