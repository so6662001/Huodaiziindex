package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryReconcileOrderListResponse(
    List<InquiryReconcileOrderItemDTO> items,
    int total,
    int page,
    int pageSize,
    int createdCount,
    int invoicePendingCount,
    int invoicedCount,
    int confirmedCount,
    int partialPaidCount,
    int paidCount,
    int closedCount,
    int disputedCount) {}
