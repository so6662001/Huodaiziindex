package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.Size;

public record InquiryH5InquiryStep1InitRequest(
    @Size(max = 32, message = "city 最大长度32")
    String city) {}
