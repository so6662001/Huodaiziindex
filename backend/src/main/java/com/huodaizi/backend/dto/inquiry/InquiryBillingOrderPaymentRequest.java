package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryBillingOrderPaymentRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @NotBlank(message = "payAmount 不能为空")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "payAmount 金额格式错误")
    String payAmount,
    @Size(max = 64, message = "payChannel 最大长度64")
    String payChannel,
    @Size(max = 64, message = "operator 最大长度64")
    String operator,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
