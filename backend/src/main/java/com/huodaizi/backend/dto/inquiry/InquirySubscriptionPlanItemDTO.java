package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquirySubscriptionPlanItemDTO(
    String planId,
    String planCode,
    String planName,
    String planDesc,
    String billingCycle,
    String originPrice,
    String discountPrice,
    boolean recommended,
    String suitableFor,
    List<InquirySubscriptionPlanFeatureDTO> features) {}
