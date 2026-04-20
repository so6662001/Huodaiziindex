package com.huodaizi.backend.dto.auth;

public record N14DispatchAppealListItemDTO(
    String appealId,
    String merchantId,
    String merchantName,
    String sceneCode,
    String sceneName,
    String issueType,
    String issueTypeText,
    String status,
    String statusText,
    String latestRemark,
    String createdAt,
    String updatedAt) {}
