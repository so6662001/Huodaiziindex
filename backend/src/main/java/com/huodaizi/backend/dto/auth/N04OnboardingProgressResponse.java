package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N04OnboardingProgressResponse(
    String userId,
    String account,
    String companyName,
    String certificationId,
    String status,
    String statusText,
    int progressPercent,
    String currentStepCode,
    String currentStepName,
    String expectedFinishAt,
    List<N04OnboardingProgressNodeDTO> nodes,
    String submittedAt,
    String updatedAt,
    String remark) {}
