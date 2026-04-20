package com.huodaizi.backend.dto.admn06;

public record Admn06LeadQualityListItemDTO(
    String qualityId,
    String source,
    String sourceText,
    String leadId,
    String leadNo,
    String companyName,
    String city,
    String qualityStatus,
    String qualityStatusText,
    String riskLevel,
    String riskLevelText,
    int qualityScore,
    String latestIssue,
    String reviewer,
    String reviewedAt,
    String updatedAt) {}
