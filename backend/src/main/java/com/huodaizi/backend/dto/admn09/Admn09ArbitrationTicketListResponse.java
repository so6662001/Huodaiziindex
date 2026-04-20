package com.huodaizi.backend.dto.admn09;

import java.util.List;

public record Admn09ArbitrationTicketListResponse(
    int total,
    int page,
    int pageSize,
    String arbitrationStatus,
    String priorityLevel,
    String city,
    String assignedArbitrator,
    String keyword,
    int pendingCount,
    int processingCount,
    int resolvedCount,
    int closedCount,
    List<Admn09ArbitrationTicketListItemDTO> records) {}
