package com.project.razorpay.payment.gateway;

import com.project.razorpay.payment.gateway.dto.PaymentGatewayRequest;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayResponse;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentGatewayResponse initiate(PaymentGatewayRequest request);

    PaymentGatewayResponse capture(UUID paymentId);
}
