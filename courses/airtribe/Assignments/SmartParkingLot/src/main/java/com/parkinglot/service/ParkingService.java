package com.parkinglot.service;

import com.parkinglot.dto.CheckInRequest;
import com.parkinglot.dto.CheckOutRequest;
import com.parkinglot.dto.TicketResponse;
import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.ParkingTicket;
import com.parkinglot.entity.TicketStatus;
import com.parkinglot.entity.Vehicle;
import com.parkinglot.exception.InvalidRequestException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.repository.ParkingTicketRepository;
import com.parkinglot.strategy.fee.FeeStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Handles the check-in / check-out flow. Spot and fee rules are delegated to collaborators.
 */
@Service
public class ParkingService {

    private final ParkingTicketRepository ticketRepository;
    private final VehicleService vehicleService;
    private final SpotService spotService;
    private final FeeStrategy feeStrategy;
    private final Clock clock;

    public ParkingService(ParkingTicketRepository ticketRepository,
                          VehicleService vehicleService,
                          SpotService spotService,
                          FeeStrategy feeStrategy,
                          Clock clock) {
        this.ticketRepository = ticketRepository;
        this.vehicleService = vehicleService;
        this.spotService = spotService;
        this.feeStrategy = feeStrategy;
        this.clock = clock;
    }

    @Transactional
    public TicketResponse checkIn(CheckInRequest request) {
        String plate = Vehicle.normalizePlate(request.getLicensePlate());

        if (ticketRepository.existsByVehicleLicensePlateAndStatus(plate, TicketStatus.ACTIVE)) {
            throw new InvalidRequestException("Vehicle " + plate + " is already parked");
        }

        Vehicle vehicle = vehicleService.findOrRegister(plate, request.getVehicleType());
        ParkingSpot spot = spotService.allocateSpot(vehicle.getVehicleType());

        ParkingTicket ticket = new ParkingTicket(vehicle, spot, LocalDateTime.now(clock));
        ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    @Transactional
    public TicketResponse checkOut(CheckOutRequest request) {
        String plate = Vehicle.normalizePlate(request.getLicensePlate());

        ParkingTicket ticket = ticketRepository.findWithDetails(plate, TicketStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active parking found for vehicle " + plate));

        LocalDateTime exitTime = LocalDateTime.now(clock);
        Duration parkedFor = Duration.between(ticket.getEntryTime(), exitTime);
        BigDecimal fee = feeStrategy.calculateFee(ticket.getVehicleType(), parkedFor);

        ticket.close(exitTime, fee);
        spotService.releaseSpot(ticket.getSpot());
        return TicketResponse.from(ticket);
    }
}
