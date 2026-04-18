package com.huodaizi.backend.dto.logistics;

public record LogisticsSearchResultDTO(
    String id,
    String type,
    String title,
    String city,
    String summary,
    String value,
    String link,
    String status,
    String updatedAt) {}
