# Object Relationships

| Relationship | Type | Explanation |
|--------------|------|-------------|
| Rider -> Ride | Association | A ride refers to a rider, but the rider exists on its own (even without rides). |
| Driver -> Ride | Association | A ride refers to a driver. The driver exists before and after the ride. |
| Ride -> FareReceipt | Composition | The receipt is created inside `Ride.complete()` and belongs only to that ride. No ride, no receipt. |
| RideService -> Strategies | Composition | `RideService` holds a `RideMatchingStrategy` and a `FareStrategy` and uses them to do its work. |
| RideService -> RiderService / DriverService | Dependency (uses) | `RideService` asks these services for riders and available drivers. |
| Driver -> VehicleType | Association | Each driver has one vehicle type. |
| PeakHourFareStrategy -> DefaultFareStrategy | Composition | Peak fare is built on top of the default fare. |

## Ride Life Cycle

1. `Main` calls `rideService.requestRide(riderId, distance)`
2. `RideService` gets the rider from `RiderService` and creates a `Ride` (status `REQUESTED`)
3. `RideService` gets available drivers from `DriverService`
4. `RideMatchingStrategy` picks a driver -> ride becomes `ASSIGNED`, driver becomes unavailable
   - If no driver is found -> ride becomes `CANCELLED` and `NoDriverAvailableException` is thrown
5. `Main` calls `rideService.completeRide(rideId)`
6. `FareStrategy` calculates the fare -> `Ride` creates a `FareReceipt` -> status `COMPLETED`
7. Driver becomes available again and their completed ride count goes up by 1
