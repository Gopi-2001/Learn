package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.exception.ResourceNotFoundExecption;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.merchant.dto.request.ApiKeyCreateRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.project.razorpay.merchant.dto.response.ApiKeyResponse;
import com.project.razorpay.merchant.entity.ApiKey;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.mapper.ApiKeyMapper;
import com.project.razorpay.merchant.repository.ApiKeyRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.service.ApiKeyService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, ApiKeyCreateRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundExecption("merchant", merchantId));

        String keyId = "rzp_" + request.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);


        String rawSecret = RandomizerUtil.randomBase64(40); // TODO: replace with cryptographic random hex

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(bCryptPasswordEncoder.encode(rawSecret))
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

       return new ApiKeyCreateResponse(apiKey.getId(),keyId,rawSecret,request.environment().name().toString());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {

        List<ApiKey> apiKeys = apiKeyRepository.findByMerchantId(merchantId);

        return apiKeyMapper.toListResponse(apiKeys);

    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundExecption("ApiKey", keyId));

        key.setEnabled(false);

        // apiKeyRepository.save(key);


        // because we are using @Transactional it will check for any data which became dirty
        // then it will automatically save, therefore no need to use apiKeyRepository.save(key)
        // explicitly.

    }

    @Override
    @Transactional
    public @Nullable ApiKeyCreateResponse rotateKey(UUID merchantId, UUID keyId) {

            ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundExecption("ApiKey", keyId));

            if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disabled ApiKey");

            String newRawSecret = RandomizerUtil.randomBase64(40);

            apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
            apiKey.setKeySecretHash(newRawSecret);  // TODO: encode with BcryptPassWordEncoder

            apiKey.setRotatedAt(LocalDateTime.now());
            apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));

            apiKey = apiKeyRepository.save(apiKey);

            return new ApiKeyCreateResponse(apiKey.getId(),
                    apiKey.getKeyId(),
                    newRawSecret,
                    apiKey.getEnvironment().toString());
    }


}
