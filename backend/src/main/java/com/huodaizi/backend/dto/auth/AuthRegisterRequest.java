package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
    @NotBlank(message = "accountType 不能为空")
    @Size(max = 16, message = "accountType 最大长度16")
    String accountType,
    @NotBlank(message = "mobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "mobile 必须为11位手机号")
    String mobile,
    @NotBlank(message = "password 不能为空")
    @Size(min = 6, max = 64, message = "password 长度应为6-64")
    String password,
    @NotBlank(message = "confirmPassword 不能为空")
    @Size(min = 6, max = 64, message = "confirmPassword 长度应为6-64")
    String confirmPassword,
    @NotBlank(message = "smsCode 不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "smsCode 必须为6位数字")
    String smsCode,
    @Size(max = 64, message = "companyName 最大长度64")
    String companyName,
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
