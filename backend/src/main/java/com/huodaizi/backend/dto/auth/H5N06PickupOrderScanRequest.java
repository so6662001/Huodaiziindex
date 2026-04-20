package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record H5N06PickupOrderScanRequest(
    @NotBlank(message = "scanCode 不能为空")
    @Size(max = 64, message = "scanCode 最大长度64")
    String scanCode,
    @Size(max = 32, message = "operator 最大长度32")
    String operator,
    @Size(max = 300, message = "remark 最大长度300")
    String remark) {}
