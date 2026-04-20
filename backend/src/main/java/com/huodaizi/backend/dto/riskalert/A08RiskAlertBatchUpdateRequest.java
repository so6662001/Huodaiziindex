package com.huodaizi.backend.dto.riskalert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record A08RiskAlertBatchUpdateRequest(
    @NotEmpty(message = "alertIds 不能为空")
    List<@NotBlank(message = "alertId 不能为空") @Size(max = 128, message = "alertId 最大长度128") String>
        alertIds,
    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @NotBlank(message = "operator 不能为空")
    @Size(max = 32, message = "operator 最大长度32")
    String operator,
    @Size(max = 300, message = "remark 最大长度300")
    String remark) {}
