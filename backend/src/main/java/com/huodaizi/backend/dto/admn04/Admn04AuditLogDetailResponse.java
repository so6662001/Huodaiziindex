package com.huodaizi.backend.dto.admn04;

import java.util.List;

public record Admn04AuditLogDetailResponse(
    String auditId,
    String moduleCode,
    String moduleName,
    String actionCode,
    String actionName,
    String targetType,
    String targetId,
    String operator,
    String operatorRole,
    String requestMethod,
    String requestPath,
    String requestIp,
    String requestTraceId,
    String resultCode,
    String resultText,
    String changedFieldsText,
    String beforeJson,
    String afterJson,
    List<String> tags,
    String createdAt) {}
