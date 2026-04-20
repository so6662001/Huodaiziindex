package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record H5N08AfterSaleCreateRequest(
    @NotBlank(message = "orderId 不能为空")
    @Size(max = 32, message = "orderId 最大长度32")
    String orderId,
    @NotBlank(message = "issueType 不能为空")
    @Size(max = 32, message = "issueType 最大长度32")
    String issueType,
    @NotBlank(message = "issueSummary 不能为空")
    @Size(max = 120, message = "issueSummary 最大长度120")
    String issueSummary,
    @NotBlank(message = "issueDescription 不能为空")
    @Size(max = 500, message = "issueDescription 最大长度500")
    String issueDescription,
    @Size(max = 120, message = "expectedResolution 最大长度120")
    String expectedResolution,
    @NotBlank(message = "contactName 不能为空")
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @NotBlank(message = "contactPhone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactPhone 必须为11位手机号")
    String contactPhone,
    @Size(max = 500, message = "evidenceFiles 最大长度500")
    String evidenceFiles,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
