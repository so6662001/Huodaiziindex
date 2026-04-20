package com.huodaizi.backend.dto.admn11;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn11PaymentRefundReviewRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 24, message = "action 最大长度24")
    String action,
    @Size(max = 120, message = "rejectReason 最大长度120")
    String rejectReason,
    @Size(max = 200, message = "reviewRemark 最大长度200")
    String reviewRemark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
