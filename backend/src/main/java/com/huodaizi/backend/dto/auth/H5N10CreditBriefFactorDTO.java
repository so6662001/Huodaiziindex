package com.huodaizi.backend.dto.auth;

public record H5N10CreditBriefFactorDTO(
    String factorCode,
    String factorName,
    int score,
    int weight,
    String trend,
    String summary) {}
