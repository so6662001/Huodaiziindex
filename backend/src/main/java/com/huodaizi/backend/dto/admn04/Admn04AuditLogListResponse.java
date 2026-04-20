package com.huodaizi.backend.dto.admn04;

import java.util.List;

public record Admn04AuditLogListResponse(
    int total,
    int page,
    int pageSize,
    String keyword,
    String moduleCode,
    String actionCode,
    String resultStatus,
    int successCount,
    int failedCount,
    List<Admn04AuditLogListItemDTO> records) {}
