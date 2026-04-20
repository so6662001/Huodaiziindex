package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record N12InvoiceTitleUpsertRequest(
    String titleId,
    @NotBlank(message = "titleName 不能为空")
    @Size(max = 80, message = "titleName 最大长度80")
    String titleName,
    @NotBlank(message = "taxNo 不能为空")
    @Size(max = 30, message = "taxNo 最大长度30")
    String taxNo,
    @NotBlank(message = "address 不能为空")
    @Size(max = 120, message = "address 最大长度120")
    String address,
    @NotBlank(message = "phone 不能为空")
    @Size(max = 20, message = "phone 最大长度20")
    String phone,
    @Size(max = 80, message = "bankName 最大长度80")
    String bankName,
    @Size(max = 40, message = "bankAccountNo 最大长度40")
    String bankAccountNo,
    String defaultTitle,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
