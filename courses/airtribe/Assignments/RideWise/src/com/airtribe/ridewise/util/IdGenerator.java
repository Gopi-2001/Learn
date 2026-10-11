package com.airtribe.ridewise.util;

// simple counter based ids like R1, D1, RIDE1
public class IdGenerator {

    private static int riderCount = 0;
    private static int driverCount = 0;
    private static int rideCount = 0;

    public static String nextRiderId() {
        riderCount++;
        return "R" + riderCount;
    }

    public static String nextDriverId() {
        driverCount++;
        return "D" + driverCount;
    }

    public static String nextRideId() {
        rideCount++;
        return "RIDE" + rideCount;
    }
}
