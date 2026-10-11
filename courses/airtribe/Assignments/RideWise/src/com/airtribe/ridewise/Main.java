package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.LeastActiveDriverStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.strategy.PeakHourFareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);

    private static RiderService riderService;
    private static DriverService driverService;
    private static RideService rideService;

    public static void main(String[] args) {
        System.out.println("===== Welcome to RideWise =====");

        riderService = new RiderService();
        driverService = new DriverService();

        // choose strategies once at start and inject them into RideService
        RideMatchingStrategy matchingStrategy = chooseMatchingStrategy();
        FareStrategy fareStrategy = chooseFareStrategy();
        rideService = new RideService(riderService, driverService, matchingStrategy, fareStrategy);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addRider();
                    break;
                case 2:
                    addDriver();
                    break;
                case 3:
                    viewAvailableDrivers();
                    break;
                case 4:
                    requestRide();
                    break;
                case 5:
                    completeRide();
                    break;
                case 6:
                    viewRides();
                    break;
                case 7:
                    running = false;
                    System.out.println("Thank you for using RideWise. Bye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select between 1 and 7.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("------ MENU ------");
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View Available Drivers");
        System.out.println("4. Request Ride");
        System.out.println("5. Complete Ride");
        System.out.println("6. View Rides");
        System.out.println("7. Exit");
    }

    private static RideMatchingStrategy chooseMatchingStrategy() {
        System.out.println("\nSelect driver matching strategy:");
        System.out.println("1. Nearest Driver");
        System.out.println("2. Least Active Driver");

        while (true) {
            int option = readInt("Enter option: ");
            if (option == 1) {
                return new NearestDriverStrategy();
            } else if (option == 2) {
                return new LeastActiveDriverStrategy();
            }
            System.out.println("Please enter 1 or 2.");
        }
    }

    private static FareStrategy chooseFareStrategy() {
        System.out.println("\nSelect fare strategy:");
        System.out.println("1. Default Fare");
        System.out.println("2. Peak Hour Fare (1.5x)");

        while (true) {
            int option = readInt("Enter option: ");
            if (option == 1) {
                return new DefaultFareStrategy();
            } else if (option == 2) {
                return new PeakHourFareStrategy();
            }
            System.out.println("Please enter 1 or 2.");
        }
    }

    private static void addRider() {
        String name = readText("Enter rider name: ");
        int location = readInt("Enter rider location (km point, e.g. 5): ");

        Rider rider = riderService.registerRider(name, location);
        System.out.println("Rider added successfully -> " + rider);
    }

    private static void addDriver() {
        String name = readText("Enter driver name: ");
        int location = readInt("Enter driver location (km point, e.g. 8): ");

        VehicleType[] types = VehicleType.values();
        System.out.println("Select vehicle type:");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        int option = readInt("Enter option: ");
        if (option < 1 || option > types.length) {
            System.out.println("Invalid vehicle type. Driver not added.");
            return;
        }

        Driver driver = driverService.registerDriver(name, location, types[option - 1]);
        System.out.println("Driver added successfully -> " + driver);
    }

    private static void viewAvailableDrivers() {
        List<Driver> drivers = driverService.getAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available right now.");
            return;
        }
        System.out.println("Available drivers:");
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }

    private static void requestRide() {
        String riderId = readText("Enter rider id (e.g. R1): ");
        double distance = readDouble("Enter trip distance in km: ");

        try {
            Ride ride = rideService.requestRide(riderId, distance);
            System.out.println("Ride booked successfully -> " + ride);
        } catch (NoDriverAvailableException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void completeRide() {
        String rideId = readText("Enter ride id (e.g. RIDE1): ");

        try {
            FareReceipt receipt = rideService.completeRide(rideId);
            System.out.println("Ride completed. " + receipt);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewRides() {
        List<Ride> rides = rideService.getAllRides();
        if (rides.isEmpty()) {
            System.out.println("No rides yet.");
            return;
        }
        for (Ride ride : rides) {
            System.out.println(ride);
        }
    }

    // ---------- input helpers (keep asking until input is valid) ----------

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}
