package com.wycherley.trackmybus.models;

public class BusDriver {
    private String busNumber;
    private String driverName;
    private String fromLocation;
    private String toLocation;

    public BusDriver() {}

    public BusDriver(String busNumber, String driverName, String fromLocation, String toLocation) {
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
}