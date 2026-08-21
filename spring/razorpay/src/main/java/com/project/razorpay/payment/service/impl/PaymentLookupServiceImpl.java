package com.project.razorpay.payment.service.impl;

import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.payment.api.PaymentLookupService;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLookupServiceImpl implements PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findUnsettledCapturePayments(UUID merchantId) {
        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
    }
}
