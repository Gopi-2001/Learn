# Class Model

## model/

| Class | Fields | Notes |
|-------|--------|-------|
| `Rider` | id, name, location | |
| `Driver` | id, name, currentLocation, available, vehicleType, completedRides | `completedRides` is used by LeastActiveDriverStrategy |
| `Ride` | id, rider, driver, distance, status, fareReceipt | Starts as REQUESTED. Methods: `assignDriver()`, `complete()`, `cancel()` |
| `FareReceipt` | rideId, amount, generatedAt | Created inside `Ride.complete()` |
| `RideStatus` (enum) | REQUESTED, ASSIGNED, COMPLETED, CANCELLED | |
| `VehicleType` (enum) | BIKE, AUTO, CAR | Each has its own `ratePerKm` |

## strategy/

```
<<interface>> RideMatchingStrategy
    + findDriver(Rider, List<Driver>) : Driver
        ^-- NearestDriverStrategy
        ^-- LeastActiveDriverStrategy

<<interface>> FareStrategy
    + calculateFare(Ride) : double
        ^-- DefaultFareStrategy
        ^-- PeakHourFareStrategy  (uses DefaultFareStrategy inside)
```

## service/

| Class | Responsibility |
|-------|----------------|
| `RiderService` | registerRider, getRiderById, getAllRiders |
| `DriverService` | registerDriver, getDriverById, updateAvailability, getAvailableDrivers |
| `RideService` | requestRide, assignDriver, completeRide, getRideById, getAllRides |

`RideService` constructor:

```java
public RideService(RiderService riderService, DriverService driverService,
                   RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy)
```

## Others

- `exception/NoDriverAvailableException` - thrown when no driver can be assigned
- `util/IdGenerator` - generates ids like R1, D1, RIDE1
- `Main` - console menu, only calls services

## Class Diagram (simple)

```
Main ──> RiderService
     ──> DriverService
     ──> RideService ──> RiderService
                     ──> DriverService
                     ──> RideMatchingStrategy (interface)
                     ──> FareStrategy (interface)

Ride ──> Rider
     ──> Driver
     ◆── FareReceipt
```
