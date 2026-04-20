package com.huodaizi.backend.dto.leadops;

import java.util.List;

public record A02LeadOpsListResponse(
    String source,
    A02LeadOpsOverviewDTO overview,
    List<A02LeadOpsItemDTO> items,
    int total,
    int page,
    int pageSize) {}
