package com.huodaizi.backend.dto.admn08;

public record Admn08DealFunnelTrendPointDTO(
    String date,
    int leadCreatedCount,
    int quotedCount,
    int wonCount,
    int dealCount,
    int paidCount) {}
