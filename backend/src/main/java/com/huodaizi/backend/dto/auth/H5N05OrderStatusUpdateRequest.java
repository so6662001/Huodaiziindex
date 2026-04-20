package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record H5N05OrderStatusUpdateRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 32, message = "action 最大长度32")
    String action,
    @Size(max = 200, message = "remark 最大长度200")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
