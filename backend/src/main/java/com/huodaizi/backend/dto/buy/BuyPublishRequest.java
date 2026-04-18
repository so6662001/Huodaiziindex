package com.huodaizi.backend.dto.buy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BuyPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @NotBlank(message = "category 不能为空")
    @Size(max = 32, message = "category 最大长度32")
    String category,
    @NotBlank(message = "spec 不能为空")
    @Size(max = 64, message = "spec 最大长度64")
    String spec,
    @NotBlank(message = "buyer 不能为空")
    @Size(max = 64, message = "buyer 最大长度64")
    String buyer,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "budget 不能为空")
    @Size(max = 32, message = "budget 最大长度32")
    String budget,
    @NotBlank(message = "demand 不能为空")
    @Size(max = 32, message = "demand 最大长度32")
    String demand,
    @NotBlank(message = "arrival 不能为空")
    @Size(max = 64, message = "arrival 最大长度64")
    String arrival,
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone) {}
