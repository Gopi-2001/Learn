package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;

public class Doctor extends Person {
    private final Specialization specialization;

    public Doctor(String id, String name, int age, String phone, Specialization specialization) throws InvalidDataException {
        super(id, name, age, phone);
        this.specialization = specialization;
    }

    public Specialization getSpecialization() { 
        return specialization; 
    }

    @Override 
    public Bill generateBill(double amount) { 
        return new Bill("B-" + getId(), getId(), amount, "Consultation"); 
    }

    @Override 
    public Doctor clone() throws CloneNotSupportedException { 
        return (Doctor) super.clone(); 
    }
}
