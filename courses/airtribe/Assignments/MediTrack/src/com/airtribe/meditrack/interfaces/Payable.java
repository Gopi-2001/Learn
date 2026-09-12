package com.airtribe.meditrack.interfaces;

public interface Payable {
    double getTotalAmount();

    default boolean isPaid(double paidAmount) {
        return paidAmount >= getTotalAmount();
    }
}
