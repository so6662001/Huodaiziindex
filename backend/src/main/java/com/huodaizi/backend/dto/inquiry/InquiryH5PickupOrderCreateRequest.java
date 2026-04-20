package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryH5PickupOrderCreateRequest(
    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @NotBlank(message = "inquiryId 不能为空")
    @Size(max = 64, message = "inquiryId 最大长度64")
    String inquiryId,
    @NotBlank(message = "quoteId 不能为空")
    @Size(max = 64, message = "quoteId 最大长度64")
    String quoteId,
    @NotBlank(message = "pickupSite 不能为空")
    @Size(max = 128, message = "pickupSite 最大长度128")
    String pickupSite,
    @NotBlank(message = "pickupDate 不能为空")
    @Size(max = 32, message = "pickupDate 最大长度32")
    String pickupDate,
    @NotBlank(message = "pickupVehicleNo 不能为空")
    @Size(max = 32, message = "pickupVehicleNo 最大长度32")
    String pickupVehicleNo,
    @NotBlank(message = "pickupDriverName 不能为空")
    @Size(max = 32, message = "pickupDriverName 最大长度32")
    String pickupDriverName,
    @NotBlank(message = "pickupDriverPhone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "pickupDriverPhone 必须为11位手机号")
    String pickupDriverPhone,
    @Size(max = 100, message = "buyerCompany 最大长度100")
    String buyerCompany,
    @Size(max = 32, message = "buyerContact 最大长度32")
    String buyerContact,
    @Size(max = 500, message = "remark 最大长度500")
    String remark,
    boolean agreedProtocol) {}
