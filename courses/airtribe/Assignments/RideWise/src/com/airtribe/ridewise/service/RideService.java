package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.RideStatus;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RideService {

    private Map<String, Ride> rides = new LinkedHashMap<>();

    private RiderService riderService;
    private DriverService driverService;

    // strategies are injected, RideService only knows the interfaces
    private RideMatchingStrategy matchingStrategy;
    private FareStrategy fareStrategy;

    public RideService(RiderService riderService, DriverService driverService,
                       RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        this.riderService = riderService;
        this.driverService = driverService;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    public Ride requestRide(String riderId, double distance) throws NoDriverAvailableException {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }

        Rider rider = riderService.getRiderById(riderId);
        if (hasActiveRide(rider)) {
            throw new IllegalStateException(rider.getName() + " already has an active ride");
        }

        Ride ride = new Ride(IdGenerator.nextRideId(), rider, distance);
        rides.put(ride.getId(), ride);

        assignDriver(ride);
        return ride;
    }

    public void assignDriver(Ride ride) throws NoDriverAvailableException {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        Driver driver = matchingStrategy.findDriver(ride.getRider(), availableDrivers);

        if (driver == null) {
            ride.cancel();
            throw new NoDriverAvailableException("No driver available right now. Ride "
                    + ride.getId() + " is cancelled.");
        }

        ride.assignDriver(driver);
        driverService.updateAvailability(driver.getId(), false);
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Only an ASSIGNED ride can be completed. Current status: "
                    + ride.getStatus());
        }

        double fare = fareStrategy.calculateFare(ride);
        ride.complete(fare);

        // driver is free again for the next ride
        Driver driver = ride.getDriver();
        driver.incrementCompletedRides();
        driverService.updateAvailability(driver.getId(), true);

        return ride.getFareReceipt();
    }

    public Ride getRideById(String rideId) {
        Ride ride = rides.get(rideId.trim().toUpperCase());
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found with id: " + rideId);
        }
        return ride;
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rides.values());
    }

    private boolean hasActiveRide(Rider rider) {
        for (Ride ride : rides.values()) {
            if (ride.getRider() == rider && ride.getStatus() == RideStatus.ASSIGNED) {
                return true;
            }
        }
        return false;
    }
}
