package com.huodaizi.backend.dto.admn13;

public record Admn13CreditModelFactorWeightDTO(
    String factorCode,
    String factorName,
    String weightPercent,
    String scoreCap,
    String scoreFloor) {}
