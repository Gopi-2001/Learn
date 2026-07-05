package com.project.razorpay.payment.gateway;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayRequest;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component // So that spring boot will handle its object
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod,PaymentAdapter> paymentAdapters;

    public PaymentGatewayResponse initiate(PaymentGatewayRequest request){

        PaymentAdapter adapter = paymentAdapters.get(request.method());

        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter found for method: " + request.method());
        }

        return adapter.initiate(request);
    }

    public PaymentGatewayResponse capture(PaymentMethod method, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapters.get(method);

        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter registered for method: " + method);
        }

        return adapter.capture(paymentId);

    }
}
