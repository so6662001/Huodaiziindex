package com.huodaizi.backend.dto.admn02;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn02BuyerBlacklistUpdateRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 16, message = "action 最大长度16")
    String action,
    @Size(max = 64, message = "reasonCode 最大长度64")
    String reasonCode,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
