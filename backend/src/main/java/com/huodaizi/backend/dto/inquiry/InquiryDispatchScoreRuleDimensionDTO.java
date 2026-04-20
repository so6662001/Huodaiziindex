package com.huodaizi.backend.dto.inquiry;

public record InquiryDispatchScoreRuleDimensionDTO(
    String code,
    String name,
    int weight,
    String description,
    String scoringMethod,
    String impactDirection) {}
