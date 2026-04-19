package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryQuoteWorkbenchTaskListResponse(
    List<InquiryQuoteWorkbenchTaskItemDTO> items,
    int total,
    int page,
    int pageSize,
    int waitingQuoteCount,
    int quotedCount,
    int followUpCount,
    int wonCount,
    int lostCount,
    int closedCount) {}
