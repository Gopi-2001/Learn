package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;

public interface RideMatchingStrategy {

    // returns the best driver from the list, or null if list is empty
    Driver findDriver(Rider rider, List<Driver> drivers);
}
