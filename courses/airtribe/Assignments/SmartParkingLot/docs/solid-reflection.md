# SOLID Reflection

How each principle shows up in the code, why it was done that way, and what I would improve.

The assignment guide uses ride-sharing names. In this project:
`RideService` → `ParkingService`, `DriverService` → `SpotService`, `FareCalculator / FareStrategy` → `FeeStrategy`, `RideMatchingStrategy` → `SpotAllocationStrategy`.

## S – Single Responsibility

**Applied**
- `ParkingService` – only the check-in / check-out flow.
- `SpotService` – only spots (allocate, release, add, availability).
- `VehicleService` – only finding or registering vehicles.
- `HourlyFeeStrategy` – only the price.
- `BestFitSpotAllocationStrategy` – only choosing a spot.
- Controllers only map HTTP; `GlobalExceptionHandler` only maps errors.

**Reflection** – Each class has one reason to change. If pricing changes, only the fee strategy changes. A trade-off is more small classes, but each is easy to read and test.

## O – Open / Closed

**Applied** – New rules are added as new classes:
```java
@Component
@Primary
public class WeekendFeeStrategy implements FeeStrategy { ... }
```
`ParkingService` does not change.

**Reflection** – The parts most likely to change (pricing, allocation) are behind interfaces. The vehicle → spot size mapping lives in the `VehicleType` enum, so adding a new vehicle type (e.g. TRUCK) still means editing the enum. That is fine for an MVP; it could move to config or a table later.

## L – Liskov Substitution

**Applied** – Any `FeeStrategy` must return a non-negative fee for any type and duration. Any `SpotAllocationStrategy` must return only a free spot that fits, or `Optional.empty()`. Services rely only on this contract, so any implementation can be swapped in.

**Reflection** – The contract is written in comments and checked by tests, not by the compiler. A shared test for all strategy implementations would make this stronger.

## I – Interface Segregation

**Applied** – Both strategy interfaces have exactly one method:
```java
Optional<ParkingSpot> selectSpot(List<ParkingSpot> freeSpots, VehicleType vehicleType);
BigDecimal calculateFee(VehicleType vehicleType, Duration parkedDuration);
```
No implementation is forced to write methods it does not need.

**Reflection** – I avoided one big `ParkingRules` interface with both methods. Keeping them separate means pricing and allocation can change independently.

## D – Dependency Inversion

**Applied**
- `ParkingService` depends on `FeeStrategy`, not `HourlyFeeStrategy`.
- `SpotService` depends on `SpotAllocationStrategy`, not the best-fit class.
- `Clock` is injected instead of calling `LocalDateTime.now()` directly.
- All dependencies come through the constructor (no field `@Autowired`), so classes can be created in plain unit tests.

**Reflection** – Services still depend on the concrete `SpotService` and `VehicleService` classes. That is OK because they are internal and there is only one implementation; adding interfaces for them now would be extra code with no benefit (YAGNI).

---

## Other Design Principles

| Principle | Where |
|---|---|
| **DRY** | Size rules only in `VehicleType`; plate cleanup only in `Vehicle.normalizePlate`; errors only in `GlobalExceptionHandler`; DTO mapping only in `from()` methods |
| **KISS** | Three entities, floor is a column not a table, fee is a simple rate map |
| **YAGNI** | No payments, reservations, auth, EV spots, multiple lots |
| **Law of Demeter** | `ParkingTicket.getVehicleType()` / `getSpotCode()` instead of `ticket.getVehicle().getVehicleType()` chains |

## What I Would Improve Next

1. Lock only one spot per check-in (`FOR UPDATE SKIP LOCKED` on PostgreSQL) instead of all candidate spots, for higher throughput.
2. Add a DB unique index for "one ACTIVE ticket per vehicle" to fully block a double check-in race.
3. Move hourly rates to `application.properties`.
4. Use a `COUNT ... GROUP BY` query for availability instead of loading all spots.
