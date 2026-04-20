package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.Size;

public record InquiryH5HomeRequest(
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId) {}
