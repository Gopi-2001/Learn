package com.parkinglot.strategy;

import com.parkinglot.entity.VehicleType;
import com.parkinglot.strategy.fee.HourlyFeeStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HourlyFeeStrategyTest {

    private final HourlyFeeStrategy feeStrategy = new HourlyFeeStrategy();

    @Test
    void chargesMinimumOneHour() {
        assertEquals(new BigDecimal("20"), feeStrategy.calculateFee(VehicleType.CAR, Duration.ofMinutes(10)));
        assertEquals(new BigDecimal("50"), feeStrategy.calculateFee(VehicleType.BUS, Duration.ZERO));
    }

    @Test
    void exactHourIsNotRoundedUp() {
        assertEquals(new BigDecimal("20"), feeStrategy.calculateFee(VehicleType.CAR, Duration.ofMinutes(60)));
    }

    @Test
    void startedHourIsCharged() {
        assertEquals(new BigDecimal("40"), feeStrategy.calculateFee(VehicleType.CAR, Duration.ofMinutes(61)));
    }

    @Test
    void rateDependsOnVehicleType() {
        Duration threeHours = Duration.ofHours(3);
        assertEquals(new BigDecimal("30"), feeStrategy.calculateFee(VehicleType.MOTORCYCLE, threeHours));
        assertEquals(new BigDecimal("60"), feeStrategy.calculateFee(VehicleType.CAR, threeHours));
        assertEquals(new BigDecimal("150"), feeStrategy.calculateFee(VehicleType.BUS, threeHours));
    }
}
