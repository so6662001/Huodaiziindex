package com.huodaizi.backend.dto.inquiry;

public record InquiryH5MemberOverviewResponse(
    String merchantId,
    String memberLevel,
    String memberLevelText,
    String currentPlanCode,
    String currentPlanName,
    String currentStatus,
    String currentStatusText,
    int activeCount,
    int expiringSoonCount,
    int expiredCount,
    String renewSuggestion,
    String tipText) {}
