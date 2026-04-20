package com.huodaizi.backend.dto.admn14;

public record Admn14AbExperimentMetricDTO(
    String metricCode,
    String metricName,
    String controlValue,
    String variantValue,
    String upliftRate,
    String confidenceLevel) {}
