package com.huodaizi.backend.dto;

public record LogisticsFreightDTO(
    String id,
    String route,
    String vehicle,
    String leadTime,
    String price
) {}
