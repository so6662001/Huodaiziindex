package com.huodaizi.backend.dto.siteadlead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SiteAdLeadSubmitRequest(
    @NotBlank(message = "placementId 不能为空")
    @Size(max = 64, message = "placementId 最大长度64")
    String placementId,
    @NotBlank(message = "placementName 不能为空")
    @Size(max = 120, message = "placementName 最大长度120")
    String placementName,
    @NotBlank(message = "city 不能为空")
    @Size(max = 120, message = "city 最大长度120")
    String city,
    @NotBlank(message = "duration 不能为空")
    @Size(max = 64, message = "duration 最大长度64")
    String duration,
    @Size(max = 120, message = "budget 最大长度120")
    String budget,
    @NotBlank(message = "companyName 不能为空")
    @Size(min = 4, max = 120, message = "companyName 长度需在4-120之间")
    String companyName,
    @NotBlank(message = "contactName 不能为空")
    @Size(min = 2, max = 32, message = "contactName 长度需在2-32之间")
    String contactName,
    @NotBlank(message = "contactPhone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactPhone 必须为11位手机号")
    String contactPhone,
    @Size(max = 1000, message = "remark 最大长度1000")
    String remark,
    Boolean agreed) {}
