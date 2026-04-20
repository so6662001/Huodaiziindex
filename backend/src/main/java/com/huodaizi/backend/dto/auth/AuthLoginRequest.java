package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
    @NotBlank(message = "account 不能为空")
    @Size(max = 64, message = "account 最大长度64")
    String account,
    @NotBlank(message = "password 不能为空")
    @Size(max = 64, message = "password 最大长度64")
    String password,
    @Pattern(regexp = "^$|^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile) {}
