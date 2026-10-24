package com.parkinglot.service;

import com.parkinglot.entity.Vehicle;
import com.parkinglot.entity.VehicleType;
import com.parkinglot.exception.InvalidRequestException;
import com.parkinglot.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // returns the saved vehicle, or registers it the first time it comes in
    @Transactional
    public Vehicle findOrRegister(String licensePlate, VehicleType vehicleType) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(licensePlate, vehicleType)));

        if (vehicle.getVehicleType() != vehicleType) {
            throw new InvalidRequestException("Vehicle " + licensePlate + " is registered as "
                    + vehicle.getVehicleType() + ", not " + vehicleType);
        }
        return vehicle;
    }
}
