package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record H5N09LitePaySubmitRequest(
    @NotBlank(message = "payMethod 不能为空")
    @Size(max = 32, message = "payMethod 最大长度32")
    String payMethod,
    @Size(max = 64, message = "payerName 最大长度64")
    String payerName,
    @Size(max = 200, message = "remark 最大长度200")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
