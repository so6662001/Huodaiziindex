package com.huodaizi.backend.dto.dashboard;

public record A01DashboardFunnelDTO(
    int inquiryCount,
    int quotedLeadCount,
    int wonLeadCount,
    int dealDoneInquiryCount,
    int pickupCompletedCount,
    int reconcilePaidCount,
    String inquiryToQuoteRate,
    String quoteToWinRate,
    String dealConversionRate,
    String dealToFulfillmentRate,
    String paidRate) {}
