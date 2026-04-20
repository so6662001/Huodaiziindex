package com.huodaizi.backend.dto.admn13;

import java.util.List;

public record Admn13CreditModelVersionListResponse(
    int total,
    int page,
    int pageSize,
    String modelStatus,
    String applicableRole,
    String keyword,
    int activeCount,
    int draftCount,
    int archivedCount,
    List<Admn13CreditModelVersionListItemDTO> records) {}
