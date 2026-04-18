package com.huodaizi.backend.dto.news;

public record NewsItemDTO(
    String id,
    String title,
    String summary,
    String category,
    String city,
    String publishAt,
    String link,
    String status,
    boolean pinned,
    String updatedAt) {}
