package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

public class Appointment extends MedicalEntity implements Cloneable {
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private AppointmentStatus status;

    public Appointment(String id, Patient patient, Doctor doctor, LocalDateTime dateTime) {
        super(id);
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.status = AppointmentStatus.CONFIRMED;
    }

    public Patient getPatient() { 
        return patient; 
    }
    public Doctor getDoctor() { 
        return doctor; 
    }
    public LocalDateTime getDateTime() { 
        return dateTime; 
    }
    public AppointmentStatus getStatus() { 
        return status; 
    }
    public void cancel() { 
        status = AppointmentStatus.CANCELLED; 
    }
    @Override 
    public String getDisplayName() { 
        return getId() + ": " + patient.getName() + " with " + doctor.getName(); 
    }
    @Override 
    public Bill generateBill(double amount) { 
        return patient.generateBill(amount); 
    }

    @Override
    public Appointment clone() throws CloneNotSupportedException {
        Appointment copy = (Appointment) super.clone();
        copy.patient = patient.clone();
        copy.doctor = doctor.clone();
        return copy;
    }
}
