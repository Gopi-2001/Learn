package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;
import com.parkinglot.entity.VehicleType;
import com.parkinglot.strategy.allocation.BestFitSpotAllocationStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BestFitSpotAllocationStrategyTest {

    private final BestFitSpotAllocationStrategy strategy = new BestFitSpotAllocationStrategy();

    @Test
    void motorcyclePrefersSmallSpotEvenOnHigherFloor() {
        ParkingSpot medium = new ParkingSpot(1, 1, SpotSize.MEDIUM);
        ParkingSpot small = new ParkingSpot(2, 1, SpotSize.SMALL);

        Optional<ParkingSpot> result = strategy.selectSpot(List.of(medium, small), VehicleType.MOTORCYCLE);

        assertEquals(small, result.get());
    }

    @Test
    void carNeverGetsSmallSpot() {
        ParkingSpot small = new ParkingSpot(1, 1, SpotSize.SMALL);

        assertTrue(strategy.selectSpot(List.of(small), VehicleType.CAR).isEmpty());
    }

    @Test
    void sameSizePicksLowestFloorThenLowestNumber() {
        ParkingSpot floor2 = new ParkingSpot(2, 1, SpotSize.LARGE);
        ParkingSpot floor1Spot5 = new ParkingSpot(1, 5, SpotSize.LARGE);
        ParkingSpot floor1Spot3 = new ParkingSpot(1, 3, SpotSize.LARGE);

        Optional<ParkingSpot> result = strategy.selectSpot(List.of(floor2, floor1Spot5, floor1Spot3), VehicleType.BUS);

        assertEquals(floor1Spot3, result.get());
    }

    @Test
    void skipsOccupiedSpots() {
        ParkingSpot taken = new ParkingSpot(1, 1, SpotSize.MEDIUM);
        taken.occupy();
        ParkingSpot free = new ParkingSpot(1, 2, SpotSize.MEDIUM);

        assertEquals(free, strategy.selectSpot(List.of(taken, free), VehicleType.CAR).get());
    }
}
