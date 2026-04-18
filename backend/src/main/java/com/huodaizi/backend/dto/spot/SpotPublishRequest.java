package com.huodaizi.backend.dto.spot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpotPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @NotBlank(message = "category 不能为空")
    @Size(max = 32, message = "category 最大长度32")
    String category,
    @NotBlank(message = "spec 不能为空")
    @Size(max = 64, message = "spec 最大长度64")
    String spec,
    @NotBlank(message = "seller 不能为空")
    @Size(max = 64, message = "seller 最大长度64")
    String seller,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "price 不能为空")
    @Size(max = 32, message = "price 最大长度32")
    String price,
    @NotBlank(message = "tonnage 不能为空")
    @Size(max = 32, message = "tonnage 最大长度32")
    String tonnage,
    @NotBlank(message = "delivery 不能为空")
    @Size(max = 64, message = "delivery 最大长度64")
    String delivery,
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone) {}
