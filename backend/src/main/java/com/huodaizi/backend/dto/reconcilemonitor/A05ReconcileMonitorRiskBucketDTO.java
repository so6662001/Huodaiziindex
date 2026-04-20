package com.huodaizi.backend.dto.reconcilemonitor;

public record A05ReconcileMonitorRiskBucketDTO(
    int overdueUnpaidCount,
    int nearDueUnpaidCount,
    int disputedCount,
    int highOutstandingCount) {}
