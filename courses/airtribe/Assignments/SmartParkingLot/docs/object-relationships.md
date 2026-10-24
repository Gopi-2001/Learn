# Object Relationships

## 1. Entity Relationships (ER)

```mermaid
erDiagram
    VEHICLES ||--o{ PARKING_TICKETS : "has"
    PARKING_SPOTS ||--o{ PARKING_TICKETS : "used in"

    VEHICLES {
        bigint id PK
        varchar license_plate UK
        varchar vehicle_type
    }
    PARKING_SPOTS {
        bigint id PK
        int floor_number
        int spot_number
        varchar size
        boolean available
        bigint version
    }
    PARKING_TICKETS {
        bigint id PK
        bigint vehicle_id FK
        bigint spot_id FK
        timestamp entry_time
        timestamp exit_time
        decimal fee
        varchar status
        bigint version
    }
```

| Relationship | Multiplicity | JPA mapping | Meaning |
|---|---|---|---|
| Ticket → Vehicle | many to one | `@ManyToOne(fetch = LAZY)` | A vehicle has many tickets over time, **only one ACTIVE** |
| Ticket → Spot | many to one | `@ManyToOne(fetch = LAZY)` | A spot has many tickets over time, **only one ACTIVE** |
| Spot (floor, number) | unique | `@UniqueConstraint(floor_number, spot_number)` | No two spots with same number on a floor |
| Vehicle plate | unique | `@Column(unique = true)` | One row per vehicle |

The relationships are one-directional (ticket knows vehicle and spot, not the other way). That keeps entities simple and avoids loading big collections.

## 2. Kinds of Relationships Between Classes

| Type | Example | Why |
|---|---|---|
| **Association** | `ParkingTicket` → `Vehicle`, `ParkingSpot` | Ticket links one vehicle to one spot for one visit |
| **Dependency (injected)** | `ParkingService` → `SpotService`, `VehicleService`, `FeeStrategy`, `Clock` | Constructor injection, Spring creates them |
| **Realization** | `HourlyFeeStrategy` implements `FeeStrategy`; `BestFitSpotAllocationStrategy` implements `SpotAllocationStrategy` | Pluggable rules |
| **Inheritance** | Repositories extend `JpaRepository`; exceptions extend `RuntimeException` | Reuse framework behaviour |
| **Uses (value)** | `Vehicle` uses `VehicleType`; `ParkingSpot` uses `SpotSize` | Enum value, no own table |

## 3. Who Talks to Whom

```mermaid
flowchart LR
    PC[ParkingController] --> PS[ParkingService]
    SC[SpotController] --> SS[SpotService]
    PS --> VS[VehicleService]
    PS --> SS
    PS --> FS[[FeeStrategy]]
    PS --> TR[(ParkingTicketRepository)]
    SS --> AS[[SpotAllocationStrategy]]
    SS --> SR[(ParkingSpotRepository)]
    VS --> VR[(VehicleRepository)]
```

- Controllers only call services.
- Only the service that owns an entity uses its repository (`SpotService` → spots, `VehicleService` → vehicles).
- Strategies have no dependencies – they get data as parameters and return a result.

## 4. State Changes

**ParkingSpot**

```mermaid
stateDiagram-v2
    [*] --> Available
    Available --> Occupied : check-in (occupy)
    Occupied --> Available : check-out (release)
```

**ParkingTicket**

```mermaid
stateDiagram-v2
    [*] --> ACTIVE : check-in
    ACTIVE --> COMPLETED : check-out (exit time + fee set)
    COMPLETED --> [*]
```

## 5. Check-in Sequence

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ParkingController
    participant PS as ParkingService
    participant VS as VehicleService
    participant SS as SpotService
    participant AS as SpotAllocationStrategy
    participant DB as Database

    C->>PC: POST /api/parking/check-in
    PC->>PS: checkIn(request)
    PS->>DB: any ACTIVE ticket for plate?
    PS->>VS: findOrRegister(plate, type)
    VS->>DB: find / insert vehicle
    PS->>SS: allocateSpot(type)
    SS->>DB: SELECT free spots FOR UPDATE (locked)
    SS->>AS: selectSpot(freeSpots, type)
    AS-->>SS: best spot
    SS->>DB: spot.available = false
    PS->>DB: insert ticket (ACTIVE, entryTime)
    PS-->>PC: TicketResponse
    PC-->>C: 201 Created
```

## 6. Check-out Sequence

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ParkingController
    participant PS as ParkingService
    participant FS as FeeStrategy
    participant SS as SpotService
    participant DB as Database

    C->>PC: POST /api/parking/check-out
    PC->>PS: checkOut(request)
    PS->>DB: find ACTIVE ticket (with vehicle + spot)
    PS->>FS: calculateFee(type, duration)
    FS-->>PS: fee
    PS->>PS: ticket.close(exitTime, fee)
    PS->>SS: releaseSpot(spot)
    SS->>DB: spot.available = true
    PS-->>PC: TicketResponse
    PC-->>C: 200 OK
```

Both flows run in a single transaction, so either everything is saved or nothing is.
