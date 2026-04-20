package com.huodaizi.backend.dto.planpricing;

import java.util.List;

public record A07PlanPricingItemDTO(
    String planId,
    String planCode,
    String planName,
    String planType,
    String billingCycle,
    String price,
    String originalPrice,
    boolean recommended,
    boolean enabled,
    String suitableFor,
    int featureCount,
    int activeSubscriptionCount,
    int pendingBillCount,
    String totalRevenueYuan,
    String updatedAt,
    String updatedBy,
    List<A07PlanPricingFeatureDTO> features) {}
