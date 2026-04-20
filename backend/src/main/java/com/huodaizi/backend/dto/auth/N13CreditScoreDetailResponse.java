package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N13CreditScoreDetailResponse(
    String scoreId,
    String merchantId,
    String merchantName,
    String periodMonth,
    String totalScore,
    String grade,
    String rankPercent,
    String riskLevel,
    String riskTags,
    String modelVersion,
    String latestRemark,
    String updatedAt,
    List<N13CreditScoreFactorDTO> factors,
    List<N13CreditScoreTrendPointDTO> trend) {}
