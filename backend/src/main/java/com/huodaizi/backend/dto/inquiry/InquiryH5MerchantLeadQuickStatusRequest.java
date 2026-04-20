package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryH5MerchantLeadQuickStatusRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 300, message = "comment 最大长度300")
    String comment) {}
