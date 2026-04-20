package com.huodaizi.backend.dto.admn06;

import java.util.List;

public record Admn06LeadQualityDetailResponse(
    String qualityId,
    String source,
    String sourceText,
    String leadId,
    String leadNo,
    String companyName,
    String city,
    String owner,
    String qualityStatus,
    String qualityStatusText,
    String riskLevel,
    String riskLevelText,
    String qualityScore,
    String issueCountText,
    String latestIssueTag,
    String latestIssueDetail,
    String reviewer,
    String reviewComment,
    String reviewedAt,
    String createdAt,
    String updatedAt,
    List<String> availableActions) {}
