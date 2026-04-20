package com.huodaizi.backend.dto.inquiry;

public record InquiryDispatchScoreRulePenaltyItemDTO(
    String code, String name, String deductionRange, String triggerCondition, String recoverSuggestion) {}
