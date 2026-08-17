package com.project.razorpay.operations.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.project.razorpay.common.dto.WebhookTarget;
import com.project.razorpay.common.enums.WebhookEventStatus;
import com.project.razorpay.common.util.SignerUtil;
import com.project.razorpay.merchant.api.MerchantWebhookApi;
import com.project.razorpay.operations.entity.WebhookEvent;
import com.project.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantWebhookApi merchantWebhookApi;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue retryQueue;
    private final WebhookDlqRecorder dlqRecorder;

    @KafkaListener(topics = {
        "${app.kafka.topics.payments:payments.events}",
        "${app.kafka.topics.orders:orders.events}",
        "${app.kafak.topics.refunds:refunds.events}",
        "${app.kafka.topics.settlements:settlements.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String, Object>> record, Acknowledgment ack) throws JsonProcessingException {
        try {
            Map<String, Object> envelope = record.value();

            Map<String, Object> data = (Map<String, Object>) envelope.get("data");

            String eventType = envelope.get("eventType").toString();

            Object rawMerchantId = data.get("merchantId");

            if (rawMerchantId == null) {
                log.warn("No merchantId was found, skipping the event: {}", eventType);
                ack.acknowledge();
                return;
            }

            UUID merchantId = UUID.fromString(rawMerchantId.toString());

            List<WebhookTarget> targets = merchantWebhookApi.getActiveConfigsForEvent(merchantId, eventType);

            if (targets.isEmpty()) {
                log.debug("No webhook target was found, skipping the event: {}", eventType);
                ack.acknowledge();
                return;
            }

            Map<String, Object> signatureData = Map.of("event", eventType, "payload", data);

            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for (WebhookTarget target : targets) {
                String signature = signerUtil.sign(signatureJson, target.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .payload(data)
                        .targetUrl(target.targetUrl())
                        .signature(signature)
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();

                webhookEvent = webhookEventRepository.save(webhookEvent);

                // redisQueue.enqueue(webhookEvent.getId())
                retryQueue.enqueue(UUID.fromString(webhookEvent.getId()), webhookEvent.getNextRetryAt());
                log.info("Created a webhook event with id: {}", webhookEvent.getId());
            }

            ack.acknowledge();
        } catch (DataAccessException | CannotCreateTransactionException dbDown) {
            log.error("Webhook consumer failed due to DB down, Could not process the record, offset: {}", record.offset(), dbDown);
        } catch (Exception logicError) {
            log.error("Webhook consumer failed due to logical error, Could not process the record, offset: {}", record.offset(), logicError);
            dlqRecorder.recordConsumerFailed(record, logicError.getMessage());
            ack.acknowledge();
        }


    }
}
