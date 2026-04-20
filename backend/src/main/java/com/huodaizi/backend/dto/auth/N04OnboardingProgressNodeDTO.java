package com.huodaizi.backend.dto.auth;

public record N04OnboardingProgressNodeDTO(
    String nodeCode,
    String nodeName,
    String nodeDescription,
    String status,
    String statusText,
    String owner,
    String startAt,
    String finishAt,
    String remark) {}
