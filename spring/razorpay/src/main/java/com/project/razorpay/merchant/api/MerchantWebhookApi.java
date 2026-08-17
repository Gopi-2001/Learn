package com.project.razorpay.merchant.api;

import com.project.razorpay.common.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {


    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);
}
