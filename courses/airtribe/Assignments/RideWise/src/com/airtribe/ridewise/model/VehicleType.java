package com.airtribe.ridewise.model;

// each vehicle type has its own per km rate
public enum VehicleType {
    BIKE(6),
    AUTO(9),
    CAR(14);

    private final double ratePerKm;

    VehicleType(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }
}
