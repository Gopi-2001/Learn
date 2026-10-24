package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

// peak hour fare = normal fare * surge multiplier
// reuses DefaultFareStrategy instead of copying the same formula (DRY)
public class PeakHourFareStrategy implements FareStrategy {

    private static final double SURGE_MULTIPLIER = 1.5;

    private FareStrategy baseStrategy = new DefaultFareStrategy();

    @Override
    public double calculateFare(Ride ride) {
        double fare = baseStrategy.calculateFare(ride) * SURGE_MULTIPLIER;
        return Math.round(fare * 100) / 100.0;
    }
}
