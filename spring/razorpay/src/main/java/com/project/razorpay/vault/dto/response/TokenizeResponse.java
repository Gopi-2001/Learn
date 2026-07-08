package com.project.razorpay.vault.dto.response;

import com.project.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String pan,
        String cvv,
        Integer expiryMonth,
        Integer expiryYear,
        CardBrand cardBrand

) {
}
