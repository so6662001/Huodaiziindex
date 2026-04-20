package com.huodaizi.backend.dto.dispatchstrategy;

import java.util.List;

public record A06DispatchStrategyListResponse(
    List<A06DispatchStrategyItemDTO> items,
    int total,
    int page,
    int pageSize,
    int activeSceneCount,
    int totalBonusCount,
    int totalPenaltyCount,
    String avgDimensionWeight,
    String updatedAt) {}
