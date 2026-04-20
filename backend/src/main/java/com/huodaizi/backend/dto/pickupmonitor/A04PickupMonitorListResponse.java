package com.huodaizi.backend.dto.pickupmonitor;

import java.util.List;

public record A04PickupMonitorListResponse(
    A04PickupMonitorOverviewDTO overview,
    List<A04PickupMonitorRiskBucketDTO> riskBuckets,
    List<A04PickupMonitorItemDTO> items,
    int total,
    int page,
    int pageSize) {}
