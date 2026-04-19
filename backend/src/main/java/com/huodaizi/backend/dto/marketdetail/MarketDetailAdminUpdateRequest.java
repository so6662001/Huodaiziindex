package com.huodaizi.backend.dto.marketdetail;

import jakarta.validation.constraints.Size;

public record MarketDetailAdminUpdateRequest(
    @Size(max = 32, message = "symbol 最大长度32") String symbol,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 200, message = "subtitle 最大长度200") String subtitle,
    @Size(max = 64, message = "value 最大长度64") String value,
    @Size(max = 64, message = "trend 最大长度64") String trend,
    @Size(max = 500, message = "extra 最大长度500") String extra,
    @Size(max = 120, message = "link 最大长度120") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
