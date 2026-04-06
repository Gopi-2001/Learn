# Tight Coupling Example

This example demonstrates the concept of **tight coupling** in object-oriented programming using Java.

## Context

In this code, we have two main classes:

- **UserManager**: Responsible for managing user-related operations.
- **UserDatabase**: Provides user details (simulated as a hardcoded string).

The `UserManager` class directly creates and uses an instance of `UserDatabase`:

```java
private UserDatabase userDatabase = new UserDatabase();
```

This means `UserManager` is tightly coupled to the `UserDatabase` implementation.

## How It Works

- The entry point is `TightCouplingExample.java`.
- In `main`, a `UserManager` object is created.
- `UserManager.getUserInfo()` is called, which internally calls `UserDatabase.getUserDetails()`.
- The result is printed to the console.

**Output:**
```
User Details from Database
```

## Why Is This Tight Coupling?

- `UserManager` is directly dependent on the concrete `UserDatabase` class.
- If you want to switch to a different data source (e.g., another database, a web service, or a mock for testing), you must modify the `UserManager` code.
- This makes the code less flexible and harder to maintain, especially as requirements change.

## When Is This Approach Used?

- For simple, small-scale applications or learning purposes.
- When you do not expect the implementation to change.

## What Are the Drawbacks?

- Difficult to extend or modify (e.g., switching databases).
- Harder to test (cannot easily substitute a mock database).
- Violates the Open/Closed Principle (code is not open for extension, only modification).

## Next Steps

To make the code more flexible and maintainable, you can refactor it to use **loose coupling** (e.g., by introducing interfaces and dependency injection). This is especially important in larger or production-grade applications.

---
**This example is intentionally tightly coupled to illustrate the concept and