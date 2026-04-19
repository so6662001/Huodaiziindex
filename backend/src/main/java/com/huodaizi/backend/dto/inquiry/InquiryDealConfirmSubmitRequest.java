package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryDealConfirmSubmitRequest(
    @NotBlank(message = "inquiryId 不能为空")
    @Size(max = 64, message = "inquiryId 最大长度64")
    String inquiryId,
    @NotBlank(message = "quoteId 不能为空")
    @Size(max = 64, message = "quoteId 最大长度64")
    String quoteId,
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @NotBlank(message = "buyerCompany 不能为空")
    @Size(max = 128, message = "buyerCompany 最大长度128")
    String buyerCompany,
    @NotBlank(message = "buyerContact 不能为空")
    @Size(max = 32, message = "buyerContact 最大长度32")
    String buyerContact,
    @NotBlank(message = "buyerPhone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "buyerPhone 必须为11位手机号")
    String buyerPhone,
    @Size(max = 64, message = "expectedSignDate 最大长度64")
    String expectedSignDate,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
