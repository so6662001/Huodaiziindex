package com.huodaizi.backend.dto.admn14;

import java.util.List;

public record Admn14AbExperimentDetailResponse(
    String experimentId,
    String experimentCode,
    String experimentName,
    String experimentStatus,
    String experimentStatusText,
    String scenarioCode,
    String scenarioText,
    String optimizationStage,
    String optimizationStageText,
    String targetMetric,
    String baselineValue,
    String targetValue,
    String confidenceLevel,
    String trafficSplitPlan,
    String winnerVariant,
    String expectedGainRate,
    String startDate,
    String endDate,
    String owner,
    String reviewer,
    String remark,
    String createdAt,
    String updatedAt,
    List<Admn14AbExperimentMetricDTO> metrics,
    List<String> availableActions) {}
