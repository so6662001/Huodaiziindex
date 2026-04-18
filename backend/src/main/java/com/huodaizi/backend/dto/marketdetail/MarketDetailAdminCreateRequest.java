package com.huodaizi.backend.dto.marketdetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarketDetailAdminCreateRequest(
    @NotBlank(message = "symbol 不能为空")
    @Size(max = 32, message = "symbol 最大长度32")
    String symbol,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 200, message = "subtitle 最大长度200")
    String subtitle,
    @Size(max = 120, message = "value 最大长度120")
    String value,
    @Size(max = 64, message = "trend 最大长度64")
    String trend,
    @Size(max = 120, message = "link 最大长度120")
    String link) {}
