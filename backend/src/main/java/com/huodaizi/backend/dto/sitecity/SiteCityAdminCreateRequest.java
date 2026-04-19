package com.huodaizi.backend.dto.sitecity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteCityAdminCreateRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 200, message = "subtitle 最大长度200")
    String subtitle,
    @Size(max = 120, message = "value 最大长度120")
    String value,
    @Size(max = 64, message = "extra 最大长度64")
    String extra,
    @Size(max = 120, message = "link 最大长度120")
    String link) {}
