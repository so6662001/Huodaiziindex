package com.huodaizi.backend.dto.admn06;

import java.util.List;

public record Admn06LeadQualityListResponse(
    int total,
    int page,
    int pageSize,
    String source,
    String qualityStatus,
    String riskLevel,
    String reviewer,
    String keyword,
    int passCount,
    int rejectCount,
    int pendingCount,
    List<Admn06LeadQualityListItemDTO> records) {}
