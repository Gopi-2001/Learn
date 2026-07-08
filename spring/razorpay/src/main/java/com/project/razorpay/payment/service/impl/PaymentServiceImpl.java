package com.project.razorpay.payment.service.impl;

import com.project.razorpay.common.enums.OrderStatus;
import com.project.razorpay.common.enums.PaymentEvent;
import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.common.exception.BusinessRuleValidationException;
import com.project.razorpay.common.exception.ResourceNotFoundExecption;
import com.project.razorpay.payment.dto.request.PaymentInitRequest;
import com.project.razorpay.payment.entity.OrderRecord;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.gateway.PaymentGatewayRouter;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayRequest;
import com.project.razorpay.payment.gateway.dto.PaymentGatewayResponse;
import com.project.razorpay.payment.mapper.PaymentMapper;
import com.project.razorpay.payment.repository.OrderRepository;
import com.project.razorpay.payment.repository.PaymentRepository;
import com.project.razorpay.payment.service.PaymentService;
import com.project.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    public com.project.razorpay.payment.dto.response.PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {

        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundExecption("Order", request.orderId()));

        // ~ (A || B)  === ~A && ~B
        if((orderRecord.getOrderStatus() != OrderStatus.CREATED) && (orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED)) {
                throw new BusinessRuleValidationException("ORDER_NOT_PAYABLE",
                        "Order Cannot accept payment in status: " + orderRecord.getOrderStatus());
        }

        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(orderRecord)
                .merchantId(merchantId)
                .amount(orderRecord.getAmount())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentGatewayRequest paymentGatewayRequest = new PaymentGatewayRequest(payment.getId(),
                request.orderId(),
                merchantId,
                orderRecord.getAmount(),
                request.method(),
                request.methodDetails());

        PaymentGatewayResponse paymentGatewayResponse = paymentGatewayRouter.initiate(paymentGatewayRequest);

        switch (paymentGatewayResponse) {
            case PaymentGatewayResponse.Pending pending -> payment.setProcessorReference(pending.registrationReference());
            case PaymentGatewayResponse.Failure failure -> {
                //payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentGatewayResponse.Success success-> {
               log.warn("Invalid State");
               return null;
            }
        }

        payment = paymentRepository.save(payment);

        orderRepository.save(orderRecord);

        // send an outbox (kafka event)


        return paymentMapper.toPaymentResponse(payment);

    }

    @Override
    public com.project.razorpay.payment.dto.response.PaymentResponse capture(UUID paymentId, UUID merchantId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundExecption("Payment", paymentId));

        // payment.setStatus(PaymentStatus.CAPTURING); // TODO statemachine

        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

        PaymentGatewayResponse paymentGatewayResponse = paymentGatewayRouter.capture(payment.getMethod(), paymentId);


        if(paymentGatewayResponse instanceof  PaymentGatewayResponse.Success success) {

            // payment.setStatus(PaymentStatus.CAPTURED);

            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());

            log.info("Payment captured successfully, PaymentID: {} " , paymentId);

        } else if(paymentGatewayResponse instanceof PaymentGatewayResponse.Failure failure) {

            // payment.setStatus(PaymentStatus.AUTHORIZED);

            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAILURE);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());

            log.warn("Payment capture failed, PaymentID: {}" , paymentId);
        }

        payment = paymentRepository.save(payment);

        // send an outbox (kafka event)


        return paymentMapper.toPaymentResponse(payment);
    }
}
