package com.huodaizi.backend.dto.riskalert;

import java.util.List;

public record A08RiskAlertListResponse(
    A08RiskAlertOverviewDTO overview,
    List<A08RiskAlertBucketDTO> buckets,
    List<A08RiskAlertItemDTO> items,
    int total,
    int page,
    int pageSize) {}
