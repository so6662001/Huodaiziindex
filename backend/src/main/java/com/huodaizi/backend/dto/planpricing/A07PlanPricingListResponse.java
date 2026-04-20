package com.huodaizi.backend.dto.planpricing;

import java.util.List;

public record A07PlanPricingListResponse(
    List<A07PlanPricingItemDTO> items,
    int total,
    int page,
    int pageSize,
    int enabledCount,
    int disabledCount,
    int recommendedCount,
    String avgDiscountRate,
    String totalMonthlyAmount,
    String updatedAt) {}
