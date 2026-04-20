package com.huodaizi.backend.dto.admn08;

public record Admn08DealFunnelNodeDTO(
    String stageCode,
    String stageName,
    int stageCount,
    String amountYuan,
    String conversionRate,
    String dropRate) {}
