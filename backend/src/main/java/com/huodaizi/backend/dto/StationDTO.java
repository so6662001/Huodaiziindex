package com.huodaizi.backend.dto;

public record StationDTO(
    String id,
    String cityName,
    String citySlug,
    String intro,
    Integer dailyNewCount,
    Boolean adEnabled
) {}
