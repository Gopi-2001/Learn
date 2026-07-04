package com.project.razorpay.payment.processor.dto.response;


// Sealed Interface -> Only permitted class can implement it
public sealed interface PaymentProcessorResponse permits
    PaymentProcessorResponse.Pending,
    PaymentProcessorResponse.Success,
    PaymentProcessorResponse.Failure  {

    // Child Classes using record - for creating POJOs (Plain old Java Object)
    record Pending(String processorReference) implements PaymentProcessorResponse {}

    record Success(String processorReference, String bankReference) implements PaymentProcessorResponse {}

    record Failure(String errorCode,String errorDescription) implements PaymentProcessorResponse {}

}
