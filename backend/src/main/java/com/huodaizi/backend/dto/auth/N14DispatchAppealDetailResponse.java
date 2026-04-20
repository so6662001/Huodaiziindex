package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N14DispatchAppealDetailResponse(
    String appealId,
    String merchantId,
    String merchantName,
    String sceneCode,
    String sceneName,
    String disputedDate,
    String disputedLeadId,
    String disputedRuleCode,
    String disputedScoreDelta,
    String reasonType,
    String reasonTypeText,
    String reasonDescription,
    String evidenceFiles,
    String status,
    String statusText,
    String latestRemark,
    List<String> processLogs,
    String createdAt,
    String updatedAt) {}
