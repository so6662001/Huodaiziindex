package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record N12InvoiceTitleSetStatusRequest(
    @NotBlank(message = "status 不能为空")
    @Size(max = 16, message = "status 最大长度16")
    String status,
    @Size(max = 128, message = "remark 最大长度128")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
