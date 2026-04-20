package com.huodaizi.backend.dto.siteadlead;

import java.util.List;

public record SiteAdLeadMineResponse(
    String contactPhone,
    List<SiteAdLeadItemDTO> items,
    int page,
    int pageSize,
    int total,
    int submittedCount,
    int assignedCount,
    int contactedCount,
    int proposalSentCount,
    int convertedCount,
    int closedCount) {}
