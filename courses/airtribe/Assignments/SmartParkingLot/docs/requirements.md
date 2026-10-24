# Requirements

## Problem

A multi-floor parking lot in a city needs a backend that assigns spots by vehicle size, tracks how long each vehicle stays and charges a fee on exit.

## Functional Requirements

| ID | Requirement | Implemented in |
|---|---|---|
| FR-1 | Assign a free spot automatically on entry, based on vehicle size (motorcycle, car, bus) | `SpotService.allocateSpot`, `BestFitSpotAllocationStrategy` |
| FR-2 | Record entry time on check-in | `ParkingService.checkIn` → `ParkingTicket.entryTime` |
| FR-3 | Record exit time on check-out | `ParkingService.checkOut` → `ParkingTicket.close()` |
| FR-4 | Calculate fee from duration and vehicle type | `FeeStrategy`, `HourlyFeeStrategy` |
| FR-5 | Update spot availability in real time on entry and exit | `ParkingSpot.occupy() / release()`, `GET /api/spots/availability` |
| FR-6 | Support multiple floors | `ParkingSpot.floorNumber`, `DataLoader` (3 floors) |
| FR-7 | Reject a vehicle that is already parked | `ParkingService.checkIn` (active ticket check) |
| FR-8 | Allow adding new spots | `POST /api/spots`, `SpotService.addSpot` |

## Non-Functional Requirements

| ID | Requirement | How it is met |
|---|---|---|
| NFR-1 | Concurrency: many vehicles can enter/exit at the same time without two getting the same spot | Pessimistic row lock (`SELECT ... FOR UPDATE`) + `@Version` optimistic lock |
| NFR-2 | Consistency: ticket and spot status always match | Check-in and check-out each run in one `@Transactional` method |
| NFR-3 | Extensibility: new pricing or allocation rules without changing core services | Strategy interfaces (`FeeStrategy`, `SpotAllocationStrategy`) |
| NFR-4 | Testability | Constructor injection, injected `Clock`, unit + integration tests |
| NFR-5 | Clear errors | `GlobalExceptionHandler` returns JSON with status 400 / 404 / 409 |
| NFR-6 | Money accuracy | Fee stored as `BigDecimal` |

## Business Rules

**Vehicle → spot size (best fit first)**

| Vehicle | Allowed spots |
|---|---|
| MOTORCYCLE | SMALL, MEDIUM, LARGE |
| CAR | MEDIUM, LARGE |
| BUS | LARGE |

**Allocation order:** smallest fitting size → lowest floor → lowest spot number.

**Fee:** `hourly rate × hours`, every started hour is charged, minimum 1 hour.

| Vehicle | Rate / hour |
|---|---|
| MOTORCYCLE | 10 |
| CAR | 20 |
| BUS | 50 |

## Assumptions

- A vehicle is identified by its licence plate (stored in upper case, trimmed).
- A plate is always the same vehicle type.
- One vehicle uses exactly one spot (a bus does not take several car spots).
- Payment happens outside this system; we only calculate the amount.
- Single parking lot.

## Out of Scope (YAGNI)

Payments, reservations, multiple lots, user login, EV charging spots, display boards, discounts.
