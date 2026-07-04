package com.project.razorpay.payment.processor;

import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}
