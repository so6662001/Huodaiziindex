package com.huodaizi.backend.dto.market;

import jakarta.validation.constraints.Size;

public record MarketAdminUpdateRequest(
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 16, message = "rangeTag 最大长度16") String rangeTag,
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 120, message = "subtitle 最大长度120") String subtitle,
    @Size(max = 64, message = "price 最大长度64") String price,
    @Size(max = 64, message = "highPrice 最大长度64") String highPrice,
    @Size(max = 64, message = "lowPrice 最大长度64") String lowPrice,
    @Size(max = 64, message = "changeValue 最大长度64") String changeValue,
    @Size(max = 120, message = "tag 最大长度120") String tag,
    @Size(max = 120, message = "link 最大长度120") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
