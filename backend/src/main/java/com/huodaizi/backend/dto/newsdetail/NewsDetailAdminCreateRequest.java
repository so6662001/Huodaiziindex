package com.huodaizi.backend.dto.newsdetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewsDetailAdminCreateRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 32, message = "category 最大长度32")
    String category,
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @Size(max = 32, message = "publishAt 最大长度32")
    String publishAt,
    @Size(max = 120, message = "source 最大长度120")
    String source,
    @Size(max = 300, message = "summary 最大长度300")
    String summary,
    @Size(max = 200, message = "tags 最大长度200")
    String tags,
    @Size(max = 4000, message = "content 最大长度4000")
    String content,
    @Size(max = 32, message = "relatedNewsId 最大长度32")
    String relatedNewsId,
    @Size(max = 500, message = "link 最大长度500")
    String link,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
