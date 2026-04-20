package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record N12InvoiceApplicationCreateRequest(
    @NotBlank(message = "orderId 不能为空")
    @Size(max = 32, message = "orderId 最大长度32")
    String orderId,
    @NotBlank(message = "titleId 不能为空")
    @Size(max = 32, message = "titleId 最大长度32")
    String titleId,
    @Size(max = 100, message = "invoiceContent 最大长度100")
    String invoiceContent,
    @Size(max = 200, message = "remark 最大长度200")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
