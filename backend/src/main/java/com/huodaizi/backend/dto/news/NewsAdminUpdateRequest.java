package com.huodaizi.backend.dto.news;

import jakarta.validation.constraints.Size;

public record NewsAdminUpdateRequest(
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 500, message = "summary 最大长度500") String summary,
    @Size(max = 32, message = "publishAt 最大长度32") String publishAt,
    @Size(max = 120, message = "link 最大长度120") String link,
    @Size(max = 120, message = "tag 最大长度120") String tag,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
