package com.project.razorpay.merchant.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
