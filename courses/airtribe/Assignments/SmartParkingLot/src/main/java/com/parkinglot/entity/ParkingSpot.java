package com.parkinglot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "parking_spots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"floor_number", "spot_number"}))
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @Column(name = "spot_number", nullable = false)
    private int spotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotSize size;

    @Column(nullable = false)
    private boolean available = true;

    // optimistic lock version, extra safety on top of the row lock
    @Version
    private Long version;

    protected ParkingSpot() {
        // for JPA
    }

    public ParkingSpot(int floorNumber, int spotNumber, SpotSize size) {
        this.floorNumber = floorNumber;
        this.spotNumber = spotNumber;
        this.size = size;
    }

    public void occupy() {
        if (!available) {
            throw new IllegalStateException("Spot " + getSpotCode() + " is already occupied");
        }
        this.available = false;
    }

    public void release() {
        this.available = true;
    }

    public String getSpotCode() {
        return "F" + floorNumber + "-S" + spotNumber;
    }

    public Long getId() {
        return id;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public SpotSize getSize() {
        return size;
    }

    public boolean isAvailable() {
        return available;
    }
}
