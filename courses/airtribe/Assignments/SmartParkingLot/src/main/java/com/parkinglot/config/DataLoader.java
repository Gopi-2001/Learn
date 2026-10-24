package com.parkinglot.config;

import com.parkinglot.entity.ParkingSpot;
import com.parkinglot.entity.SpotSize;
import com.parkinglot.repository.ParkingSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a sample lot on startup: 3 floors, each with 5 small, 10 medium and 2 large spots.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final int FLOORS = 3;
    private static final int SMALL_SPOTS = 5;
    private static final int MEDIUM_SPOTS = 10;
    private static final int LARGE_SPOTS = 2;

    private final ParkingSpotRepository spotRepository;

    public DataLoader(ParkingSpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @Override
    public void run(String... args) {
        if (spotRepository.count() > 0) {
            return;
        }

        List<ParkingSpot> spots = new ArrayList<>();
        for (int floor = 1; floor <= FLOORS; floor++) {
            int spotNumber = 1;
            for (int i = 0; i < SMALL_SPOTS; i++) {
                spots.add(new ParkingSpot(floor, spotNumber++, SpotSize.SMALL));
            }
            for (int i = 0; i < MEDIUM_SPOTS; i++) {
                spots.add(new ParkingSpot(floor, spotNumber++, SpotSize.MEDIUM));
            }
            for (int i = 0; i < LARGE_SPOTS; i++) {
                spots.add(new ParkingSpot(floor, spotNumber++, SpotSize.LARGE));
            }
        }
        spotRepository.saveAll(spots);
    }
}
