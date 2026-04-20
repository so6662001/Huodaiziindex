package com.huodaizi.backend.dto.inquiry;

public record InquiryPickupOrderDetailResponse(
    InquiryPickupOrderItemDTO order,
    String pickupAddress,
    String contactName,
    String contactPhone,
    String vehicleNo,
    String driverName,
    String driverPhone,
    String latestRemark) {}
