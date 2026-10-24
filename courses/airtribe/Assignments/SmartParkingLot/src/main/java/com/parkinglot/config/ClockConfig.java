package com.parkinglot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    // injected instead of calling LocalDateTime.now() directly, so tests can control time
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
