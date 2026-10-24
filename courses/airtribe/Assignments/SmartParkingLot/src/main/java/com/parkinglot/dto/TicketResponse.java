package com.parkinglot.dto;

import com.parkinglot.entity.ParkingTicket;
import com.parkinglot.entity.TicketStatus;
import com.parkinglot.entity.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketResponse {

    private Long ticketId;
    private String licensePlate;
    private VehicleType vehicleType;
    private String spotCode;
    private int floorNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal fee;
    private TicketStatus status;

    public static TicketResponse from(ParkingTicket ticket) {
        TicketResponse response = new TicketResponse();
        response.ticketId = ticket.getId();
        response.licensePlate = ticket.getLicensePlate();
        response.vehicleType = ticket.getVehicleType();
        response.spotCode = ticket.getSpotCode();
        response.floorNumber = ticket.getFloorNumber();
        response.entryTime = ticket.getEntryTime();
        response.exitTime = ticket.getExitTime();
        response.fee = ticket.getFee();
        response.status = ticket.getStatus();
        return response;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getSpotCode() {
        return spotCode;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public TicketStatus getStatus() {
        return status;
    }
}
