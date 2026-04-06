# @Autowired with Annotations: Modern Dependency Injection in Spring

## Overview
This folder demonstrates **@Autowired annotation-based dependency injection** with automatic component scanning. It's the modern, recommended way to handle dependency injection in Spring applications.

Unlike the XML-based autowiring examples, this approach uses:
- `@Component` to mark classes as beans
- `@ComponentScan` to auto-discover components
- `@Autowired` to automatically inject dependencies
- `@Qualifier` to specify which bean to inject (when multiple exist)

---

## Quick Comparison: XML vs Annotation-Based

| Aspect | XML-Based Autowiring | Annotation-Based @Autowired |
|--------|----------------------|---------------------------|
| **Configuration** | XML files (autowire="constructor") | Java annotations (@Autowired) |
| **Bean Declaration** | `<bean>` tags in XML | `@Component` annotation |
| **Dependency Injection** | `autowire="byConstructor\|byName\|byType"` | `@Autowired` annotation |
| **Discovery** | `<context:component-scan>` | `@ComponentScan` annotation |
| **Context Loading** | `ClassPathXmlApplicationContext` | `AnnotationConfigApplicationContext` |
| **Coupling** | Configuration separate from code | Configuration in code |
| **Type Safety** | No compile-time checking | Full IDE support, type-safe |
| **Flexibility** | Easy to change without code | Must recompile to change |
| **Modern Usage** | ❌ Legacy | ✅ Current standard |
| **Verbosity** | More XML boilerplate | Less code, cleaner |

---

## The Example in This Folder

### File Structure

```
com/example/autowired/annotation/
├── Employee.java           # Dependency bean
├── Manager.java            # Uses @Autowired to inject Employee
├── AppConfig.java          # Configuration class
├── App.java                # Main application
└── README.md               # This file
```

### How It Works - The Flow

```
┌─────────────────────────────────────────────────────────┐
│ Step 1: Application Startup                            │
│ App.main() → new AnnotationConfigApplicationContext    │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ Step 2: Spring Loads Configuration                      │
│ Loads AppConfig.class                                   │
│ Reads: @Configuration, @ComponentScan                   │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ Step 3: Component Scanning                              │
│ Scan package: com.example.autowired.annotation          │
│ Look for: @Component annotations                        │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ Step 4: Bean Registration                               │
│ Found @Component("employee") → Register Employee bean   │
│ Found @Component("manager1") → Register Manager bean    │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ Step 5: Dependency Injection                            │
│ Manager has @Autowired on employee field               │
│ Look for: bean of type Employee                         │
│ Found: @Qualifier("employee")                           │
│ Inject: Employee bean into Manager.employee            │
└────────────────┬──────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────────────────────────┐
│ Step 6: Beans Ready                                     │
│ Employee bean fully initialized                         │
│ Manager bean initialized with Employee dependency       │
└─────────────────────────────────────────────────────────┘
```

---

## The Code - Line by Line

### 1. Employee.java - The Dependency

```java
package com.example.autowired.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("employee")  // ← Mark this class as a Spring bean with id "employee"
public class Employee {
    @Value("1")        // ← Inject simple values
    private int employeeId;

    @Value("Hello")
    private String firstName;

    @Value("${java.home}")  // ← Inject system properties
    private String lastName;

    @Value("#{4*4}")  // ← Inject SpEL expressions
    private double salary;

    // Getters and setters...
    
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                '}';
    }
}
```

**Key Points:**
- `@Component("employee")` - Registers this class as a bean
- Custom name "employee" becomes the bean ID
- `@Value` annotates fields to inject values
- Multiple injection types: literals, properties, SpEL expressions

### 2. Manager.java - The Dependent Class

```java
package com.example.autowired.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("manager1")  // ← Register Manager as a bean with id "manager1"
public class Manager {
    @Autowired           // ← Tell Spring to inject a dependency here
    @Qualifier("employee")  // ← Specify WHICH bean to inject (by id)
    private Employee employee;  // ← The injected dependency

    @Override
    public String toString() {
        return "Manager{" +
                "employee=" + employee +
                '}';
    }
}
```

**Key Points:**
- `@Autowired` - Tells Spring to automatically inject an Employee bean
- `@Qualifier("employee")` - Specifies which bean to inject (by ID)
- Without `@Qualifier`, Spring would search by type (might cause ambiguity)
- Spring uses reflection to set this field (no need for setter or constructor)

**Alternative: Constructor Injection (Recommended)**
```java
@Component("manager1")
public class Manager {
    private final Employee employee;
    
    @Autowired  // ← Annotation on constructor
    public Manager(Employee employee) {  // ← Dependency as constructor parameter
        this.employee = employee;
    }
}
```

### 3. AppConfig.java - Configuration Class

```java
package com.example.autowired.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration  // ← Make this class a Spring configuration
@ComponentScan(basePackages = "com.example.autowired.annotation")  // ← Scan this package
public class AppConfig {
    // Empty! Component scanning and @Autowired handle everything
}
```

**What Happens Here:**
1. `@Configuration` - This is a configuration class (like XML `<beans>` tag)
2. `@ComponentScan` - Tells Spring where to find @Component classes
3. Spring will scan the specified package and auto-detect beans
4. All `@Autowired` fields will be automatically wired

### 4. App.java - The Application

```java
package com.example.autowired.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args){
        // Create Spring context using Java configuration
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve beans from context
        Employee employee = (Employee) context.getBean("employee");
        Manager manager = context.getBean("manager1", Manager.class);

        // Beans are fully initialized with dependencies injected
        System.out.println(employee.toString());
        // Output: Employee{employeeId=1, firstName='Hello', lastName='C:\...\java', salary=16.0}
        
        System.out.println(manager.toString());
        // Output: Manager{employee=Employee{employeeId=1, firstName='Hello', ...}}
    }
}
```

**Key Points:**
- `AnnotationConfigApplicationContext` - Loads configuration from Java class
- Pass `AppConfig.class` to load the configuration
- Spring automatically scans, detects, and injects all dependencies
- No XML files needed!

---

## Understanding @Autowired in Depth

### What @Autowired Does

```java
@Autowired
private Employee employee;
```

**Spring internally does this:**

```java
// 1. Create Manager instance
Manager manager = new Manager();

// 2. See @Autowired on employee field
// 3. Determine what type to inject: Employee.class

// 4. Look for bean of type Employee in container
Employee employeeBean = container.findBeanByType(Employee.class);

// 5. If @Qualifier present, refine the search
employeeBean = container.findBeanByQualifier("employee");

// 6. Use reflection to inject the bean
Field field = Manager.class.getDeclaredField("employee");
field.setAccessible(true);
field.set(manager, employeeBean);

// 7. Now manager.employee is populated
return manager;
```

### Dependency Resolution Strategy

When you use `@Autowired`, Spring searches in this order:

```
1. Check for @Qualifier annotation
   ↓ (if present: Look for bean with that specific ID/name)
   ↓ (if found: Use it)
   ↓ (if not found: Continue)

2. By Type Matching
   ↓ (Look for bean matching the field type)
   ↓ (if found exactly ONE: Use it)
   ↓ (if found MULTIPLE: Try step 3)

3. By Field Name
   ↓ (Look for bean with ID matching field name)
   ↓ (if found: Use it)
   ↓ (if not found: Continue)

4. Fail
   ↓ (Throw NoSuchBeanDefinitionException)
```

---

## @Autowired Injection Types in Annotation-Based

### 1. Field Injection (Shown in Example)

```java
@Component
public class Manager {
    @Autowired
    @Qualifier("employee")
    private Employee employee;  // ← Injected directly into field
}
```

**Pros:**
- Simple, clean syntax
- Easy to understand

**Cons:**
- ❌ Hard to test (need to use reflection)
- ❌ Dependencies not immutable
- ❌ Hidden dependencies (not obvious from constructor)
- ⚠️ **Not recommended for production code**

### 2. Constructor Injection (Recommended)

```java
@Component
public class Manager {
    private final Employee employee;  // ← Immutable
    
    @Autowired
    public Manager(Employee employee) {
        this.employee = employee;
    }
}
```

**Pros:**
- ✅ Dependencies immutable (final)
- ✅ Clear required dependencies
- ✅ Easy to test
- ✅ Supports circular dependency detection
- ✅ **Recommended for production code**

**Cons:**
- Slightly more verbose

### 3. Setter Injection

```java
@Component
public class Manager {
    private Employee employee;
    
    @Autowired
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
```

**Pros:**
- Dependencies are optional (can use `@Autowired(required=false)`)
- Flexible

**Cons:**
- ❌ Dependencies are mutable
- ❌ Not clear which are required
- ⚠️ Not recommended

### 4. Optional Injection

```java
@Component
public class Manager {
    @Autowired(required = false)  // ← This bean is optional
    private Logger logger;
    
    public void log(String msg) {
        if (logger != null) {
            logger.log(msg);
        }
    }
}
```

---

## @Qualifier vs @Qualifier with @Component

### Problem: Multiple Beans of Same Type

```java
@Component
public class Employee { }

@Component
public class Contractor { }  // Also an Employee-like class?
```

**Which one does @Autowired inject?**

```java
@Autowired
private Employee employee;  // Ambiguous!
```

### Solution 1: @Qualifier on Injection

```java
@Component
public class Manager {
    @Autowired
    @Qualifier("employee")  // ← Specify which bean
    private Employee employee;
}
```

### Solution 2: @Qualifier on Bean Definition

```java
@Component
@Qualifier("primaryEmployee")
public class Employee { }

// Then inject
@Autowired
@Qualifier("primaryEmployee")
private Employee employee;
```

### Solution 3: @Primary Annotation

```java
@Component
@Primary  // ← This is the default choice
public class Employee { }

// Then inject without specifying
@Autowired
private Employee employee;  // Uses @Primary bean
```

### Solution 4: Named Components

```java
@Component("fullTimeEmployee")
public class Employee { }

@Component("contractor")
public class Contractor { }

// Inject by name
@Autowired
@Qualifier("fullTimeEmployee")
private Employee employee;
```

---

## Step-by-Step Execution

### What Happens When You Run the App

```
1. JVM starts
   ↓
2. App.main() executes
   ↓
3. new AnnotationConfigApplicationContext(AppConfig.class)
   ├─ Reads AppConfig.class
   ├─ Finds @Configuration annotation
   ├─ Finds @ComponentScan annotation
   └─ Scans com.example.autowired.annotation package
   ↓
4. Component Scanning
   ├─ Finds Employee.java with @Component("employee")
   │   └─ Registers: bean id="employee", type=Employee
   ├─ Finds Manager.java with @Component("manager1")
   │   └─ Registers: bean id="manager1", type=Manager
   └─ Finds AppConfig.java with @Configuration
       └─ Already processed
   ↓
5. Dependency Injection (AutoWiring)
   ├─ Processing Manager beans
   ├─ Found @Autowired on Manager.employee field
   ├─ Look for Employee bean
   │   └─ Found: bean id="employee", @Qualifier matches
   ├─ Inject Employee bean into Manager
   └─ Manager initialization complete
   ↓
6. Return fully initialized beans
   ├─ Employee bean ready
   └─ Manager bean ready with Employee injected
   ↓
7. Application code calls context.getBean()
   ├─ context.getBean("employee") → Employee instance
   └─ context.getBean("manager1", Manager.class) → Manager with Employee
   ↓
8. Output:
   Employee{employeeId=1, firstName='Hello', lastName='...', salary=16.0}
   Manager{employee=Employee{...}}
```

---

## Annotation-Based vs XML-Based: Side-by-Side

### XML-Based Autowiring

```xml
<!-- Configuration in separate XML file -->
<bean id="specification" class="...Specification">
    <property name="make" value="Toyota"/>
</bean>

<bean id="myCar" class="...Car" autowire="constructor">
    <!-- Spring auto-injects Specification -->
</bean>
```

**How it works:**
1. Read XML file
2. Parse `<bean>` declarations
3. Find `autowire="constructor"` attribute
4. Inject matching beans via constructor

### Annotation-Based @Autowired

```java
// Configuration in Java code
@Component
public class Specification {
    private String make = "Toyota";
}

@Component
public class Car {
    @Autowired
    private Specification specification;  // Spring auto-injects
}
```

**How it works:**
1. Scan classpath for @Component
2. Skip XML parsing entirely
3. Find @Autowired annotations
4. Inject matching beans

### Key Differences

| Aspect | XML | Annotation |
|--------|-----|-----------|
| **Configuration Location** | Separate XML files | In Java classes (@) |
| **Bean Declaration** | `<bean>` tags | `@Component` |
| **Autowiring** | `autowire="type"` | `@Autowired` |
| **Type Safety** | No (string-based) | Yes (Java types) |
| **Refactoring** | Manual | IDE-assisted |
| **Startup Speed** | Slower (XML parsing) | Faster (direct scanning) |
| **Learning Curve** | Steeper | Easier (annotations) |

---

## Common Patterns

### Pattern 1: Single Dependency

```java
@Component
public class Service {
    @Autowired
    private Repository repository;
}
```

### Pattern 2: Multiple Dependencies

```java
@Component
public class Service {
    @Autowired
    private Repository repository;
    
    @Autowired
    private Logger logger;
    
    @Autowired
    @Qualifier("emailSender")
    private Sender sender;
}
```

### Pattern 3: List of Beans (Same Type)

```java
@Component
public class Service {
    @Autowired
    private List<Repository> repositories;  // Get ALL Repository beans
}
```

### Pattern 4: Map of Beans

```java
@Component
public class Service {
    @Autowired
    private Map<String, Repository> repositoriesMap;
    // Keys = bean IDs, Values = bean instances
}
```

### Pattern 5: Optional Dependency

```java
@Component
public class Service {
    @Autowired(required = false)
    private OptionalService optional;
    
    public void doSomething() {
        if (optional != null) {
            optional.doIt();
        }
    }
}
```

---

## Understanding @Value Injection (Bonus)

The Employee class uses `@Value` to inject different types of data:

```java
@Component
public class Employee {
    // 1. Simple literal values
    @Value("1")
    private int employeeId;
    
    // 2. String literals
    @Value("Hello")
    private String firstName;
    
    // 3. System properties
    @Value("${java.home}")
    private String lastName;
    
    // 4. SpEL (Spring Expression Language)
    @Value("#{4*4}")  // Evaluates to 16
    private double salary;
}
```

**Output:**
```
employeeId: 1
firstName: Hello
lastName: C:\Program Files\Java\jdk-21
salary: 16.0
```

---

## Best Practices for Annotation-Based @Autowired

### ✅ DO:

1. **Use Constructor Injection (Recommended)**
   ```java
   @Autowired
   public Service(Repository repo) {
       this.repo = repo;
   }
   ```

2. **Use @Qualifier for multiple beans**
   ```java
   @Autowired
   @Qualifier("primaryDb")
   private DataSource dataSource;
   ```

3. **Make dependencies final (when constructor injection)**
   ```java
   private final Repository repo;
   ```

4. **Use @Component for simple beans**
   ```java
   @Component
   public class UserRepository { }
   ```

5. **Keep configuration classes simple**
   ```java
   @Configuration
   @ComponentScan(basePackages = "com.example")
   public class AppConfig { }
   ```

### ❌ DON'T:

1. **Don't use field injection in production**
   ```java
   @Autowired
   private Repository repo;  // ❌ Hard to test
   ```

2. **Don't mix injection types unnecessarily**
   ```java
   @Autowired
   private Repository repo1;  // Field injection
   
   @Autowired
   public Service(Repository repo2) { }  // Constructor injection
   // ❌ Confusing and redundant
   ```

3. **Don't create circular dependencies**
   ```java
   // ❌ Bad: A needs B, B needs A
   @Component
   public class ServiceA {
       @Autowired
       private ServiceB serviceB;
   }
   
   @Component
   public class ServiceB {
       @Autowired
       private ServiceA serviceA;
   }
   ```

4. **Don't forget to enable component scanning**
   ```java
   // ❌ Missing @ComponentScan
   @Configuration
   public class AppConfig { }
   
   // ✅ Correct
   @Configuration
   @ComponentScan(basePackages = "com.example")
   public class AppConfig { }
   ```

5. **Don't use complex expressions in @Value**
   ```java
   // ❌ Too complex for annotations
   @Value("#{complexCalculation()}")
   private int value;
   
   // ✅ Better: use method in @Bean
   @Bean
   public int complexValue() {
       return complexCalculation();
   }
   ```

---

## Troubleshooting Common Issues

### Issue 1: NoSuchBeanDefinitionException

**Problem:**
```
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No bean named 'employee' available
```

**Causes:**
- @Component not found (not scanned)
- @ComponentScan pointing to wrong package
- Bean name doesn't match @Qualifier

**Solutions:**
```java
// Ensure @ComponentScan covers the package
@ComponentScan(basePackages = "com.example.autowired.annotation")

// Verify @Component annotation exists
@Component("employee")
public class Employee { }

// Use correct @Qualifier name
@Qualifier("employee")  // Must match bean id
```

### Issue 2: Ambiguous Bean Definition

**Problem:**
```
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException:
No qualifying bean of type 'Employee' available
```

**Causes:**
- Multiple beans of same type exist
- No @Qualifier specified
- @Primary not set

**Solutions:**
```java
// Option 1: Use @Qualifier
@Autowired
@Qualifier("employee")
private Employee employee;

// Option 2: Use @Primary
@Component
@Primary
public class Employee { }

// Option 3: Make one bean the primary
@Bean
@Primary
public Employee employee() {
    return new Employee();
}
```

### Issue 3: Circular Dependency

**Problem:**
```
Caused by: org.springframework.beans.factory.BeanCreationException:
Requested bean is currently in creation: [circular reference]
```

**Causes:**
- Bean A depends on Bean B
- Bean B depends on Bean A

**Solution:**
Redesign to remove circular dependency (refactor classes)

---

## Learning Path

1. ✅ Understand IoC and beans
2. ✅ Learn @Component and @ComponentScan
3. ✅ **Learn @Autowired for dependency injection** ← You are here
4. 📚 Learn @Qualifier for multiple instances
5. 📚 Learn @Primary and bean selection
6. 📚 Learn @Configuration and @Bean methods
7. 📚 Learn Spring Boot (auto-configuration)
8. 📚 Learn AOP (Aspect Oriented Programming)

---

## Summary Table

| Concept | Purpose | Example |
|---------|---------|---------|
| `@Component` | Mark class as Spring bean | `@Component("employee")` |
| `@Autowired` | Inject dependency automatically | `@Autowired private Employee e;` |
| `@Qualifier` | Specify which bean to inject | `@Qualifier("employee")` |
| `@Configuration` | Mark class as configuration | `@Configuration` |
| `@ComponentScan` | Tell Spring where to find beans | `@ComponentScan("com.example")` |
| `@Value` | Inject property values | `@Value("${property}")` |
| `@Primary` | Default bean when multiple exist | `@Primary` |
| `@Autowired(required=false)` | Make dependency optional | Optional injection |

---

## Real-World Scenario

### Without @Autowired (Manual Management)

```java
public class Application {
    public static void main(String[] args) {
        // You must manually create everything
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setFirstName("Hello");
        
        Manager manager = new Manager();
        manager.setEmployee(employee);
        
        System.out.println(manager);
        
        // Problem: What if you need 50 classes? 100 dependencies?
    }
}
```

### With @Autowired (Spring Manages It)

```java
public class Application {
    public static void main(String[] args) {
        // Spring creates everything automatically
        ApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);
        
        Manager manager = context.getBean(Manager.class);
        System.out.println(manager);
        
        // Benefit: Spring handles 50 classes, 100 dependencies with one line!
    }
}
```

---

## Conclusion

**@Autowired with annotation-based configuration is:**

- ✅ **Modern** - Current Spring standard (not legacy XML)
- ✅ **Clean** - Minimal boilerplate code
- ✅ **Type-safe** - Full IDE and compiler support
- ✅ **Powerful** - Enables complex dependency graphs with simple annotations
- ✅ **Flexible** - Easy to test, refactor, and maintain

**Key Takeaway:** Instead of manually creating and wiring objects, let Spring do it automatically using `@Autowired`. Focus on business logic, not object creation!

