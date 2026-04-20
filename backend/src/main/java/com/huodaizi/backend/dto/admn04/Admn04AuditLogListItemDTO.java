package com.huodaizi.backend.dto.admn04;

public record Admn04AuditLogListItemDTO(
    String logId,
    String moduleCode,
    String moduleName,
    String actionCode,
    String actionName,
    String targetType,
    String targetId,
    String operator,
    String operatorIp,
    String result,
    String resultText,
    String detailSummary,
    String happenedAt) {}
