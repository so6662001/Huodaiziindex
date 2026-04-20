package com.huodaizi.backend.dto.leadops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record A02LeadOpsStatusUpdateRequest(
    @Size(max = 32, message = "source 最大长度32")
    String source,
    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 120, message = "operator 最大长度120")
    String operator,
    @Size(max = 300, message = "comment 最大长度300")
    String comment) {}
