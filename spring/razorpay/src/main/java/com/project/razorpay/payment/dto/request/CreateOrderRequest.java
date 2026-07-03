package com.project.razorpay.payment.dto.request;

import com.project.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record CreateOrderRequest(

        @NotNull(message =  "Amount is required")
        Money amount,

        @Size(max = 100)
        String receipt, // OrderId (known to merchant)

        Map<String, Object> notes,

        LocalDateTime expiresAt

) {
}
