package com.huodaizi.backend.dto.admn10;

public record Admn10BillingRuleStepDTO(
    int stepNo, String rangeStart, String rangeEnd, String priceOrRatio, String unit) {}
