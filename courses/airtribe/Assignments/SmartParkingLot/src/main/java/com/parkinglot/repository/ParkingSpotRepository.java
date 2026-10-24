package com.parkinglot.repository;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    // SELECT ... FOR UPDATE -> other check-ins wait until this transaction commits,
    // so two vehicles can never get the same spot
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSpot s WHERE s.available = true AND s.size IN :sizes")
    List<ParkingSpot> findFreeSpotsForUpdate(@Param("sizes") List<SpotSize> sizes);

    boolean existsByFloorNumberAndSpotNumber(int floorNumber, int spotNumber);
}
