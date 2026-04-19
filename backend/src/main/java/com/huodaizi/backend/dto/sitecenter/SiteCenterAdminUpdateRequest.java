package com.huodaizi.backend.dto.sitecenter;

import jakarta.validation.constraints.Size;

public record SiteCenterAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 120, message = "subtitle 最大长度120") String subtitle,
    @Size(max = 120, message = "value 最大长度120") String value,
    @Size(max = 1000, message = "extra 最大长度1000") String extra,
    @Size(max = 120, message = "region 最大长度120") String region,
    @Size(max = 120, message = "cityName 最大长度120") String cityName,
    @Size(max = 120, message = "citySlug 最大长度120") String citySlug,
    @Size(max = 120, message = "focus 最大长度120") String focus,
    @Size(max = 120, message = "update 最大长度120") String update,
    @Size(max = 120, message = "price 最大长度120") String price,
    @Size(max = 500, message = "link 最大长度500") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
