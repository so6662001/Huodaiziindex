package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquirySubscriptionPlanListResponse(
    String merchantId,
    String category,
    List<InquirySubscriptionPlanItemDTO> plans,
    String recommendPlanCode,
    String currency) {}
