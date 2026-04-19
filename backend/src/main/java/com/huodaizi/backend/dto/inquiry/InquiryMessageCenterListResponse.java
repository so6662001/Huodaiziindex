package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryMessageCenterListResponse(
    String merchantId,
    List<InquiryMessageCenterItemDTO> items,
    int total,
    int page,
    int pageSize,
    int unreadCount,
    int readCount,
    int systemCount,
    int transactionCount,
    int riskCount) {}
