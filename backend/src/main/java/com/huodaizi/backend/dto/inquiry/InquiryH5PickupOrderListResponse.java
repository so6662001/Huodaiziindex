package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5PickupOrderListResponse(
    String contactMobileMasked,
    String selectedStatus,
    String selectedKeyword,
    List<InquiryPickupOrderItemDTO> items,
    int total,
    int page,
    int pageSize,
    int createdCount,
    int confirmedCount,
    int inTransitCount,
    int signedCount,
    int completedCount,
    int cancelledCount,
    String tipText) {}
