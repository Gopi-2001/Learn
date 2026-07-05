package com.project.razorpay.payment.gateway.adapter;

import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayRequest;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayResponse;
import com.project.razorpay.payment.processor.PaymentProcessorRouter;
import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentGatewayResponse initiate(PaymentGatewayRequest request) {
        log.info("Initiating Payment with CARD, paymentId: {}", request.paymentId());

        try {

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(
                    request.paymentId(),
                    "pan",
                    "expiryMonth",
                    request.amount(),
                    request.methodDetails());

            PaymentProcessorResponse paymentProcessorResponse =
                    paymentProcessorRouter.charge(paymentProcessorRequest);


            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentGatewayResponse.Failure(failure.errorCode(), failure.errorDescription());

                case PaymentProcessorResponse.Pending pending ->
                        new PaymentGatewayResponse.Pending(pending.processorReference());

                case PaymentProcessorResponse.Success success -> new PaymentGatewayResponse.Success(success.bankReference());
            };
        } catch (Exception e) {

            log.error("CARD Failed, PaymentId : {} ", request.paymentId());

            return new PaymentGatewayResponse.Failure("CARD_FAILED",e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse capture(UUID paymentId) {
        return new PaymentGatewayResponse.Success("CARD_REF");
    }
}
