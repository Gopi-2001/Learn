# Smart Parking Lot – Spring Boot LLD

Backend for a multi-floor parking lot. It assigns a spot based on vehicle size, records entry/exit time, calculates the fee on exit and shows live spot availability.

## Tech Stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Bean Validation
- H2 in-memory database
- JUnit 5
- Gradle 8.10 via the Gradle Wrapper (no Gradle install needed)

## Run

Only **JDK 17 or newer** is needed. The wrapper (`gradlew`) downloads the right Gradle version on the first run.

```bash
# Windows
gradlew.bat bootRun      # starts on http://localhost:8080
gradlew.bat test         # runs unit + integration tests
gradlew.bat build        # builds build/libs/smart-parking-lot-1.0.0.jar

# Mac / Linux
./gradlew bootRun
./gradlew test
./gradlew build
```

Run the built jar: `java -jar build/libs/smart-parking-lot-1.0.0.jar`

On startup `DataLoader` creates a sample lot: **3 floors**, each with 5 SMALL, 10 MEDIUM and 2 LARGE spots.
H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:parkingdb`, user `sa`).

## Project Structure

```
build.gradle, settings.gradle     build setup
gradlew, gradlew.bat, gradle/     Gradle Wrapper
docs/                             design documents (see below)

com.parkinglot
├── controller   REST endpoints (ParkingController, SpotController)
├── service      business flow (ParkingService, SpotService, VehicleService)
├── strategy
│   ├── allocation   SpotAllocationStrategy + BestFitSpotAllocationStrategy
│   └── fee          FeeStrategy + HourlyFeeStrategy
├── repository   Spring Data JPA repositories
├── entity       Vehicle, ParkingSpot, ParkingTicket + enums
├── dto          request / response objects
├── exception    custom exceptions + GlobalExceptionHandler
└── config       Clock bean, sample data loader
```

## Design Docs

| Doc | Contents |
|---|---|
| [Requirements](docs/requirements.md) | Functional / non-functional requirements, business rules, assumptions |
| [Class Model](docs/class-model.md) | Full class diagram and responsibility of each class |
| [Object Relationships](docs/object-relationships.md) | ER diagram, relationship types, state and sequence diagrams |
| [SOLID Reflection](docs/solid-reflection.md) | How each SOLID principle is applied and what could be better |

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/parking/check-in` | Vehicle enters, gets a spot and a ticket |
| POST | `/api/parking/check-out` | Vehicle leaves, fee is calculated, spot is freed |
| GET | `/api/spots/availability` | Free spots per floor and size (real time) |
| GET | `/api/spots` | All spots with status |
| POST | `/api/spots` | Add a new spot |

**Check-in**
```bash
curl -X POST localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate":"KA01AB1234","vehicleType":"CAR"}'
```
```json
{"ticketId":1,"licensePlate":"KA01AB1234","vehicleType":"CAR","spotCode":"F1-S6",
 "floorNumber":1,"entryTime":"2026-10-24T10:00:00","exitTime":null,"fee":null,"status":"ACTIVE"}
```

**Check-out**
```bash
curl -X POST localhost:8080/api/parking/check-out \
  -H "Content-Type: application/json" \
  -d '{"licensePlate":"KA01AB1234"}'
```
Returns the same ticket with `exitTime`, `fee` and `status: COMPLETED`.

**Add spot**
```bash
curl -X POST localhost:8080/api/spots \
  -H "Content-Type: application/json" \
  -d '{"floorNumber":4,"spotNumber":1,"size":"LARGE"}'
```

Errors come back as `{"status":409,"message":"No free spot available for BUS","timestamp":"..."}`
(404 = no active ticket, 400 = bad input / already parked, 409 = lot full or concurrent clash).

## Data Model

```
vehicles          (id, license_plate UNIQUE, vehicle_type)
parking_spots     (id, floor_number, spot_number, size, available, version)  UNIQUE(floor_number, spot_number)
parking_tickets   (id, vehicle_id FK, spot_id FK, entry_time, exit_time, fee, status, version)
```

A vehicle can have many tickets over time, but only one `ACTIVE` ticket. A spot can have many tickets over time, but only one active at a time.

## Business Rules

**Which vehicle fits where**

| Vehicle | Allowed spots (best fit first) |
|---|---|
| MOTORCYCLE | SMALL → MEDIUM → LARGE |
| CAR | MEDIUM → LARGE |
| BUS | LARGE |

**Spot allocation (best fit, nearest)** – among free spots the vehicle fits in, pick the smallest size first, then the lowest floor, then the lowest spot number. This keeps bigger spots free for bigger vehicles.

**Fee** – `hourly rate × hours`, every started hour is charged, minimum 1 hour.

| Vehicle | Rate / hour |
|---|---|
| MOTORCYCLE | 10 |
| CAR | 20 |
| BUS | 50 |

## Concurrency

- Check-in loads candidate free spots with `SELECT ... FOR UPDATE` (pessimistic lock), so two vehicles arriving at the same time can never get the same spot. The second request waits, then sees the spot as taken.
- `@Version` on `ParkingSpot` and `ParkingTicket` adds optimistic locking as a safety net (for example, the same car checked out twice at once).
- Lock/constraint clashes are returned as HTTP 409 so the client can retry.

## Tests

- `HourlyFeeStrategyTest` – fee rounding and rates
- `BestFitSpotAllocationStrategyTest` – allocation order and size rules
- `ParkingServiceTest` – full check-in / check-out flow
- `ConcurrentCheckInTest` – 10 buses arrive together for 6 large spots, no spot is given twice

## Extending

- New pricing (weekend, flat rate): add a class implementing `FeeStrategy` and mark it `@Primary`.
- New allocation rule (nearest to lift, EV spots): add a class implementing `SpotAllocationStrategy`.
- No change needed in `ParkingService` or `SpotService`.
