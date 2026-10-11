package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

// fare = base fare + (distance * rate of the vehicle type)
public class DefaultFareStrategy implements FareStrategy {

    private static final double BASE_FARE = 30;

    @Override
    public double calculateFare(Ride ride) {
        double fare = BASE_FARE + ride.getDistance() * ride.getVehicleType().getRatePerKm();
        return Math.round(fare * 100) / 100.0;
    }
}
