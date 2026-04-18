package com.huodaizi.backend.dto.sitecity;

import jakarta.validation.constraints.Size;

public record SiteCityAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 200, message = "subtitle 最大长度200") String subtitle,
    @Size(max = 64, message = "value 最大长度64") String value,
    @Size(max = 64, message = "extra 最大长度64") String extra,
    @Size(max = 120, message = "link 最大长度120") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
