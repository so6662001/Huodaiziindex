package com.huodaizi.backend.dto.quoteefficiency;

import java.util.List;

public record A03QuoteEfficiencyListResponse(
    A03QuoteEfficiencyOverviewDTO overview,
    List<A03QuoteEfficiencyAgingBucketDTO> agingBuckets,
    List<A03QuoteEfficiencyTaskItemDTO> items,
    int total,
    int page,
    int pageSize) {}
