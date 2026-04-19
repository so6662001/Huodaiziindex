package com.huodaizi.backend.dto.logistics;

public record LogisticsFreightCardDTO(
    String id,
    String line,
    String vehicle,
    String leadTime,
    String price) {}
