package com.huodaizi.backend.dto.admn10;

public record Admn10BillingRuleListItemDTO(
    String ruleId,
    String ruleCode,
    String ruleName,
    String sceneCode,
    String sceneCodeText,
    String billingMode,
    String billingModeText,
    String feeCurrency,
    String basePriceYuan,
    String minFeeYuan,
    String maxFeeYuan,
    String ruleStatus,
    String ruleStatusText,
    String effectiveFrom,
    String effectiveTo,
    String operator,
    String updatedAt) {}
