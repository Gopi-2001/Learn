package com.airtribe.meditrack.entity;

public final class BillSummary {
    private final String billId;
    private final double totalAmount;

    public BillSummary(String billId, double totalAmount) {
        this.billId = billId;
        this.totalAmount = totalAmount;
    }

    public String getBillId() { 
        return billId; 
    }

    public double getTotalAmount() { 
        return totalAmount; 
    }
}
