package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record N14DispatchAppealStatusUpdateRequest(
    @NotBlank(message = "status 不能为空")
    @Size(max = 24, message = "status 最大长度24")
    String status,
    @Size(max = 200, message = "remark 最大长度200")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
