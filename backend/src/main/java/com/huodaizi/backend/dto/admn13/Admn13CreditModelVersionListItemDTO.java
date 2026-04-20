package com.huodaizi.backend.dto.admn13;

public record Admn13CreditModelVersionListItemDTO(
    String modelId,
    String versionCode,
    String versionName,
    String versionStatus,
    String versionStatusText,
    String releaseType,
    String releaseTypeText,
    String scoreRange,
    int sampleSize,
    String validFrom,
    String validTo,
    String updatedAt) {}
