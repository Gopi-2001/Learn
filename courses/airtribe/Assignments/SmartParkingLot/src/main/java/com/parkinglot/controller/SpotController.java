package com.parkinglot.controller;

import com.parkinglot.dto.AddSpotRequest;
import com.parkinglot.dto.AvailabilityResponse;
import com.parkinglot.dto.SpotResponse;
import com.parkinglot.service.SpotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public List<SpotResponse> getAllSpots() {
        return spotService.getAllSpots();
    }

    @GetMapping("/availability")
    public AvailabilityResponse getAvailability() {
        return spotService.getAvailability();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpotResponse addSpot(@Valid @RequestBody AddSpotRequest request) {
        return spotService.addSpot(request);
    }
}
