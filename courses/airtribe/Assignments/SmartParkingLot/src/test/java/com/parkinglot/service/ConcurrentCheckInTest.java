package com.parkinglot.service;

import com.parkinglot.dto.CheckInRequest;
import com.parkinglot.dto.TicketResponse;
import com.parkinglot.entity.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ConcurrentCheckInTest {

    private static final int LARGE_SPOTS_IN_LOT = 6; // 3 floors x 2 large spots (see DataLoader)

    @Autowired
    private ParkingService parkingService;

    @Test
    void busesArrivingTogetherNeverShareASpot() throws Exception {
        int buses = 10;
        ExecutorService pool = Executors.newFixedThreadPool(buses);
        CountDownLatch startTogether = new CountDownLatch(1);
        List<Future<TicketResponse>> results = new ArrayList<>();

        for (int i = 0; i < buses; i++) {
            String plate = "BUS-" + i;
            results.add(pool.submit(() -> {
                startTogether.await();
                return parkingService.checkIn(new CheckInRequest(plate, VehicleType.BUS));
            }));
        }
        startTogether.countDown();

        Set<String> givenSpots = new HashSet<>();
        int parked = 0;
        for (Future<TicketResponse> result : results) {
            try {
                TicketResponse ticket = result.get(30, TimeUnit.SECONDS);
                parked++;
                assertTrue(givenSpots.add(ticket.getSpotCode()), "Spot given twice: " + ticket.getSpotCode());
            } catch (ExecutionException e) {
                // lot full (or lock clash) -> this bus is rejected, which is fine
            }
        }
        pool.shutdown();

        assertTrue(parked > 0);
        assertTrue(parked <= LARGE_SPOTS_IN_LOT, "More buses parked than large spots: " + parked);
    }
}
