package com.huodaizi.backend.dto.riskalert;

public record A08RiskAlertBucketDTO(
    int leadTimeoutCount,
    int pickupOverdueCount,
    int reconcileOverdueCount,
    int billingOverdueCount,
    int highSeverityCount,
    int mediumSeverityCount,
    int unhandledCount) {}
