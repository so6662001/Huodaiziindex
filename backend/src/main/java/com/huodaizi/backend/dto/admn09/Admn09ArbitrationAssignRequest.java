package com.huodaizi.backend.dto.admn09;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn09ArbitrationAssignRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 24, message = "action 最大长度24")
    String action,
    @Size(max = 64, message = "assignedArbitrator 最大长度64")
    String assignedArbitrator,
    @Size(max = 32, message = "priorityLevel 最大长度32")
    String priorityLevel,
    @Size(max = 300, message = "handleRemark 最大长度300")
    String handleRemark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
