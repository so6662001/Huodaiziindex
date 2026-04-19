package com.huodaizi.backend.dto.siteaddetail;

import jakarta.validation.constraints.Size;

public record SiteAdDetailAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 500, message = "subtitle 最大长度500") String subtitle,
    @Size(max = 500, message = "value 最大长度500") String value,
    @Size(max = 1000, message = "extra 最大长度1000") String extra,
    @Size(max = 120, message = "name 最大长度120") String name,
    @Size(max = 120, message = "price 最大长度120") String price,
    @Size(max = 1000, message = "desc 最大长度1000") String desc,
    @Size(max = 200, message = "exposureScene 最大长度200") String exposureScene,
    @Size(max = 200, message = "recommendedCategory 最大长度200") String recommendedCategory,
    @Size(max = 200, message = "reachHint 最大长度200") String reachHint,
    @Size(max = 120, message = "defaultCity 最大长度120") String defaultCity,
    @Size(max = 64, message = "defaultDuration 最大长度64") String defaultDuration,
    @Size(max = 500, message = "link 最大长度500") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
