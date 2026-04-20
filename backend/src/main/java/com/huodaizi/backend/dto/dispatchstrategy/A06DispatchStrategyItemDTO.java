package com.huodaizi.backend.dto.dispatchstrategy;

import java.util.List;

public record A06DispatchStrategyItemDTO(
    String sceneCode,
    String sceneName,
    String ruleVersion,
    String scoreFormula,
    String updateCycle,
    String paidFactorDesc,
    int dimensionCount,
    int bonusCount,
    int penaltyCount,
    String updatedAt,
    List<A06DispatchStrategyDimensionDTO> dimensions,
    List<A06DispatchStrategyBonusDTO> bonuses,
    List<A06DispatchStrategyPenaltyDTO> penalties) {}
