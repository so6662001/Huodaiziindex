package com.huodaizi.backend.dto.leadops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record A02LeadOpsAssignRequest(
    @NotBlank(message = "operator 不能为空")
    @Size(max = 64, message = "operator 最大长度64")
    String operator,
    @NotBlank(message = "ownerName 不能为空")
    @Size(max = 120, message = "ownerName 最大长度120")
    String ownerName,
    @Size(max = 120, message = "team 最大长度120")
    String team,
    @Size(max = 300, message = "comment 最大长度300")
    String comment) {}
