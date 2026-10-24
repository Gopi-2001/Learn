package com.parkinglot.service;

import com.parkinglot.dto.CheckInRequest;
import com.parkinglot.dto.CheckOutRequest;
import com.parkinglot.dto.TicketResponse;
import com.parkinglot.entity.SpotSize;
import com.parkinglot.entity.TicketStatus;
import com.parkinglot.entity.VehicleType;
import com.parkinglot.exception.InvalidRequestException;
import com.parkinglot.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ParkingServiceTest {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private SpotService spotService;

    @Test
    void carCheckInAndCheckOut() {
        long freeBefore = spotService.getAvailability().getFreeSpots();

        TicketResponse ticket = parkingService.checkIn(new CheckInRequest("ka01ab1234", VehicleType.CAR));
        assertEquals("KA01AB1234", ticket.getLicensePlate());
        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        assertEquals(freeBefore - 1, spotService.getAvailability().getFreeSpots());

        TicketResponse closed = parkingService.checkOut(new CheckOutRequest("KA01AB1234"));
        assertEquals(TicketStatus.COMPLETED, closed.getStatus());
        assertNotNull(closed.getExitTime());
        assertEquals(new BigDecimal("20"), closed.getFee()); // under 1 hour -> minimum 1 hour for a car
        assertEquals(freeBefore, spotService.getAvailability().getFreeSpots());
    }

    @Test
    void motorcycleGetsSmallSpotOnFirstFloor() {
        long smallFreeBefore = spotService.getAvailability().getFreeSpotsByFloor().get(1).get(SpotSize.SMALL);

        TicketResponse ticket = parkingService.checkIn(new CheckInRequest("BIKE-1", VehicleType.MOTORCYCLE));

        assertEquals(1, ticket.getFloorNumber());
        assertEquals(smallFreeBefore - 1,
                (long) spotService.getAvailability().getFreeSpotsByFloor().get(1).get(SpotSize.SMALL));
        parkingService.checkOut(new CheckOutRequest("BIKE-1"));
    }

    @Test
    void sameVehicleCannotCheckInTwice() {
        parkingService.checkIn(new CheckInRequest("DUP-1", VehicleType.CAR));

        assertThrows(InvalidRequestException.class,
                () -> parkingService.checkIn(new CheckInRequest("DUP-1", VehicleType.CAR)));
        parkingService.checkOut(new CheckOutRequest("DUP-1"));
    }

    @Test
    void checkOutWithoutCheckInFails() {
        assertThrows(ResourceNotFoundException.class,
                () -> parkingService.checkOut(new CheckOutRequest("NOT-HERE")));
    }
}
