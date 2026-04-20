package com.huodaizi.backend.dto.auth;

public record N13CreditScoreListItemDTO(
    String scoreId,
    String merchantId,
    String merchantName,
    String scoreVersion,
    String score,
    String grade,
    String rankPercent,
    String riskLevel,
    String updatedAt) {}
