package com.huodaizi.backend.dto.auth;

public record H5N10CreditBriefListItemDTO(
    String scoreId,
    String merchantId,
    String merchantName,
    String periodMonth,
    String totalScore,
    String grade,
    String rankPercent,
    String riskLevel,
    String riskLevelText,
    String quickActionText,
    String updatedAt) {}
