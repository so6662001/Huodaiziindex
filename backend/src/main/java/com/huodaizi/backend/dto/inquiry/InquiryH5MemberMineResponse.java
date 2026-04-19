package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5MemberMineResponse(
    String merchantId,
    List<InquirySubscriptionMineItemDTO> items,
    int total,
    int activeCount,
    int expiringSoonCount,
    int expiredCount,
    String tipText) {}
