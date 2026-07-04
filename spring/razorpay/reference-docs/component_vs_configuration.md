In Spring Boot, @Component and @Configuration are both stereotype annotations used to register beans in the Spring Container, but they serve completely different purposes. 
The primary difference is that @Component is used for automatic, class-level bean scanning, while @Configuration is used for manual, method-level bean definition. 
------------------------------

## Key Differences at a Glance

| Feature | @Component | @Configuration |
|---|---|---|
| Primary Purpose | Defines a single, automatic Spring Bean. | Houses multiple @Bean methods. |
| Bean Creation | Class-level (Implicit creation). | Method-level (Explicit definition). |
| Proxy Mode | No proxying (proxyBeanMethods = false). | Proxied via CGLIB (proxyBeanMethods = true). |
| Inter-Bean Calls | Calling methods creates a new object instance. | Calling methods returns the cached singleton bean. |
| Best For | Domain logic, services, and repositories. | Third-party libraries and infrastructure setup. |

------------------------------

## Detailed Comparison## 1. @Component (Scanning & Automation)

When you mark a class with @Component, you tell Spring, "Hey, look at this class, instantiate it, and manage it as a bean." It relies on classpath scanning. 

@Componentpublic class PaymentGateway {
public void process() {
System.out.println("Processing payment...");
}
}


* How it works: Spring detects this class automatically during @ComponentScan and creates a singleton instance.
* Specialized Types: @Service, @Repository, and @Controller are just specialized variants of @Component.

## 2. @Configuration (Wiring & Third-Party Code)

When you mark a class with @Configuration, you tell Spring, "This class is a factory for other beans." Inside this class, you write methods annotated with @Bean. 
```
@Configurationpublic class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Manual instantiation
    }
}
```

* How it works: Spring executes the restTemplate() method and places the returned RestTemplate object into the container.
* Why use it: This is perfect for configuring third-party classes (like RestTemplate or ObjectMapper) where you cannot modify the source code to add a @Component annotation.

------------------------------
## The "Lite" vs "Full" Mode Behavior (Crucial Concept)
The most important technical difference is how they handle internal method calls.
## Under @Configuration (Full Mode) 
Spring wraps @Configuration classes inside a CGLIB proxy. If one @Bean method calls another @Bean method, the proxy intercepts the call to ensure the same singleton instance is returned. 

```
@Configurationpublic class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain filterChain() {
        // Calling userDetailsService() directly here DOES NOT create a new object.
        // It safely retrieves the existing singleton bean from the Spring container.
        return new SecurityFilterChain(userDetailsService()); 
    }
}
```

## Under @Component (Lite Mode)
If you define @Bean methods inside a regular @Component class, Spring runs in "Lite" mode. No CGLIB proxy is created.
If methodA() calls methodB(), it bypasses the Spring Container entirely and acts like a plain Java method call, creating a brand new object instance and breaking the Singleton pattern.
------------------------------

# References

1. https://medium.com/@niteeshboddapu/component-vs-configuration-in-spring-79250a1e9349
2. https://www.linkedin.com/posts/marioromeu_springboot-java-softwareengineering-activity-7363319050216583169-guwf/
