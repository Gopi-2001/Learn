package com.project.razorpay.payment.entity;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // no FK - as this is cross - service boundary
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    /*
     * - @Column(columnDefinition = "jsonb"): Explicitly forces PostgreSQL to use the binary
     *   JSON format (JSONB) instead of standard text/json. This enables indexing (GIN)
     *   and highly efficient JSON-path queries.
     * - @JdbcTypeCode(SqlTypes.JSON): Jackson/Hibernate binding mechanism. Automatically
     *   handles serialization (Map -> JSON String) on write and deserialization
     *   (JSON String -> Map) on read.
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> notes;

    @Column(name = "expires_at" , nullable = false)
    private LocalDateTime expiresAt;

}
