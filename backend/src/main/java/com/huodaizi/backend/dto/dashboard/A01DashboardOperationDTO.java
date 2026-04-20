package com.huodaizi.backend.dto.dashboard;

public record A01DashboardOperationDTO(
    int adLeadSubmittedCount,
    int adLeadAssignedCount,
    int memberExpiringSoonCount,
    int memberExpiredCount,
    int reconcileDisputedCount,
    int pickupInTransitCount,
    int pickupCancelledCount,
    String tipText) {}
