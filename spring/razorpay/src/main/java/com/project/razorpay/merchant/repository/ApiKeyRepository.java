package com.project.razorpay.merchant.repository;

import com.project.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchantId(UUID merchantId);

    ApiKey findByIdAndMerchantId(UUID keyId, UUID merchantId);

    Optional<ApiKey> findByKeyId(String keyId);
}
