package com.parkinglot.dto;

import com.parkinglot.entity.SpotSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddSpotRequest {

    @Min(0)
    private int floorNumber;

    @Min(1)
    private int spotNumber;

    @NotNull
    private SpotSize size;

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    public SpotSize getSize() {
        return size;
    }

    public void setSize(SpotSize size) {
        this.size = size;
    }
}
