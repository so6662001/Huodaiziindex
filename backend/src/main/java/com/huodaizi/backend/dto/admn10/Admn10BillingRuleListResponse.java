package com.huodaizi.backend.dto.admn10;

import java.util.List;

public record Admn10BillingRuleListResponse(
    int total,
    int page,
    int pageSize,
    String ruleStatus,
    String sceneCode,
    String billingMode,
    String keyword,
    int activeCount,
    int disabledCount,
    int draftCount,
    List<Admn10BillingRuleListItemDTO> records) {}
