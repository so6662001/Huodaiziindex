package com.huodaizi.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUpdateRequest(
    @Size(max = 120, message = "name 最大长度120")
    String name,
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 1000, message = "content 最大长度1000")
    String content,
    @Size(max = 64, message = "city 最大长度64")
    String city,
    @Size(max = 64, message = "type 最大长度64")
    String type,
    @Size(max = 64, message = "category 最大长度64")
    String category,
    @Size(max = 64, message = "price 最大长度64")
    String price,
    @Size(max = 64, message = "trend 最大长度64")
    String trend,
    @Size(max = 64, message = "meta 最大长度64")
    String meta
) {}
