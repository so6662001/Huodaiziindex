package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record H5N01QuickLoginRequest(
    @NotBlank(message = "mobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "mobile 必须为11位手机号")
    String mobile,
    @NotBlank(message = "smsCode 不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "smsCode 必须为6位数字")
    String smsCode,
    @Size(max = 32, message = "channel 最大长度32")
    String channel,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
