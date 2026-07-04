package com.project.razorpay.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.razorpay.common.entity.Money;
import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) //Remove all the field that are null and send only fields which are not null as response
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod method,
        Map<String,Object> methodDetails,
        String errorCode,
        String errorDescription,
        LocalDateTime capturedAt,
        LocalDateTime createdAt
) {
}
