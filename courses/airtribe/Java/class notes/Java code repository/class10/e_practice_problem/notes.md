## Scenario
You're building a notification system for an e-commerce platform. When an order is placed, multiple services need to be notified, the message must pass fraud checks, the notification channel is chosen at runtime, and the message can be decorated with extra formatting.

## Requirements

### 1. Observer Pattern — Order Events
- `OrderService` is the Subject
- When a new order is placed, notify all registered listeners
- Listeners: `InventoryService`, `NotificationService`, `AnalyticsService`

### 2. Strategy Pattern — Notification Channels
- `NotificationService` uses a `NotificationChannel` strategy
- Channels: `EmailChannel`, `SMSChannel`, `PushChannel`
- Channel can be swapped at runtime

### 3. Chain of Responsibility — Fraud Checking
- Before sending notification, the order passes through a fraud check chain
- Chain: `AmountChecker` (rejects orders > $10,000) → `LocationChecker` (rejects blacklisted countries) → `FrequencyChecker` (rejects if > 5 orders/hour from same user)
- If any checker flags fraud → notification is blocked

### 4. Decorator Pattern — Message Formatting
- Base message: order details
- Decorators: `UrgencyDecorator` (adds [URGENT] prefix for high-value orders), `TimestampDecorator` (prepends timestamp), `SignatureDecorator` (appends company signature)
- Decorators are stackable

## Expected Output
```
=== E-Commerce Notification System ===

--- Order 1: Normal order ---
  [Inventory] Reserving stock for Order#1001: Laptop ($999.00)
  [Analytics] Tracking Order#1001: Laptop ($999.00)
  [Fraud Check] AmountChecker: PASSED
  [Fraud Check] LocationChecker: PASSED
  [Fraud Check] FrequencyChecker: PASSED
  [EMAIL] 2024-01-15 10:30 | New order: Laptop ($999.00) — MyStore Inc.

--- Order 2: High-value order ---
  [Inventory] Reserving stock for Order#1002: Server Rack ($15000.00)
  [Analytics] Tracking Order#1002: Server Rack ($15000.00)
  [Fraud Check] AmountChecker: BLOCKED — Amount $15000.00 exceeds limit

--- Order 3: Switch to SMS channel ---
  [Inventory] Reserving stock for Order#1003: Keyboard ($79.00)
  [Analytics] Tracking Order#1003: Keyboard ($79.00)
  [Fraud Check] AmountChecker: PASSED
  [Fraud Check] LocationChecker: PASSED
  [Fraud Check] FrequencyChecker: PASSED
  [SMS] New order: Keyboard ($79.00)
```

## Hints
- Start with the `Order` class (id, item, amount, country, userId)
- Build each pattern independently, then wire them together
- `NotificationService` is both an Observer (listens for orders) and uses Strategy (channel) + Chain (fraud) + Decorator (formatting)