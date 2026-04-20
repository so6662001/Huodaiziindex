package com.huodaizi.backend.dto.admn16;

public record Admn16DataApiQuotaMetricDTO(
    String metricCode,
    String metricName,
    String usedValue,
    String quotaValue,
    String usageRate,
    String trend) {}
