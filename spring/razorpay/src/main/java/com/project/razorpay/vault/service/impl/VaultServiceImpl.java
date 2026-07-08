package com.project.razorpay.vault.service.impl;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.common.enums.CardBrand;
import com.project.razorpay.common.exception.ResourceNotFoundExecption;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessorRouter;
import com.project.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.response.PaymentProcessorResponse;
import com.project.razorpay.vault.config.VaultEncryptionConfig;
import com.project.razorpay.vault.dto.request.TokenizeRequest;
import com.project.razorpay.vault.dto.response.TokenizeResponse;
import com.project.razorpay.vault.entity.CardToken;
import com.project.razorpay.vault.entity.VaultCard;
import com.project.razorpay.vault.repository.CardTokenRepository;
import com.project.razorpay.vault.repository.VaultCardRepository;
import com.project.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest tokenizeRequest, UUID merchantId) {

        String lastFour = tokenizeRequest.pan().substring(tokenizeRequest.pan().length() - 4);
        String bin = tokenizeRequest.pan().substring(0,6);
        CardBrand cardBrand = detectCardBrand(tokenizeRequest.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();

        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(tokenizeRequest.pan().getBytes(StandardCharsets.UTF_8));

        byte[] encryptedDek = dekEncryptor.encrypt(encryptedPan);

        VaultCard vaultCard = vaultCardRepository.save(VaultCard.builder()
                        .brand(cardBrand)
                        .expiryYear(tokenizeRequest.expiryYear().toString())
                        .expiryMonth(tokenizeRequest.expiryMonth().toString())
                        .bin(bin)
                        .lastFour(lastFour)
                        .encryptedDek(encryptedDek)
                        .cardHolderName(tokenizeRequest.cardHolderName())
                        .build());

        String token = "tok_" + RandomizerUtil.randomBase64(32);

        cardTokenRepository.save(CardToken.builder()
                        .vaultCard(vaultCard)
                        .token(token)
                        .customer(tokenizeRequest.customerId())
                        .merchant(merchantId)
                .build());

        return new TokenizeResponse(token, lastFour,tokenizeRequest.expiryMonth(),tokenizeRequest.expiryYear(),cardBrand);
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {

        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
            .orElseThrow(() -> new ResourceNotFoundExecption("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();

        byte[] panBytes = null;

        try {

            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());

            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(
                paymentId,
                pan,
                expiry,
                amount,
                methodDetails
        );

        PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

        log.info("Valut charge registered, token={}***", token.substring(0, 4));

        return paymentProcessorResponse;
        } catch (Exception e) {

            log.warn("Vault charge failed, token={}***", token.substring(0, 4));

            return  new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if(panBytes != null) Arrays.fill(panBytes, (byte) 0);
        }

    }



    private CardBrand detectCardBrand(String pan) {

        if(pan.startsWith("4")) return CardBrand.VISA;
        if(pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;

        return CardBrand.RUPAY;

    }
}
