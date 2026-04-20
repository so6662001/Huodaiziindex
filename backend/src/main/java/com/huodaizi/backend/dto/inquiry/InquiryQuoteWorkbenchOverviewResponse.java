package com.huodaizi.backend.dto.inquiry;

public record InquiryQuoteWorkbenchOverviewResponse(
    String merchantId,
    int totalLeads,
    int newCount,
    int contactedCount,
    int quotedCount,
    int wonCount,
    int lostCount,
    int closedCount,
    int pendingQuoteCount,
    String avgResponseMinutes,
    String quoteRate) {}
