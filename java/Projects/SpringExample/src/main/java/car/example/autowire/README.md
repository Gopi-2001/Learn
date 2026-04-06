# @Autowired: Dependency Injection in Spring

## Overview
`@Autowired` is a Spring Framework annotation that automatically **injects dependencies** into a class. It tells Spring: *"Find a matching bean and inject it here, I don't want to do it manually."*

This folder demonstrates three different autowiring strategies using XML configuration:
1. **Constructor Autowiring** - Inject via constructor parameters
2. **byName Autowiring** - Inject via setter methods matching bean names
3. **byType Autowiring** - Inject via setter methods matching bean types

---

## The Problem @Autowired Solves

### ❌ WITHOUT Autowiring (Manual Dependency Injection)

```java
// Old Way - No autowiring
public class Car {
    private Specification specification;
    
    public Car(Specification specification) {
        this.specification = specification;  // Manually passing dependency
    }
}

// In application code - you have to create the dependency
Specification spec = new Specification();
spec.setMake("Toyota");
spec.setModel("Camry");
Car car = new Car(spec);  // Manually inject it
```

**Problems:**
- ❌ Verbose boilerplate code
- ❌ Must manually create all dependencies
- ❌ Difficult to manage when dependencies are complex
- ❌ Easy to forget or pass wrong dependency
- ❌ Tightly couples your code to dependency creation
- ❌ Hard to test (must always create all dependencies)

### ✅ WITH Autowiring (Automatic Dependency Injection)

```java
// New Way - With autowiring
public class Car {
    @Autowired
    private Specification specification;
    
    public void displayDetails() {
        System.out.println(specification.toString());  // Spring injected it!
    }
}

// In application code - Spring handles everything
Car car = context.getBean("myCar");  // Specification already injected!
car.displayDetails();
```

**Benefits:**
- ✅ No boilerplate code
- ✅ Spring creates and injects dependencies automatically
- ✅ Clean, readable code
- ✅ Easy to manage complex dependency graphs
- ✅ Loosely coupled (depends on abstraction, not concrete creation)
- ✅ Simple to test (mock dependencies easily)

---

## How @Autowired Works Behind the Scenes

### The Autowiring Process (Step-by-Step)

```
┌─────────────────────────────────────────────────────────┐
│ 1. Spring reads configuration (XML or annotations)     │
│    Finds: @Autowired annotation on a field/method      │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Dependency Resolution Strategy                       │
│    Spring asks: Which bean should I inject?            │
│    ┌─────────────────────────────────────────────────┐ │
│    │ Strategy 1: By Type (Default)                  │ │
│    │ - Find bean matching the type                  │ │
│    │                                                 │ │
│    │ Strategy 2: By Name                            │ │
│    │ - Find bean matching the field/parameter name  │ │
│    │                                                 │ │
│    │ Strategy 3: By Qualifier                       │ │
│    │ - Use @Qualifier to explicitly specify        │ │
│    └─────────────────────────────────────────────────┘ │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Bean Lookup                                          │
│    Spring searches in its container:                    │
│    "Is there a bean matching the criteria?"            │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ 4. Bean Injection                                       │
│    Spring injects the found bean                       │
│    - Via constructor                                    │
│    - Via setter method                                 │
│    - Via field (reflection)                            │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ 5. Object Ready                                         │
│    Your class now has the dependency!                  │
└─────────────────────────────────────────────────────────┘
```

### Real Example: What Happens Internally

```java
public class Car {
    @Autowired
    private Specification specification;  // ← Mark for injection
}
```

**Spring does this internally:**

```java
// 1. Spring creates Car instance
Car car = new Car();

// 2. Spring sees @Autowired on 'specification' field
// 3. Spring looks for a bean of type Specification
Specification spec = container.findBeanByType(Specification.class);

// 4. Spring uses reflection to set the field
Field field = Car.class.getDeclaredField("specification");
field.setAccessible(true);
field.set(car, spec);  // ← Injects the dependency

// 5. Now car.specification is available
```

---

## Understanding the Three Autowiring Types

### 1. Constructor Autowiring

#### What It Does
Spring matches **constructor parameters** with available beans.

#### Example Files
- `constructor/Car.java`
- `constructor/Specification.java`
- `constructor/App.java`

#### The Code

```java
package car.example.autowire.constructor;

public class Car {
    private Specification specification2;
    
    // Constructor with dependency parameter
    public Car(Specification specification2) {
        this.specification2 = specification2;  // ← Injected by Spring
    }
    
    public void displayDetails() {
        System.out.println("Car Details: " + specification2.toString());
    }
}
```

#### XML Configuration (XML-based autowiring)

```xml
<bean id="specification" class="car.example.autowire.constructor.Specification">
    <property name="make" value="Toyota"/>
    <property name="model" value="Camry"/>
</bean>

<bean id="myCar" class="car.example.autowire.constructor.Car" autowire="constructor">
    <!-- Spring will automatically call Car(specification) -->
</bean>
```

#### How It Works

```
<bean ... autowire="constructor">
         ↓
Spring looks at Car's constructor parameters
         ↓
Found: Specification specification2
         ↓
Spring searches for Specification bean in container
         ↓
Found: <bean id="specification" ...>
         ↓
Spring calls: new Car(specification)  ← Constructor injection!
```

#### Key Points

✅ **Advantages:**
- Immutable dependencies (set in constructor)
- Clear which dependencies are required
- Can't have circular dependencies
- Type-safe
- Easy to test (pass mock in constructor)

❌ **Disadvantages:**
- Constructor must match bean names exactly
- Can't have optional dependencies
- Less flexible for multiple beans of same type

---

### 2. Autowiring by Name (byName)

#### What It Does
Spring matches **setter method names** with bean IDs.

The setter name (without "set" prefix) must match a bean ID.

#### Example Files
- `name/Car.java`
- `name/Specification.java`
- `name/App.java`

#### The Code

```java
package car.example.autowire.name;

public class Car {
    private Specification specification;
    private Specification specification1;
    
    // Setter 1: Method name "setSpecification" → looks for bean "specification"
    public void setSpecification(Specification specification) {
        this.specification = specification;
    }
    
    // Setter 2: Method name "setSpecification1" → looks for bean "specification1"
    public void setSpecification1(Specification specification) {
        this.specification1 = specification;
    }
    
    public void displayDetails() {
        System.out.println("Car Details: " + specification.toString());
    }
    
    public void displayDetails1() {
        System.out.println("Car Details1: " + specification1.toString());
    }
}
```

#### XML Configuration

```xml
<bean id="specification" class="car.example.autowire.name.Specification">
    <property name="make" value="Toyota"/>
    <property name="model" value="Camry"/>
</bean>

<bean id="specification1" class="car.example.autowire.name.Specification">
    <property name="make" value="Honda"/>
    <property name="model" value="Civic"/>
</bean>

<bean id="myCar" class="car.example.autowire.name.Car" autowire="byName">
    <!-- Spring will call: -->
    <!-- car.setSpecification(specification bean) -->
    <!-- car.setSpecification1(specification1 bean) -->
</bean>
```

#### How It Works

```
<bean ... autowire="byName">
         ↓
Spring inspects Car's setter methods
         ↓
Found: setSpecification()
    Extract property name: "specification"
    Look for bean with id="specification"
         ↓
Found: <bean id="specification" ...>
         ↓
Spring calls: car.setSpecification(specification bean)
```

**Matching Logic:**
```
Method Name   → Extract Name → Look for Bean
setSpecification → "specification" → bean id="specification"
setSpecification1 → "specification1" → bean id="specification1"
setMake → "make" → bean id="make"
```

#### Key Points

✅ **Advantages:**
- Multiple beans of same type (different IDs)
- Flexible - can pick which bean to inject
- Optional dependencies (only inject what matches)
- Clear intent (bean ID determines which one)

❌ **Disadvantages:**
- Bean ID must match setter name exactly
- Fragile - renaming setters or beans breaks autowiring
- Error-prone (no compile-time checking)
- Harder to track which bean is injected
- Not recommended for new code

---

### 3. Autowiring by Type (byType)

#### What It Does
Spring matches **setter method parameter type** with available beans of that type.

#### Example Files
- `type/Car.java`
- `type/Specification.java`
- `type/App.java`

#### The Code

```java
package car.example.autowire.type;

public class Car {
    private Specification specification2;
    
    // Setter parameter type is Specification
    // Spring looks for any bean of type Specification
    public void setSpecification(Specification specification) {
        this.specification2 = specification;  // ← Injected by Spring
    }
    
    public void displayDetails() {
        System.out.println("Car Details: " + specification2.toString());
    }
}
```

#### XML Configuration

```xml
<bean id="spec1" class="car.example.autowire.type.Specification">
    <property name="make" value="Toyota"/>
    <property name="model" value="Camry"/>
</bean>

<bean id="myCar" class="car.example.autowire.type.Car" autowire="byType">
    <!-- Spring will look for a bean of type Specification -->
    <!-- Found: spec1 (type is Specification) -->
    <!-- Call: car.setSpecification(spec1) -->
</bean>
```

#### How It Works

```
<bean ... autowire="byType">
         ↓
Spring inspects Car's setter methods
         ↓
Found: setSpecification(Specification specification)
    Parameter type: Specification
    Look for bean of type Specification
         ↓
Found: <bean id="spec1" class="Specification">
         ↓
Spring calls: car.setSpecification(spec1)
```

#### Key Points

✅ **Advantages:**
- Flexible - bean ID doesn't matter
- Matches by type (semantically clearer)
- Safe - type-checked at runtime
- Works with inheritance hierarchies
- Recommended for autowiring

❌ **Disadvantages:**
- **Problem: Multiple beans of same type** (which one to inject?)
- Harder to visualize which bean is injected
- Less flexible than byName for multiple implementations

---

## Comparison Table: The Three Types

| Aspect | Constructor | By Name | By Type |
|--------|-------------|---------|---------|
| **Matching Strategy** | Constructor params | Setter method names | Setter parameter types |
| **What It Matches** | Parameter names | Method name - "set" prefix | Parameter type/class |
| **XML Example** | `autowire="constructor"` | `autowire="byName"` | `autowire="byType"` |
| **Multiple Beans** | ❌ Not supported | ✅ Supported (different IDs) | ⚠️ Problem if multiple |
| **Immutability** | ✅ Constructor = immutable | ❌ Setters = mutable | ❌ Setters = mutable |
| **Readability** | ✅ Best | ⚠️ Depends on naming | ✅ Good |
| **Fragility** | Medium | ❌ Fragile (name-dependent) | Low |
| **Recommended** | ✅ Yes | ❌ No (legacy) | ✅ Yes |
| **Flexibility** | Low | ✅ High | Medium |

---

## XML-Based vs Annotation-Based (@Autowired)

### XML-Based Autowiring (Traditional)

```xml
<!-- autowireByConstructor.xml -->
<bean id="specification" class="car.example.autowire.constructor.Specification">
    <property name="make" value="Toyota"/>
</bean>

<bean id="myCar" class="car.example.autowire.constructor.Car" autowire="constructor">
    <!-- Spring automatically injects via constructor -->
</bean>
```

**Pros:**
- Configuration separate from code
- Easy to change without recompiling
- All wiring visible in one place

**Cons:**
- Verbose XML files
- Easy to make mistakes
- No IDE support for validation

### Annotation-Based @Autowired (Modern)

```java
@Component
public class Specification {
    private String make = "Toyota";
    // ...
}

@Component
public class Car {
    @Autowired  // ← Modern approach
    private Specification specification;
}
```

**Pros:**
- Clean, concise code
- IDE support and validation
- Closer to code (easier to understand)
- Less XML boilerplate
- Type-safe (compile-time checking)

**Cons:**
- Coupling configuration to code
- Must use classpaths scanning or component detection

---

## @Autowired vs @Inject vs @Resource

| Annotation | Source | Type | By Name | By Type | Notes |
|-----------|--------|------|---------|---------|-------|
| `@Autowired` | Spring | By Type | ✅ With `@Qualifier` | ✅ Default | Spring-specific |
| `@Inject` | Java (JSR-330) | By Type | ⚠️ Complex | ✅ Default | Standard Java |
| `@Resource` | Java (JSR-250) | **By Name** | ✅ Default | ⚠️ Fallback | Standard Java |

**Which to use?**
- **@Autowired** - Spring applications (most common)
- **@Inject** - When you want standards (portable across frameworks)
- **@Resource** - When you need name-based matching

---

## How to Resolve "Multiple Beans of Same Type" Problem

### Problem: Which bean to inject?

```java
// Multiple Specification beans exist
// Which one should Spring inject?

@Autowired
private Specification specification;  // Ambiguous!
```

### Solution 1: Using @Qualifier

```java
@Autowired
@Qualifier("specification1")  // ← Explicitly specify which bean
private Specification specification;
```

### Solution 2: Using @Primary

```xml
<bean id="specification" class="..." primary="true">
    <!-- This is the default choice -->
</bean>
```

### Solution 3: List all beans

```java
@Autowired
private List<Specification> specifications;  // Get all Specification beans
```

---

## Behind-the-Scenes: Spring's Autowiring Process

### Phase 1: Bean Discovery
```
Spring scans classpath for:
├─ @Component classes
├─ @Configuration classes
├─ Beans defined in XML
└─ Beans defined via @Bean methods
```

### Phase 2: Instantiation
```
Spring creates bean instances:
├─ Constructor injection (if using constructor)
├─ Field injection (using reflection)
└─ Setter injection (calling setter methods)
```

### Phase 3: Dependency Resolution
```
For each @Autowired:
├─ Determine dependency type
├─ Search for matching bean
│  ├─ By type (default)
│  ├─ By name (if @Qualifier)
│  └─ By custom criteria
└─ Inject the bean
```

### Phase 4: Post-Processing
```
After injection:
├─ Call @PostConstruct methods
├─ Run BeanPostProcessors
└─ Bean is ready to use
```

---

## Autowiring Resolution Order

When you use `@Autowired`, Spring searches in this order:

```
1. By Type (exact match)
   └─ Found? Return it
   └─ Not found? → Continue to step 2

2. By Name (@Qualifier or variable name)
   └─ Found? Return it
   └─ Not found? → Continue to step 3

3. By @Primary annotation
   └─ Found? Return it
   └─ Not found? → Throw NoSuchBeanDefinitionException
```

---

## Practical Examples

### Example 1: Field Injection

```java
@Component
public class UserService {
    @Autowired
    private UserRepository repository;  // ← Injected here
    
    public User getUser(int id) {
        return repository.findById(id);
    }
}
```

### Example 2: Constructor Injection (Recommended)

```java
@Component
public class UserService {
    private final UserRepository repository;
    
    @Autowired
    public UserService(UserRepository repository) {  // ← Injected via constructor
        this.repository = repository;
    }
}
```

### Example 3: Setter Injection

```java
@Component
public class UserService {
    private UserRepository repository;
    
    @Autowired
    public void setRepository(UserRepository repository) {  // ← Injected via setter
        this.repository = repository;
    }
}
```

### Example 4: Optional Dependencies

```java
@Component
public class UserService {
    @Autowired(required = false)  // ← This bean is optional
    private Logger logger;
    
    public void logMessage(String msg) {
        if (logger != null) {
            logger.log(msg);
        }
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use Constructor Injection** (most common for required dependencies)
   ```java
   @Autowired
   public UserService(UserRepository repository) {
       this.repository = repository;
   }
   ```

2. **Use @Qualifier for multiple beans**
   ```java
   @Autowired
   @Qualifier("primaryDatabase")
   private DataSource dataSource;
   ```

3. **Use Type matching** (byType is safer)
   ```java
   // Good - Spring matches by type
   @Autowired
   private UserRepository repository;
   ```

4. **Keep dependencies immutable** (constructor injection)
   ```java
   private final UserRepository repository;
   ```

5. **Use interfaces/abstractions**
   ```java
   @Autowired
   private UserRepository repository;  // Interface, not concrete class
   ```

### ❌ DON'T:

1. **Avoid Field Injection** (hard to test, immutability issues)
   ```java
   @Autowired
   private UserRepository repository;  // ❌ Not recommended
   ```

2. **Avoid Circular Dependencies**
   ```java
   // ❌ Bad: A needs B, B needs A
   @Autowired
   private ServiceB serviceB;  // If ServiceB needs this service
   ```

3. **Don't rely on byName exclusively**
   ```java
   // ❌ Fragile: depends on exact method names
   autowire="byName"
   ```

4. **Don't mix injection types** unnecessarily
   ```java
   @Autowired
   private Repository repo;  // Already @Autowired
   
   @Autowired
   public void setRepository(Repository r) {} // Don't also do this
   ```

---

## File Structure

```
car/example/autowire/
├── constructor/
│   ├── Car.java                 # Constructor injection example
│   ├── Specification.java       # Dependency class
│   └── App.java                 # Main application
│
├── name/
│   ├── Car.java                 # byName autowiring example
│   ├── Specification.java       # Dependency class
│   └── App.java                 # Main application
│
├── type/
│   ├── Car.java                 # byType autowiring example
│   ├── Specification.java       # Dependency class
│   └── App.java                 # Main application
│
└── README.md                     # This file
```

---

## Real-World Analogy

Think of autowiring like a **restaurant ordering system:**

### WITHOUT Autowiring (BEFORE)
```
You: "I need food"
Manager: "OK, you must go buy ingredients, cook the food, then bring it here"
You: "But... I just wanted food!"
Manager: "That's how it works"
```

**Problems:**
- You have to do all the work
- Complex and error-prone
- Frustrating

### WITH Autowiring (AFTER)
```
You: "I need food" ← @Autowired annotation
Manager: "OK, I'll find a chef in my kitchen and send food to you"
Spring Container: [Searches for Chef bean, creates food, delivers to you]
You: "Thanks! Food arrived!" ← No manual work needed
```

**Benefits:**
- Manager handles everything
- You focus on eating (using the food)
- Simple and clean

---

## Learning Progression

1. ✅ Understand loose coupling concept
2. ✅ Learn marker annotations (@Component)
3. ✅ Learn component scanning
4. ✅ **Learn autowiring (constructor, byName, byType)** ← You are here
5. 📚 Learn @Qualifier for multiple beans
6. 📚 Learn constructor vs setter injection trade-offs
7. 📚 Learn circular dependency issues
8. 📚 Learn Spring Boot's @SpringBootApplication

---

## Summary

| Concept | Key Takeaway |
|---------|--------------|
| **@Autowired** | Tells Spring to automatically inject dependencies |
| **Constructor** | Immutable, clear requirements, highly recommended |
| **byName** | Flexible but fragile, legacy approach |
| **byType** | Type-safe, modern approach, handles multiple with @Qualifier |
| **Spring handles** | Finding, creating, and injecting dependent objects |
| **You write** | Just the class with @Autowired, Spring does the rest |

---

## Conclusion

**@Autowired solves the core problem of modern applications:** How do you manage complex dependencies without writing lots of boilerplate code?

**Answer:** Let Spring do it automatically!

By marking fields, constructors, or methods with `@Autowired`, you tell Spring: *"I need this dependency, find it and inject it."* Spring's container handles discovery, creation, and injection of all dependencies in the correct order.

This is one of Spring Framework's most powerful features and fundamental to modern Java development!

