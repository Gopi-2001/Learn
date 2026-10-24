package com.parkinglot.strategy.fee;

import com.parkinglot.entity.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

/**
 * Fee = hourly rate of vehicle type * number of hours.
 * Every started hour is charged and the minimum charge is 1 hour.
 */
@Component
public class HourlyFeeStrategy implements FeeStrategy {

    private static final Map<VehicleType, BigDecimal> HOURLY_RATES = Map.of(
            VehicleType.MOTORCYCLE, new BigDecimal("10"),
            VehicleType.CAR, new BigDecimal("20"),
            VehicleType.BUS, new BigDecimal("50")
    );

    @Override
    public BigDecimal calculateFee(VehicleType vehicleType, Duration parkedDuration) {
        long minutes = parkedDuration.toMinutes();
        long hours = (minutes + 59) / 60; // round up to the next hour
        if (hours < 1) {
            hours = 1;
        }
        return HOURLY_RATES.get(vehicleType).multiply(BigDecimal.valueOf(hours));
    }
}
