package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.enums.MerchantStatus;
import com.project.razorpay.common.enums.UserRole;
import com.project.razorpay.common.exception.DuplicateResourceException;
import com.project.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.project.razorpay.merchant.dto.response.MerchantResponse;
import com.project.razorpay.merchant.entity.AppUser;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.mapper.MerchantMapper;
import com.project.razorpay.merchant.repository.AppUserRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;

    private final MerchantMapper merchantMapper;

    @Override
    public MerchantResponse signup(MerchantSignupRequest request) {

        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL",
                    "Merchant with email already exists: " + request.email());
        }


        Merchant merchant = merchantMapper.toEntityFromMerchantSignUpRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchant =  merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .passwordHash(request.password()) // TODO: encrypt using Bcrypt
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return merchantMapper.toMerchantResponseFromMerchant(merchant);
    }
}
