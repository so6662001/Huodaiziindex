package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5ReconcileOrderListResponse(
    String contactMobileMasked,
    String selectedStatus,
    String selectedKeyword,
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
    int disputedCount,
    String tipText) {}
