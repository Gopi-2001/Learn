package com.airtribe.ridewise.model;

public class Driver {

    private String id;
    private String name;
    private int currentLocation;
    private boolean available;
    private VehicleType vehicleType;
    private int completedRides;   // used by LeastActiveDriverStrategy

    public Driver(String id, String name, int currentLocation, VehicleType vehicleType) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.vehicleType = vehicleType;
        this.available = true;    // new driver is available by default
        this.completedRides = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(int currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void incrementCompletedRides() {
        completedRides++;
    }

    @Override
    public String toString() {
        return "Driver[id=" + id + ", name=" + name + ", location=" + currentLocation
                + ", vehicle=" + vehicleType + ", available=" + available
                + ", completedRides=" + completedRides + "]";
    }
}
