package com.project.razorpay.payment.repository;

import com.project.razorpay.merchant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByMerchantIdAndEmail(UUID merchantId, String email);
}
