package com.parkinglot.service;

import com.parkinglot.dto.AddSpotRequest;
import com.parkinglot.dto.AvailabilityResponse;
import com.parkinglot.dto.SpotResponse;
import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;
import com.parkinglot.entity.VehicleType;
import com.parkinglot.exception.InvalidRequestException;
import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.repository.ParkingSpotRepository;
import com.parkinglot.strategy.allocation.SpotAllocationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Everything about spots: allocate, release, add and availability.
 */
@Service
public class SpotService {

    private final ParkingSpotRepository spotRepository;
    private final SpotAllocationStrategy allocationStrategy;

    public SpotService(ParkingSpotRepository spotRepository, SpotAllocationStrategy allocationStrategy) {
        this.spotRepository = spotRepository;
        this.allocationStrategy = allocationStrategy;
    }

    @Transactional
    public ParkingSpot allocateSpot(VehicleType vehicleType) {
        // rows are locked until the outer transaction commits
        List<ParkingSpot> freeSpots = spotRepository.findFreeSpotsForUpdate(vehicleType.getAllowedSpotSizes());

        ParkingSpot spot = allocationStrategy.selectSpot(freeSpots, vehicleType)
                .orElseThrow(() -> new ParkingFullException("No free spot available for " + vehicleType));

        spot.occupy();
        return spotRepository.save(spot);
    }

    @Transactional
    public void releaseSpot(ParkingSpot spot) {
        spot.release();
        spotRepository.save(spot);
    }

    @Transactional
    public SpotResponse addSpot(AddSpotRequest request) {
        if (spotRepository.existsByFloorNumberAndSpotNumber(request.getFloorNumber(), request.getSpotNumber())) {
            throw new InvalidRequestException("Spot " + request.getSpotNumber()
                    + " already exists on floor " + request.getFloorNumber());
        }
        ParkingSpot spot = new ParkingSpot(request.getFloorNumber(), request.getSpotNumber(), request.getSize());
        return SpotResponse.from(spotRepository.save(spot));
    }

    @Transactional(readOnly = true)
    public List<SpotResponse> getAllSpots() {
        return spotRepository.findAll().stream()
                .map(SpotResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability() {
        List<ParkingSpot> spots = spotRepository.findAll();
        Map<Integer, Map<SpotSize, Long>> freeByFloor = new TreeMap<>();
        long freeCount = 0;

        for (ParkingSpot spot : spots) {
            Map<SpotSize, Long> floorCounts = freeByFloor.computeIfAbsent(spot.getFloorNumber(), floor -> emptyCounts());
            if (spot.isAvailable()) {
                floorCounts.merge(spot.getSize(), 1L, Long::sum);
                freeCount++;
            }
        }
        return new AvailabilityResponse(spots.size(), freeCount, freeByFloor);
    }

    private Map<SpotSize, Long> emptyCounts() {
        Map<SpotSize, Long> counts = new EnumMap<>(SpotSize.class);
        for (SpotSize size : SpotSize.values()) {
            counts.put(size, 0L);
        }
        return counts;
    }
}
