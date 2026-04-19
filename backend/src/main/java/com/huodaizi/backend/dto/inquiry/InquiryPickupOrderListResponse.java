package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryPickupOrderListResponse(
    List<InquiryPickupOrderItemDTO> items,
    int total,
    int page,
    int pageSize,
    int createdCount,
    int confirmedCount,
    int inTransitCount,
    int signedCount,
    int completedCount,
    int cancelledCount) {}
