# Requirements

## Functional Requirements

1. Register riders
2. Register drivers
3. Show available drivers
4. Request a ride
5. Match the ride to a driver using a strategy
6. Calculate fare using a pricing strategy
7. Track ride status - `REQUESTED`, `ASSIGNED`, `COMPLETED`, `CANCELLED`

## Non-Functional Requirements

- Pricing algorithm should be easy to extend
- Driver matching logic should be easy to change
- Low coupling between services
- Code should be readable and maintainable

## Console Menu

1. Add Rider
2. Add Driver
3. View Available Drivers
4. Request Ride
5. Complete Ride
6. View Rides
7. Exit

Every option uses only the service layer and handles invalid input (non-numbers, empty text,
wrong ids, wrong menu option) without crashing.

## Ride Status Flow

```
REQUESTED --(driver found)--> ASSIGNED --(complete)--> COMPLETED
    |
    +--(no driver free)--> CANCELLED
```

## Out of Scope (YAGNI)

Login, payments, maps/GPS, database, ratings. These can be added later without changing the core design.
