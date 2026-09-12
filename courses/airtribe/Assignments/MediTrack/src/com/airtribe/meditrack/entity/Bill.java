package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.interfaces.Payable;

public class Bill extends MedicalEntity implements Payable {
    private final String patientId;
    private final double amount;
    private final String description;

    public Bill(String id, String patientId, double amount, String description) {
        super(id);
        this.patientId = patientId;
        this.amount = amount;
        this.description = description;
    }

    public String getPatientId() { return patientId; }
    public double getAmount() { return amount; }

    @Override 
    public String getDisplayName() { 
        return description + " for " + patientId; 
    }

    @Override 
    public double getTotalAmount() {
         return amount + (amount * Constants.TAX_RATE); 
        }

    @Override 
    public Bill generateBill(double amount) { 
        return this; 
    }
}
