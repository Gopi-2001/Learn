package com.parkinglot.dto;

import com.parkinglot.entity.SpotSize;

import java.util.Map;

public class AvailabilityResponse {

    private final long totalSpots;
    private final long freeSpots;
    // floor number -> (spot size -> free count)
    private final Map<Integer, Map<SpotSize, Long>> freeSpotsByFloor;

    public AvailabilityResponse(long totalSpots, long freeSpots, Map<Integer, Map<SpotSize, Long>> freeSpotsByFloor) {
        this.totalSpots = totalSpots;
        this.freeSpots = freeSpots;
        this.freeSpotsByFloor = freeSpotsByFloor;
    }

    public long getTotalSpots() {
        return totalSpots;
    }

    public long getFreeSpots() {
        return freeSpots;
    }

    public Map<Integer, Map<SpotSize, Long>> getFreeSpotsByFloor() {
        return freeSpotsByFloor;
    }
}
