package com.huodaizi.backend.dto.pickupmonitor;

public record A04PickupMonitorRiskBucketDTO(
    int todayPendingConfirmCount,
    int over1DayUnconfirmedCount,
    int over3DayInTransitCount,
    int cancelledCount) {}
