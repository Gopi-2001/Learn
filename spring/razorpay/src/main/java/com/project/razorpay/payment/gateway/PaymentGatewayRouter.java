package com.project.razorpay.payment.gateway;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component // So that spring boot will handle its object
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod,PaymentAdapter> paymentAdapters;

    public void initiate(PaymentRequest request){

        PaymentAdapter adapter = paymentAdapters.get(request.method());

        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter found for method: " + request.method());
        }

        adapter.initiate(request);
    }
}
