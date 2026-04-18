package com.huodaizi.backend.dto.logistics;

import jakarta.validation.constraints.Size;

public record LogisticsSectionUpdateRequest(
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 160, message = "subtitle 最大长度160")
    String subtitle,
    @Size(max = 64, message = "city 最大长度64")
    String city,
    @Size(max = 64, message = "value 最大长度64")
    String value,
    @Size(max = 64, message = "extra 最大长度64")
    String extra,
    @Size(max = 255, message = "link 最大长度255")
    String link,
    @Size(max = 1000, message = "content 最大长度1000")
    String content,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
