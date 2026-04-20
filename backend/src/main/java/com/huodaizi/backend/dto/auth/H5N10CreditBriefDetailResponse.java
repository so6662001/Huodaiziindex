package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N10CreditBriefDetailResponse(
    String scoreId,
    String merchantId,
    String merchantName,
    String periodMonth,
    String totalScore,
    String grade,
    String rankPercent,
    String riskLevel,
    String riskLevelText,
    String riskTags,
    String modelVersion,
    String latestRemark,
    String channel,
    List<String> availableActions,
    List<String> suggestions,
    List<H5N10CreditBriefFactorDTO> factors,
    List<H5N10CreditBriefTrendPointDTO> trend,
    String tipText,
    String updatedAt) {}
