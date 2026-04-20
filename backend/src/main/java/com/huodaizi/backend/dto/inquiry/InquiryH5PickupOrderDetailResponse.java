package com.huodaizi.backend.dto.inquiry;

public record InquiryH5PickupOrderDetailResponse(
    InquiryPickupOrderItemDTO order,
    String pickupAddress,
    String contactName,
    String contactPhoneMasked,
    String vehicleNo,
    String driverName,
    String driverPhoneMasked,
    String latestRemark,
    String tipText) {}
