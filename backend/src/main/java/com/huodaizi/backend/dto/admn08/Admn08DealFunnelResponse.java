package com.huodaizi.backend.dto.admn08;

import java.util.List;

public record Admn08DealFunnelResponse(
    String generatedAt,
    int windowDays,
    String city,
    int inquiryCount,
    int quotedCount,
    int wonCount,
    int dealCount,
    int pickupCompletedCount,
    int paidCount,
    String inquiryToQuoteRate,
    String quoteToWinRate,
    String wonToDealRate,
    String dealToPickupRate,
    String pickupToPaidRate,
    String paidAmountYuan,
    String avgDealDays,
    String avgPickupDays,
    List<Admn08DealFunnelNodeDTO> nodes,
    List<Admn08DealFunnelTrendPointDTO> trends,
    List<String> insights) {}
