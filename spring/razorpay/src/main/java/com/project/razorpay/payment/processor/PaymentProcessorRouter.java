package com.project.razorpay.payment.processor;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;


    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor processor = paymentProcessorMap.get(request.method());

        if(processor == null) {
            throw new IllegalArgumentException("No payment processor registered for method " + request.method());
        }

        return processor.charge(request);
    }
}
