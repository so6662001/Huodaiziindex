package com.huodaizi.backend.dto.newsdetail;

import java.util.List;

public record NewsDetailArticleDTO(
    String id,
    String title,
    String category,
    String city,
    String publishAt,
    String source,
    String summary,
    List<String> content,
    List<String> tags,
    String status,
    boolean pinned,
    String updatedAt) {}
