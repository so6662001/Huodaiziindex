package com.huodaizi.backend.dto.quoteefficiency;

public record A03QuoteEfficiencyOverviewDTO(
    int totalLeads,
    int waitingQuoteCount,
    int timeoutCount,
    int quotedCount,
    int wonCount,
    int lostCount,
    String quoteRate,
    String winRate,
    String avgResponseMinutes) {}
