package com.huodaizi.backend.dto.riskalert;

public record A08RiskAlertItemDTO(
    String alertId,
    String sourceType,
    String sourceText,
    String bizId,
    String bizNo,
    String merchantId,
    String merchantName,
    String city,
    String severity,
    String severityText,
    String riskCode,
    String riskTitle,
    String riskDetail,
    String riskScore,
    String owner,
    String status,
    String statusText,
    long agingHours,
    String suggestedAction,
    String createdAt,
    String updatedAt) {}
