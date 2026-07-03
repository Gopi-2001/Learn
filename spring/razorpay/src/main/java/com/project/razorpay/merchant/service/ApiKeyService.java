package com.project.razorpay.merchant.service;

import com.project.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;

import java.util.UUID;

public interface ApiKeyService{
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);
}
