package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5MemberPlanListResponse(
    String merchantId, List<InquirySubscriptionPlanItemDTO> plans, String recommendPlanCode, String tipText) {}
