package com.project.razorpay.payment.api;

import com.project.razorpay.payment.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentLookupService {

    List<Payment> findUnsettledCapturePayments(UUID merchantId);

}
