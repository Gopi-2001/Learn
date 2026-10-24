package com.parkinglot.dto;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;

public class SpotResponse {

    private Long id;
    private String spotCode;
    private int floorNumber;
    private int spotNumber;
    private SpotSize size;
    private boolean available;

    public static SpotResponse from(ParkingSpot spot) {
        SpotResponse response = new SpotResponse();
        response.id = spot.getId();
        response.spotCode = spot.getSpotCode();
        response.floorNumber = spot.getFloorNumber();
        response.spotNumber = spot.getSpotNumber();
        response.size = spot.getSize();
        response.available = spot.isAvailable();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getSpotCode() {
        return spotCode;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public SpotSize getSize() {
        return size;
    }

    public boolean isAvailable() {
        return available;
    }
}
