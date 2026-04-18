package com.huodaizi.backend.dto.newsdetail;

public record NewsDetailAdminItemDTO(
    String id,
    String newsId,
    String section,
    String title,
    String category,
    String city,
    String publishAt,
    String source,
    String summary,
    String tags,
    String relatedNewsId,
    String content,
    String link,
    String status,
    boolean pinned,
    String updatedAt) {}
