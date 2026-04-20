package com.huodaizi.backend.dto.admn15;

import java.util.List;

public record Admn15RiskAlertTicketListResponse(
    int total,
    int page,
    int pageSize,
    String ticketStatus,
    String riskLevel,
    String sourceType,
    String owner,
    String keyword,
    int pendingCount,
    int processingCount,
    int escalatedCount,
    int resolvedCount,
    int closedCount,
    List<Admn15RiskAlertTicketListItemDTO> records) {}
