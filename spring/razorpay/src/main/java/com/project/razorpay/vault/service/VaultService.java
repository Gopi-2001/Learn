package com.project.razorpay.vault.service;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;
import com.project.razorpay.vault.dto.request.TokenizeRequest;
import com.project.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest tokenizeRequest,UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> stringObjectMap);
}
