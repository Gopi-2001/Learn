package com.project.razorpay.payment.gateway.adapter;

import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayRequest;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayResponse;
import com.project.razorpay.payment.processor.PaymentProcessorRouter;
import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;
import com.project.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentGatewayResponse initiate(PaymentGatewayRequest request) {

        String token = (String) request.methodDetails().get("token");

        PaymentProcessorResponse paymentProcessorResponse = vaultService.charge(
                request.paymentId(),token, request.amount(),request.methodDetails()
        );

        return switch (paymentProcessorResponse){
            case PaymentProcessorResponse.Success success -> new PaymentGatewayResponse.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure -> new PaymentGatewayResponse.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentGatewayResponse.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentGatewayResponse capture(UUID paymentId) {
        return new PaymentGatewayResponse.Success("CARD_REF");
    }
}
