package com.huodaizi.backend.dto.auth;

public record N13CreditScoreFactorDTO(
    String factorCode,
    String factorName,
    int score,
    int weight,
    String trend,
    String summary) {}
