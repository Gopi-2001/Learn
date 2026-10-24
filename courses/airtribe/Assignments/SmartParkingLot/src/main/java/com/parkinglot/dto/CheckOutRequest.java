package com.parkinglot.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckOutRequest {

    @NotBlank
    private String licensePlate;

    public CheckOutRequest() {
    }

    public CheckOutRequest(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
