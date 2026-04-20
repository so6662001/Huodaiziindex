package com.huodaizi.backend.dto.riskalert;

public record A08RiskAlertOverviewDTO(
    int totalCount,
    int highCount,
    int mediumCount,
    int lowCount,
    int openCount,
    int processingCount,
    int resolvedCount,
    int overdueCount,
    String highRate,
    String resolvedRate,
    String avgAgingHours) {}
