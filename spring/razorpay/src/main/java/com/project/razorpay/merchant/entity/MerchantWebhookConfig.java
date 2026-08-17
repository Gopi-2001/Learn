package com.project.razorpay.merchant.entity;

import com.project.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config", indexes = {
        @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantWebhookConfig  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 500)
    private String targetUrl;

    @Column(length = 255)
    private String webhookSecret;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(length = 255)
    private String eventTypes;

    // Comma - Separated list of event types to subscribe to

    public boolean isSubscribedTo(String eventType) {
        if(eventTypes == null || eventTypes.isEmpty()) {
            return true;
        }

        for(String type : eventTypes.split(",")) {
            String trimmed = type.trim();

            if(trimmed.equalsIgnoreCase("ALL") ||
                    trimmed.equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }
}
