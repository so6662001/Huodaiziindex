package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryDealConfirmPreviewRequest(
    @NotBlank(message = "inquiryId 不能为空")
    @Size(max = 64, message = "inquiryId 最大长度64")
    String inquiryId,
    @NotBlank(message = "quoteId 不能为空")
    @Size(max = 64, message = "quoteId 最大长度64")
    String quoteId,
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile) {}
