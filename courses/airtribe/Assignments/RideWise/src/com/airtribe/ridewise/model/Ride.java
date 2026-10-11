package com.airtribe.ridewise.model;

public class Ride {

    private String id;
    private Rider rider;
    private Driver driver;
    private double distance;
    private RideStatus status;
    private FareReceipt fareReceipt;   // created by the ride itself (composition)

    public Ride(String id, Rider rider, double distance) {
        this.id = id;
        this.rider = rider;
        this.distance = distance;
        this.status = RideStatus.REQUESTED;
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
        this.status = RideStatus.ASSIGNED;
    }

    public void complete(double fare) {
        this.fareReceipt = new FareReceipt(id, fare);
        this.status = RideStatus.COMPLETED;
    }

    public void cancel() {
        this.status = RideStatus.CANCELLED;
    }

    // small helper so strategies don't need ride.getDriver().getVehicleType()
    public VehicleType getVehicleType() {
        if (driver == null) {
            return null;
        }
        return driver.getVehicleType();
    }

    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getDistance() {
        return distance;
    }

    public RideStatus getStatus() {
        return status;
    }

    public FareReceipt getFareReceipt() {
        return fareReceipt;
    }

    @Override
    public String toString() {
        String driverName = (driver == null) ? "Not assigned" : driver.getName() + " (" + driver.getVehicleType() + ")";
        String text = "Ride[id=" + id + ", rider=" + rider.getName() + ", driver=" + driverName
                + ", distance=" + distance + " km, status=" + status;
        if (fareReceipt != null) {
            text += ", fare=Rs. " + String.format("%.2f", fareReceipt.getAmount());
        }
        return text + "]";
    }
}
