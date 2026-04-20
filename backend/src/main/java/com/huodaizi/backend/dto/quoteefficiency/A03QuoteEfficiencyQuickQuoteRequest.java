package com.huodaizi.backend.dto.quoteefficiency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record A03QuoteEfficiencyQuickQuoteRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @NotBlank(message = "supplierName 不能为空")
    @Size(max = 64, message = "supplierName 最大长度64")
    String supplierName,
    @NotBlank(message = "unitPrice 不能为空")
    @Pattern(regexp = "^\\d+(\\.\\d{1,3})?$", message = "unitPrice 格式不正确")
    String unitPrice,
    @NotBlank(message = "deliveryDays 不能为空")
    @Pattern(regexp = "^\\d+$", message = "deliveryDays 必须为整数")
    String deliveryDays,
    @Size(max = 64, message = "paymentTerm 最大长度64")
    String paymentTerm,
    @Size(max = 128, message = "quoteRemark 最大长度128")
    String quoteRemark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
