package com.huodaizi.backend.dto.dispatchstrategy;

public record A06DispatchStrategyDimensionDTO(
    String code,
    String name,
    int weight,
    String description,
    String scoreMethod,
    String dataSource) {}
