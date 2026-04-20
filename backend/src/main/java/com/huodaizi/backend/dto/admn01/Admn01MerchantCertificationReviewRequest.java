package com.huodaizi.backend.dto.admn01;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn01MerchantCertificationReviewRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 16, message = "action 最大长度16")
    String action,
    @Size(max = 300, message = "reviewRemark 最大长度300")
    String reviewRemark,
    @Size(max = 64, message = "reviewer 最大长度64")
    String reviewer) {}
