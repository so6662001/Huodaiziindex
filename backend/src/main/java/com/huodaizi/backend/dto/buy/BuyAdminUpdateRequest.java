package com.huodaizi.backend.dto.buy;

import jakarta.validation.constraints.Size;

public record BuyAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 64, message = "buyer 最大长度64") String buyer,
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 64, message = "spec 最大长度64") String spec,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "budget 最大长度32") String budget,
    @Size(max = 32, message = "demand 最大长度32") String demand,
    @Size(max = 64, message = "arrival 最大长度64") String arrival,
    @Size(max = 32, message = "contactName 最大长度32") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 16, message = "status 最大长度16") String status,
    Boolean pinned) {}
