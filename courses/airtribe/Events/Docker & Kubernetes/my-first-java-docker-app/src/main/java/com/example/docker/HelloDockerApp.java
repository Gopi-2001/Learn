package com.example.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Entry point — Spring Boot scans this package and all sub-packages for components
@SpringBootApplication
public class HelloDockerApp {
    public static void main(String[] args) {
        SpringApplication.run(HelloDockerApp.class, args);
    }
}
