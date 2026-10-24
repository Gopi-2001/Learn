package com.parkinglot.strategy.allocation;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Decides which free spot a vehicle should get.
 * New rules (nearest to lift, EV spots first, etc.) = new implementation, no change in services.
 *
 * Contract: return only a spot from the given list that is free and fits the vehicle,
 * or Optional.empty() if there is none. Never modify the spots.
 */
public interface SpotAllocationStrategy {

    Optional<ParkingSpot> selectSpot(List<ParkingSpot> freeSpots, VehicleType vehicleType);
}
