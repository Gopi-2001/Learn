package com.project.razorpay.payment.gateway;

import com.project.razorpay.payment.gateway.dto.PaymentRequest;

public interface PaymentAdapter {

    void initiate(PaymentRequest request);
}
