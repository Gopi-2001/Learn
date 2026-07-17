package com.project.razorpay.merchant.controller;

import com.project.razorpay.merchant.dto.request.ApiKeyCreateRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.project.razorpay.merchant.dto.response.ApiKeyResponse;
import com.project.razorpay.merchant.security.MerchantContext;
import com.project.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody ApiKeyCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantContext.getMerchantId(),request));

    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant(){
        return ResponseEntity.ok(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/keyId")
    public ResponseEntity<Void> deleteByMerchant(@PathVariable UUID keyId){

        apiKeyService.revoke(merchantContext.getMerchantId(),keyId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable UUID keyId){
        return  ResponseEntity.ok(apiKeyService.rotateKey(merchantContext.getMerchantId(),keyId));
    }


}
