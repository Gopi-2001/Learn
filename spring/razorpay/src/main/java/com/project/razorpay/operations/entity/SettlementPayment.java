package com.project.razorpay.operations.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "settlement_payment")
public class SettlementPayment {

    @EmbeddedId
    private  SettlementPaymentId id;

    @MapsId("settlementId") // Marks this relationship to share the Parent's ID
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private  Settlement settlement;
}
