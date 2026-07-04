package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
