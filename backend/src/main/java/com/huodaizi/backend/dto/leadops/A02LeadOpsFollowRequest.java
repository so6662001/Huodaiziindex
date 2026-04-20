package com.huodaizi.backend.dto.leadops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record A02LeadOpsFollowRequest(
    @Size(max = 32, message = "source 最大长度32")
    String source,
    @NotBlank(message = "operator 不能为空")
    @Size(max = 64, message = "operator 最大长度64")
    String operator,
    @NotBlank(message = "content 不能为空")
    @Size(max = 500, message = "content 最大长度500")
    String content,
    @Size(max = 120, message = "nextActionAt 最大长度120")
    String nextActionAt) {}
