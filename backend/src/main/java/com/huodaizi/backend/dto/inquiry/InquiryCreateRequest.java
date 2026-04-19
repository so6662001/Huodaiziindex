package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record InquiryCreateRequest(
    @NotBlank(message = "categoryCode 不能为空")
    @Size(max = 32, message = "categoryCode 最大长度32")
    String categoryCode,
    @NotBlank(message = "specText 不能为空")
    @Size(max = 128, message = "specText 最大长度128")
    String specText,
    @NotNull(message = "demandQtyTon 不能为空")
    @Positive(message = "demandQtyTon 必须大于0")
    BigDecimal demandQtyTon,
    @NotBlank(message = "deliveryCity 不能为空")
    @Size(max = 32, message = "deliveryCity 最大长度32")
    String deliveryCity,
    @Size(max = 64, message = "expectedDeliveryAt 最大长度64")
    String expectedDeliveryAt,
    @Size(max = 16, message = "invoiceNeed 最大长度16")
    String invoiceNeed,
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
