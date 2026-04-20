package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryH5ReconcileOrderCreateRequest(
    @NotBlank(message = "pickupOrderId 不能为空")
    @Size(max = 64, message = "pickupOrderId 最大长度64")
    String pickupOrderId,
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @NotBlank(message = "statementMonth 不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "statementMonth 格式为 yyyy-MM")
    String statementMonth,
    @NotBlank(message = "dueDate 不能为空")
    @Size(max = 32, message = "dueDate 最大长度32")
    String dueDate,
    @Size(max = 120, message = "invoiceTitle 最大长度120")
    String invoiceTitle,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
