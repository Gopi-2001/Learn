package com.project.razorpay.payment.gateway.dto;

public sealed interface PaymentGatewayResponse permits
PaymentGatewayResponse.Pending,
PaymentGatewayResponse.Failure,
PaymentGatewayResponse.Success {

    record Pending(String registrationReference) implements PaymentGatewayResponse {}

    record Failure(String errorCode, String errorDescription) implements PaymentGatewayResponse {}

    record Success(String bankReference) implements PaymentGatewayResponse {}
}
