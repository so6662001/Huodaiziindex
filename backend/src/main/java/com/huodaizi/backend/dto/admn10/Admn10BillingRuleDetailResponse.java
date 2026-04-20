package com.huodaizi.backend.dto.admn10;

import java.util.List;

public record Admn10BillingRuleDetailResponse(
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
    String ladderConfig,
    String ruleStatus,
    String ruleStatusText,
    String effectiveFrom,
    String effectiveTo,
    String remark,
    String operator,
    String createdAt,
    String updatedAt,
    List<Admn10BillingRuleStepDTO> ruleSteps,
    List<String> availableActions) {}
