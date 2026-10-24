# RideWise - Modular Ride-Sharing System

RideWise is a simple console based ride-sharing system (like Uber / Ola) written in core Java.
The goal of this project is not to build a full app, but to show clean Low Level Design using
OOP, SOLID principles and the Strategy pattern.

## Features

- Register riders and drivers
- View available drivers
- Request a ride (driver is matched using a strategy)
- Complete a ride (fare is calculated using a strategy and a receipt is generated)
- Track ride status: `REQUESTED -> ASSIGNED -> COMPLETED` or `CANCELLED` (when no driver is free)

## Strategies

| Type | Implementation | How it works |
|------|----------------|--------------|
| Matching | `NearestDriverStrategy` | Picks the driver closest to the rider |
| Matching | `LeastActiveDriverStrategy` | Picks the driver with the fewest completed rides |
| Fare | `DefaultFareStrategy` | `30 + distance x rate per km` (BIKE 6, AUTO 9, CAR 14) |
| Fare | `PeakHourFareStrategy` | Default fare x 1.5 |

Both strategies are selected when the app starts and are injected into `RideService` through its constructor.

## Project Structure

```
src/com/airtribe/ridewise/
├── Main.java                  -> console menu (talks only to services)
├── model/                     -> Rider, Driver, Ride, FareReceipt, RideStatus, VehicleType
├── strategy/                  -> RideMatchingStrategy, FareStrategy and their implementations
├── service/                   -> RiderService, DriverService, RideService
├── exception/                 -> NoDriverAvailableException
└── util/                      -> IdGenerator
docs/
├── Requirements.md
├── Class_Model.md
├── SOLID_Reflection.md
└── Object_Relationships.md
```

## How to Run

Requires Java 8 or above.

**Windows (cmd)**

```
cd RideWise
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out com.airtribe.ridewise.Main
```

**Mac / Linux**

```
cd RideWise
javac -d out $(find src -name "*.java")
java -cp out com.airtribe.ridewise.Main
```

You can also open the folder in IntelliJ / Eclipse, mark `src` as the source root and run `Main.java`.

## Sample Run

```
Select driver matching strategy:
1. Nearest Driver
2. Least Active Driver
Enter option: 1

Select fare strategy:
1. Default Fare
2. Peak Hour Fare (1.5x)
Enter option: 1

------ MENU ------
1. Add Rider
...
Enter your choice: 4
Enter rider id (e.g. R1): R1
Enter trip distance in km: 10
Ride booked successfully -> Ride[id=RIDE1, rider=Amit, driver=Ravi (CAR), distance=10.0 km, status=ASSIGNED]

Enter your choice: 5
Enter ride id (e.g. RIDE1): RIDE1
Ride completed. FareReceipt[rideId=RIDE1, amount=Rs. 170.00, generatedAt=11-10-2026 16:26]
```

## Assumptions

- Location is kept simple as a single number (a km point on the city map). Distance between
  rider and driver = `|driverLocation - riderLocation|`.
- Data is stored in memory (no database), so it is lost when the app is closed.
- A rider can have only one active (ASSIGNED) ride at a time.
- If no driver is free, the ride is saved with status `CANCELLED`.

## Design Docs

More details are in the `docs/` folder:
[Requirements](docs/Requirements.md) |
[Class Model](docs/Class_Model.md) |
[SOLID Reflection](docs/SOLID_Reflection.md) |
[Object Relationships](docs/Object_Relationships.md)
