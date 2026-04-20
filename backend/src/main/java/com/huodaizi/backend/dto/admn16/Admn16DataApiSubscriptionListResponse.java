package com.huodaizi.backend.dto.admn16;

import java.util.List;

public record Admn16DataApiSubscriptionListResponse(
    int total,
    int page,
    int pageSize,
    String subscriptionStatus,
    String apiProductCode,
    String billingCycle,
    String owner,
    String keyword,
    int activeCount,
    int suspendedCount,
    int expiredCount,
    int trialingCount,
    List<Admn16DataApiSubscriptionListItemDTO> records) {}
