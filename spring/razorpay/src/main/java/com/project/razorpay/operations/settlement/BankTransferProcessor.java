package com.project.razorpay.operations.settlement;

import com.project.razorpay.common.entity.Money;
import com.project.razorpay.operations.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);

}
