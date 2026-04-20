package com.huodaizi.backend.dto.admn13;

import java.util.List;

public record Admn13CreditModelVersionDetailResponse(
    String versionId,
    String versionCode,
    String versionName,
    String modelStatus,
    String modelStatusText,
    String releaseType,
    String releaseTypeText,
    String effectiveFrom,
    String effectiveTo,
    String riskBandConfig,
    String baselineSampleSize,
    String minDataDays,
    String grayTrafficPercent,
    String owner,
    String reviewer,
    String latestRemark,
    String createdAt,
    String updatedAt,
    List<Admn13CreditModelFactorWeightDTO> factorWeights,
    List<String> availableActions) {}
