package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;

public class Patient extends Person implements Searchable {
    private Doctor assignedDoctor;

    public Patient(String id, String name, int age, String phone) throws InvalidDataException {
        super(id, name, age, phone);
    }

    public Doctor getAssignedDoctor() { 
        return assignedDoctor; 
    }

    public void setAssignedDoctor(Doctor assignedDoctor) { 
        this.assignedDoctor = assignedDoctor; 
    }

    @Override 
    public boolean matches(String keyword) { 
        return getName().toLowerCase().contains(keyword.toLowerCase()); 
    }

    @Override 
    public Bill generateBill(double amount) { 
        return new Bill("B-" + getId(), getId(), amount, "Patient treatment"); 
    }

    @Override
    public Patient clone() throws CloneNotSupportedException {
        Patient copy = (Patient) super.clone();
        copy.assignedDoctor = assignedDoctor == null ? null : assignedDoctor.clone();
        return copy;
    }
}
