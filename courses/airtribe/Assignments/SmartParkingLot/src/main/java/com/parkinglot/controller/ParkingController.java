package com.parkinglot.controller;

import com.parkinglot.dto.CheckInRequest;
import com.parkinglot.dto.CheckOutRequest;
import com.parkinglot.dto.TicketResponse;
import com.parkinglot.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return parkingService.checkIn(request);
    }

    @PostMapping("/check-out")
    public TicketResponse checkOut(@Valid @RequestBody CheckOutRequest request) {
        return parkingService.checkOut(request);
    }
}
