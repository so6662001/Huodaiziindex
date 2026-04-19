package com.huodaizi.backend.dto.reconcilemonitor;

import java.util.List;

public record A05ReconcileMonitorListResponse(
    A05ReconcileMonitorOverviewDTO overview,
    List<A05ReconcileMonitorRiskBucketDTO> riskBuckets,
    List<A05ReconcileMonitorItemDTO> items,
    int total,
    int page,
    int pageSize) {}
