package com.huodaizi.backend.dto.sitead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SiteAdPublishRequest(
    @NotBlank(message = "city 不能为空")
    @Size(max = 120, message = "city 最大长度120")
    String city,
    @NotBlank(message = "placement 不能为空")
    @Size(max = 120, message = "placement 最大长度120")
    String placement,
    @NotBlank(message = "duration 不能为空")
    @Size(max = 32, message = "duration 最大长度32")
    String duration,
    @Size(max = 120, message = "budget 最大长度120")
    String budget,
    @NotBlank(message = "companyName 不能为空")
    @Size(max = 120, message = "companyName 最大长度120")
    String companyName,
    @NotBlank(message = "contactName 不能为空")
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @NotBlank(message = "phone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "phone 必须为11位手机号")
    String phone,
    @Size(max = 1000, message = "remark 最大长度1000")
    String remark,
    Boolean agreed) {}
