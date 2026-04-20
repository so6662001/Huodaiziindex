package com.huodaizi.backend.dto.admn14;

public record Admn14AbExperimentListItemDTO(
    String experimentId,
    String experimentCode,
    String experimentName,
    String scenarioCode,
    String scenarioText,
    String experimentStatus,
    String experimentStatusText,
    String optimizationStage,
    String optimizationStageText,
    String trafficPercent,
    String primaryMetric,
    String liftPercent,
    String confidenceLevel,
    String owner,
    String updatedAt) {}
