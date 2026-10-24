package com.parkinglot.entity;

import java.util.List;

public enum VehicleType {

    // spot sizes are listed from best fit to worst fit
    MOTORCYCLE(List.of(SpotSize.SMALL, SpotSize.MEDIUM, SpotSize.LARGE)),
    CAR(List.of(SpotSize.MEDIUM, SpotSize.LARGE)),
    BUS(List.of(SpotSize.LARGE));

    private final List<SpotSize> allowedSpotSizes;

    VehicleType(List<SpotSize> allowedSpotSizes) {
        this.allowedSpotSizes = allowedSpotSizes;
    }

    public List<SpotSize> getAllowedSpotSizes() {
        return allowedSpotSizes;
    }

    public boolean canFitIn(SpotSize size) {
        return allowedSpotSizes.contains(size);
    }
}
