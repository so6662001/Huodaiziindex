package com.huodaizi.backend.dto.pickupmonitor;

public record A04PickupMonitorOverviewDTO(
    int totalOrders,
    int createdCount,
    int confirmedCount,
    int inTransitCount,
    int signedCount,
    int completedCount,
    int cancelledCount,
    int riskCount,
    String completionRate,
    String inTransitRate,
    String cancelledRate,
    String avgAgingHours) {}
