package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InquiryH5PickupOrderDetailRequest(
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile) {}
