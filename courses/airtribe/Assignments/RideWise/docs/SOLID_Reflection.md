# SOLID Reflection

## S - Single Responsibility Principle
Each class does one job.
- `RiderService` only manages riders, `DriverService` only manages drivers.
- `RideService` only handles the ride flow (request, assign, complete).
- Fare logic lives in `FareStrategy` classes, matching logic lives in `RideMatchingStrategy` classes.
- `Main` only handles input/output.

## O - Open/Closed Principle
To add a new pricing rule (for example `NightFareStrategy`) we just create a new class that
implements `FareStrategy`. `RideService` does not change at all. Same for matching
(for example `HighestRatedDriverStrategy`).

## L - Liskov Substitution Principle
Any `FareStrategy` or `RideMatchingStrategy` can replace another without breaking the app.
All matching strategies return a `Driver` (or `null` when the list is empty), and all fare
strategies return a positive fare amount. `RideService` works the same with all of them.

## I - Interface Segregation Principle
The interfaces are small and focused. `RideMatchingStrategy` has only `findDriver()` and
`FareStrategy` has only `calculateFare()`. A class is never forced to implement a method it does not need.

## D - Dependency Inversion Principle
`RideService` depends on the interfaces `RideMatchingStrategy` and `FareStrategy`, not on concrete
classes. The concrete strategy is chosen in `Main` and passed through the constructor.

## Other Principles

- **DRY** - `PeakHourFareStrategy` reuses `DefaultFareStrategy` instead of copying the formula.
  Driver allocation logic is written once in `RideService.assignDriver()`.
- **KISS** - Location is a single number, data is kept in simple maps, no frameworks.
- **YAGNI** - Only the MVP features from the brief are built. No login, payment or database.
- **Law of Demeter** - Services talk only to their direct collaborators. For example the fare
  strategy calls `ride.getVehicleType()` instead of `ride.getDriver().getVehicleType()`, and
  `Main` never reaches inside models through long chains.
- **Composition over Inheritance** - `RideService` *has* strategies instead of extending
  different ride service classes. `PeakHourFareStrategy` *has* a `DefaultFareStrategy`.
