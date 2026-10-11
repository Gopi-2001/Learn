package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Bill;

public final class BillFactory {
    private BillFactory() { }
    public static Bill create(String billId, String patientId, double amount, String type) {
        return new Bill(billId, patientId, amount, type);
    }
}
