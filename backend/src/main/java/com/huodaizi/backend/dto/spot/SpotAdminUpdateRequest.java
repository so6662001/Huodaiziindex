package com.huodaizi.backend.dto.spot;

import jakarta.validation.constraints.Size;

public record SpotAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 64, message = "seller 最大长度64") String seller,
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 64, message = "spec 最大长度64") String spec,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "price 最大长度32") String price,
    @Size(max = 32, message = "tonnage 最大长度32") String tonnage,
    @Size(max = 64, message = "delivery 最大长度64") String delivery,
    @Size(max = 32, message = "contactName 最大长度32") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 16, message = "status 最大长度16") String status,
    Boolean pinned) {}
