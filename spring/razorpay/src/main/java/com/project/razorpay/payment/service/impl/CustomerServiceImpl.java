package com.project.razorpay.payment.service.impl;

import com.project.razorpay.common.exception.ResourceNotFoundExecption;
import com.project.razorpay.merchant.entity.Customer;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.payment.repository.CustomerRepository;
import com.project.razorpay.payment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {

        if(email == null || email.isBlank()) {
            return null;
        }

        return customerRepository.findByMerchantIdAndEmail(merchantId,email)
                .map(Customer::getId)
                .orElseGet(() -> createNew(merchantId, email,name,phone));

    }

    private UUID createNew(UUID merchantId, String email, String name, String phone) {
        Merchant merchant= merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundExecption("Merchant",merchantId));

        Customer customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .phone(phone)
                .build();

        customer = customerRepository.save(customer);

        log.info("Customer created via findOrCreate id={}, merchantId={}, email={}",customer.getId(),merchant.getId(),email);

        return customer.getId();
    }

}
