package com.huodaizi.backend.dto.admn16;

public record Admn16DataApiSubscriptionListItemDTO(
    String subscriptionId,
    String subscriptionCode,
    String merchantId,
    String merchantName,
    String apiProductCode,
    String apiProductName,
    String planCode,
    String planName,
    String billingCycle,
    String billingCycleText,
    String subscriptionStatus,
    String subscriptionStatusText,
    String quotaTotal,
    String quotaUsed,
    String usageRate,
    String expireAt,
    String updatedAt) {}
