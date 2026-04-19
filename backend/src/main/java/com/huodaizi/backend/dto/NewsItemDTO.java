package com.huodaizi.backend.dto;

public record NewsItemDTO(
    String id,
    String title,
    String summary,
    String category,
    String publishAt
) {}
