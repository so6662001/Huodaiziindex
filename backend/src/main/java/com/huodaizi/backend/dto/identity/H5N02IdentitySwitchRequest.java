package com.huodaizi.backend.dto.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record H5N02IdentitySwitchRequest(
    @NotBlank(message = "identityCode 不能为空")
    @Size(max = 32, message = "identityCode 最大长度32")
    String identityCode,
    @Size(max = 64, message = "channel 最大长度64")
    String channel) {}
