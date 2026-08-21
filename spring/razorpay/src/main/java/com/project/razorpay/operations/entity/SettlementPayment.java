package com.project.razorpay.operations.entity;

import com.project.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "settlement_payment")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private  SettlementPaymentId id;

    @MapsId("settlementId") // Marks this relationship to share the Parent's ID
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private  Settlement settlement;
}
