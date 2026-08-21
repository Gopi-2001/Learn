package com.project.razorpay.operations.entity;

import com.project.razorpay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;
}
