package com.huodaizi.backend.dto.inquiry;

public record InquiryDispatchScoreRuleBonusItemDTO(
    String code,
    String name,
    String type,
    int scoreImpact,
    String triggerCondition,
    String example) {}
