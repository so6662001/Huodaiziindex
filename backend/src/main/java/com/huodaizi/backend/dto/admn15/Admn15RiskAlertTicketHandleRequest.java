package com.huodaizi.backend.dto.admn15;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn15RiskAlertTicketHandleRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 24, message = "action 最大长度24")
    String action,
    @Size(max = 24, message = "targetStatus 最大长度24")
    String targetStatus,
    @Size(max = 64, message = "owner 最大长度64")
    String owner,
    @Size(max = 300, message = "followUpPlan 最大长度300")
    String followUpPlan,
    @Size(max = 300, message = "solution 最大长度300")
    String solution,
    @Size(max = 300, message = "handleRemark 最大长度300")
    String handleRemark,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
