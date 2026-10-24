package com.parkinglot.strategy.fee;

import com.parkinglot.entity.VehicleType;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Calculates the parking fee. New pricing (weekend, flat rate, etc.) = new implementation.
 *
 * Contract: always return a fee (never null, never negative) for any vehicle type and duration.
 */
public interface FeeStrategy {

    BigDecimal calculateFee(VehicleType vehicleType, Duration parkedDuration);
}
