package com.project.razorpay.operations.repository;

import com.project.razorpay.operations.entity.SettlementPayment;
import com.project.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {

}
