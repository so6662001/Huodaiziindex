package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryH5MemberCreateRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @NotBlank(message = "planCode 不能为空")
    @Size(max = 32, message = "planCode 最大长度32")
    String planCode,
    @NotBlank(message = "billingCycle 不能为空")
    @Size(max = 16, message = "billingCycle 最大长度16")
    String billingCycle,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
