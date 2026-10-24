package com.parkinglot.strategy.allocation;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;
import com.parkinglot.entity.VehicleType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Picks the smallest spot the vehicle fits in, then the lowest floor, then the lowest spot number.
 * Example: a motorcycle takes a SMALL spot first, so MEDIUM spots stay free for cars.
 */
@Component
public class BestFitSpotAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<ParkingSpot> selectSpot(List<ParkingSpot> freeSpots, VehicleType vehicleType) {
        List<SpotSize> preferredSizes = vehicleType.getAllowedSpotSizes();

        Comparator<ParkingSpot> bestFirst = Comparator
                .comparingInt((ParkingSpot spot) -> preferredSizes.indexOf(spot.getSize()))
                .thenComparingInt(ParkingSpot::getFloorNumber)
                .thenComparingInt(ParkingSpot::getSpotNumber);

        return freeSpots.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(spot -> vehicleType.canFitIn(spot.getSize()))
                .min(bestFirst);
    }
}
