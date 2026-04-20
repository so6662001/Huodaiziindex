package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryDispatchScoreRuleResponse(
    String merchantId,
    String ruleVersion,
    String ruleName,
    String scoreRange,
    String formula,
    String explanation,
    List<InquiryDispatchScoreRuleDimensionDTO> dimensions,
    List<InquiryDispatchScoreRuleBonusItemDTO> bonusItems,
    List<InquiryDispatchScoreRulePenaltyItemDTO> penaltyItems,
    List<InquiryDispatchScoreRuleCaseItemDTO> cases,
    String updatedAt) {}
