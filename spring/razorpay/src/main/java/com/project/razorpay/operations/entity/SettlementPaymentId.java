package com.project.razorpay.operations.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class SettlementPaymentId implements Serializable {

    @Column(columnDefinition = "uuid")
    private UUID settlementId;

    private UUID paymentId;
}
