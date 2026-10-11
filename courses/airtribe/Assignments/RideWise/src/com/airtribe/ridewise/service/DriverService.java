package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DriverService {

    private Map<String, Driver> drivers = new LinkedHashMap<>();

    public Driver registerDriver(String name, int location, VehicleType vehicleType) {
        Driver driver = new Driver(IdGenerator.nextDriverId(), name, location, vehicleType);
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Driver getDriverById(String id) {
        Driver driver = drivers.get(id.trim().toUpperCase());
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with id: " + id);
        }
        return driver;
    }

    public void updateAvailability(String driverId, boolean available) {
        Driver driver = getDriverById(driverId);
        driver.setAvailable(available);
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable()) {
                availableDrivers.add(driver);
            }
        }
        return availableDrivers;
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(drivers.values());
    }
}
