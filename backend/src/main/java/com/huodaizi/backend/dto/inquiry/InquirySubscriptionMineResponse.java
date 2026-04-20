package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquirySubscriptionMineResponse(
    String merchantId,
    List<InquirySubscriptionMineItemDTO> items,
    int total,
    int page,
    int pageSize,
    int activeCount,
    int expiringSoonCount,
    int expiredCount) {}
