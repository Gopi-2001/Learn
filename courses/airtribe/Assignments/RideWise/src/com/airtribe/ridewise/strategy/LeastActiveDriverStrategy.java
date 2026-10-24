package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;

// picks the driver who has completed the least number of rides
// so that work is shared fairly between drivers
public class LeastActiveDriverStrategy implements RideMatchingStrategy {

    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        Driver leastActive = null;

        for (Driver driver : drivers) {
            if (leastActive == null || driver.getCompletedRides() < leastActive.getCompletedRides()) {
                leastActive = driver;
            }
        }
        return leastActive;
    }
}
