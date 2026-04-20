package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record H5N03EnterpriseCertificationSubmitRequest(
    @NotBlank(message = "companyName 不能为空")
    @Size(max = 80, message = "companyName 最大长度80")
    String companyName,
    @NotBlank(message = "unifiedSocialCreditCode 不能为空")
    @Pattern(regexp = "^[0-9A-Z]{18}$", message = "unifiedSocialCreditCode 必须为18位数字或大写字母")
    String unifiedSocialCreditCode,
    @NotBlank(message = "legalPersonName 不能为空")
    @Size(max = 32, message = "legalPersonName 最大长度32")
    String legalPersonName,
    @NotBlank(message = "legalPersonIdNo 不能为空")
    @Pattern(regexp = "^(\\d{15}|\\d{17}[0-9Xx])$", message = "legalPersonIdNo 格式错误")
    String legalPersonIdNo,
    @NotBlank(message = "contactName 不能为空")
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @NotBlank(message = "businessLicenseUrl 不能为空")
    @Size(max = 300, message = "businessLicenseUrl 最大长度300")
    String businessLicenseUrl,
    @NotBlank(message = "legalIdFrontUrl 不能为空")
    @Size(max = 300, message = "legalIdFrontUrl 最大长度300")
    String legalIdFrontUrl,
    @NotBlank(message = "legalIdBackUrl 不能为空")
    @Size(max = 300, message = "legalIdBackUrl 最大长度300")
    String legalIdBackUrl,
    @NotBlank(message = "bankAccountName 不能为空")
    @Size(max = 60, message = "bankAccountName 最大长度60")
    String bankAccountName,
    @NotBlank(message = "bankAccountNo 不能为空")
    @Pattern(regexp = "^\\d{8,30}$", message = "bankAccountNo 格式错误")
    String bankAccountNo,
    @NotBlank(message = "bankName 不能为空")
    @Size(max = 60, message = "bankName 最大长度60")
    String bankName,
    @NotBlank(message = "province 不能为空")
    @Size(max = 20, message = "province 最大长度20")
    String province,
    @NotBlank(message = "city 不能为空")
    @Size(max = 20, message = "city 最大长度20")
    String city,
    @NotBlank(message = "address 不能为空")
    @Size(max = 120, message = "address 最大长度120")
    String address,
    @Size(max = 200, message = "remark 最大长度200")
    String remark,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
