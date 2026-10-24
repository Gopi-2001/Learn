package com.parkinglot.repository;

import com.parkinglot.entity.ParkingTicket;
import com.parkinglot.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {

    boolean existsByVehicleLicensePlateAndStatus(String licensePlate, TicketStatus status);

    // fetch vehicle and spot in one go because we need both on check-out
    @Query("SELECT t FROM ParkingTicket t JOIN FETCH t.vehicle v JOIN FETCH t.spot "
            + "WHERE v.licensePlate = :plate AND t.status = :status")
    Optional<ParkingTicket> findWithDetails(@Param("plate") String licensePlate,
                                            @Param("status") TicketStatus status);
}
