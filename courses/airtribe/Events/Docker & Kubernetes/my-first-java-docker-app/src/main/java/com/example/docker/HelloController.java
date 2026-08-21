package com.example.docker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// @RestController = @Controller + @ResponseBody
// Every method return value is serialized to JSON automatically
@RestController
public class HelloController {

    // GET /  →  {"message": "Hello from Docker! ...", "status": "running"}
    @GetMapping("/")
    public Map<String, String> hello() {
        return Map.of(
            "message", "Hello from Docker! 🐳  (Java / Spring Boot Edition)",
            "status",  "running"
        );
    }

    // Explicit /health — Spring Boot Actuator also exposes /actuator/health
    // This simpler endpoint is what the Dockerfile HEALTHCHECK uses
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
