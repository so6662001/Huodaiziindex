package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record H5N01SendLoginCodeRequest(
    @NotBlank(message = "mobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "mobile 必须为11位手机号")
    String mobile,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
