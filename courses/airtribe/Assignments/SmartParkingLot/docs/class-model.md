# Class Model

## Layers

```
controller  →  service  →  strategy (interfaces)
                  ↓
              repository  →  database
entity / dto are shared data classes
```

## Full Class Diagram

```mermaid
classDiagram
    direction LR

    %% ---------- controllers ----------
    class ParkingController {
        -ParkingService parkingService
        +checkIn(CheckInRequest) TicketResponse
        +checkOut(CheckOutRequest) TicketResponse
    }
    class SpotController {
        -SpotService spotService
        +getAllSpots() List~SpotResponse~
        +getAvailability() AvailabilityResponse
        +addSpot(AddSpotRequest) SpotResponse
    }

    %% ---------- services ----------
    class ParkingService {
        -ParkingTicketRepository ticketRepository
        -VehicleService vehicleService
        -SpotService spotService
        -FeeStrategy feeStrategy
        -Clock clock
        +checkIn(CheckInRequest) TicketResponse
        +checkOut(CheckOutRequest) TicketResponse
    }
    class SpotService {
        -ParkingSpotRepository spotRepository
        -SpotAllocationStrategy allocationStrategy
        +allocateSpot(VehicleType) ParkingSpot
        +releaseSpot(ParkingSpot) void
        +addSpot(AddSpotRequest) SpotResponse
        +getAllSpots() List~SpotResponse~
        +getAvailability() AvailabilityResponse
    }
    class VehicleService {
        -VehicleRepository vehicleRepository
        +findOrRegister(String, VehicleType) Vehicle
    }

    %% ---------- strategies ----------
    class SpotAllocationStrategy {
        <<interface>>
        +selectSpot(List~ParkingSpot~, VehicleType) Optional~ParkingSpot~
    }
    class BestFitSpotAllocationStrategy {
        +selectSpot(List~ParkingSpot~, VehicleType) Optional~ParkingSpot~
    }
    class FeeStrategy {
        <<interface>>
        +calculateFee(VehicleType, Duration) BigDecimal
    }
    class HourlyFeeStrategy {
        -Map HOURLY_RATES
        +calculateFee(VehicleType, Duration) BigDecimal
    }

    %% ---------- repositories ----------
    class ParkingSpotRepository {
        <<interface>>
        +findFreeSpotsForUpdate(List~SpotSize~) List~ParkingSpot~
        +existsByFloorNumberAndSpotNumber(int, int) boolean
    }
    class ParkingTicketRepository {
        <<interface>>
        +existsByVehicleLicensePlateAndStatus(String, TicketStatus) boolean
        +findWithDetails(String, TicketStatus) Optional~ParkingTicket~
    }
    class VehicleRepository {
        <<interface>>
        +findByLicensePlate(String) Optional~Vehicle~
    }

    %% ---------- entities ----------
    class Vehicle {
        -Long id
        -String licensePlate
        -VehicleType vehicleType
        +normalizePlate(String)$ String
    }
    class ParkingSpot {
        -Long id
        -int floorNumber
        -int spotNumber
        -SpotSize size
        -boolean available
        -Long version
        +occupy() void
        +release() void
        +getSpotCode() String
    }
    class ParkingTicket {
        -Long id
        -Vehicle vehicle
        -ParkingSpot spot
        -LocalDateTime entryTime
        -LocalDateTime exitTime
        -BigDecimal fee
        -TicketStatus status
        -Long version
        +close(LocalDateTime, BigDecimal) void
        +getVehicleType() VehicleType
        +getLicensePlate() String
        +getSpotCode() String
    }
    class VehicleType {
        <<enumeration>>
        MOTORCYCLE
        CAR
        BUS
        +getAllowedSpotSizes() List~SpotSize~
        +canFitIn(SpotSize) boolean
    }
    class SpotSize {
        <<enumeration>>
        SMALL
        MEDIUM
        LARGE
    }
    class TicketStatus {
        <<enumeration>>
        ACTIVE
        COMPLETED
    }

    ParkingController --> ParkingService
    SpotController --> SpotService
    ParkingService --> ParkingTicketRepository
    ParkingService --> VehicleService
    ParkingService --> SpotService
    ParkingService --> FeeStrategy
    SpotService --> ParkingSpotRepository
    SpotService --> SpotAllocationStrategy
    VehicleService --> VehicleRepository
    SpotAllocationStrategy <|.. BestFitSpotAllocationStrategy
    FeeStrategy <|.. HourlyFeeStrategy
    ParkingTicket "*" --> "1" Vehicle
    ParkingTicket "*" --> "1" ParkingSpot
    Vehicle --> VehicleType
    ParkingSpot --> SpotSize
    ParkingTicket --> TicketStatus
```

## Class Responsibilities

| Class | Layer | Responsibility |
|---|---|---|
| `ParkingController` | controller | HTTP endpoints for check-in / check-out |
| `SpotController` | controller | HTTP endpoints for spots and availability |
| `ParkingService` | service | Check-in / check-out flow and transaction |
| `SpotService` | service | Allocate, release, add spots; build availability |
| `VehicleService` | service | Find or register a vehicle |
| `SpotAllocationStrategy` | strategy | Contract: pick one spot from free spots |
| `BestFitSpotAllocationStrategy` | strategy | Smallest fit → lowest floor → lowest number |
| `FeeStrategy` | strategy | Contract: fee for a type and duration |
| `HourlyFeeStrategy` | strategy | Hourly rate per type, started hour charged, min 1 hour |
| `*Repository` | repository | DB access through Spring Data JPA |
| `Vehicle` | entity | A vehicle identified by plate |
| `ParkingSpot` | entity | One spot on one floor; knows if it is free |
| `ParkingTicket` | entity | One visit: vehicle + spot + times + fee |
| `VehicleType` | entity (enum) | Which spot sizes a vehicle fits in |
| `GlobalExceptionHandler` | exception | Converts exceptions into JSON errors |
| `ClockConfig` | config | Provides `Clock` bean (testable time) |
| `DataLoader` | config | Seeds 3 floors of sample spots |

## DTOs

| DTO | Used for |
|---|---|
| `CheckInRequest` | `licensePlate`, `vehicleType` |
| `CheckOutRequest` | `licensePlate` |
| `AddSpotRequest` | `floorNumber`, `spotNumber`, `size` |
| `TicketResponse` | ticket details returned on check-in / check-out |
| `SpotResponse` | one spot with status |
| `AvailabilityResponse` | total, free, free per floor per size |
| `ErrorResponse` | `status`, `message`, `timestamp` |

## Exceptions

| Exception | HTTP |
|---|---|
| `ResourceNotFoundException` | 404 |
| `InvalidRequestException` | 400 |
| `ParkingFullException` | 409 |
| Lock / constraint clash (Spring) | 409 |
