package com.project.razorpay.payment.config;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.project.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
// Indicates that a class declares one or more @Bean methods and may be processed by the Spring container
// to generate bean definitions and service requests for those beans at runtime
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
          PaymentMethod.CARD, new CardPaymentAdapter(),
          PaymentMethod.NETBANKING, new NetBankingAdapter(),
          PaymentMethod.UPI, new UpiPaymentAdapter()
        );
    }
}
