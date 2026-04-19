package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryMerchantLeadListResponse(
    List<InquiryMerchantLeadItemDTO> items,
    int total,
    int page,
    int pageSize,
    int pendingQuoteCount,
    int quotedCount,
    int wonCount,
    int lostCount,
    int closedCount) {}
