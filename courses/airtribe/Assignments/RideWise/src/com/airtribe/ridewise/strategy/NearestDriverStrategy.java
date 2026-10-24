package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;

// picks the driver who is closest to the rider
public class NearestDriverStrategy implements RideMatchingStrategy {

    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        Driver nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Driver driver : drivers) {
            int distance = Math.abs(driver.getCurrentLocation() - rider.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = driver;
            }
        }
        return nearest;
    }
}
