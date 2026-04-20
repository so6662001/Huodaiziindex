package com.huodaizi.backend.dto.admn11;

import java.util.List;

public record Admn11PaymentRefundListResponse(
    int total,
    int page,
    int pageSize,
    String refundStatus,
    String refundReasonCode,
    String payChannel,
    String keyword,
    int pendingCount,
    int approvedCount,
    int rejectedCount,
    int refundedCount,
    List<Admn11PaymentRefundListItemDTO> records) {}
