package com.huodaizi.backend.dto.inquiry;

public record InquiryPickupOrderItemDTO(
    String pickupId,
    String pickupNo,
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierId,
    String supplierName,
    String buyerCompany,
    String pickupAddress,
    String contactName,
    String contactPhoneMasked,
    String pickupDate,
    String pickupTimeSlot,
    String truckNo,
    String driverName,
    String driverPhoneMasked,
    String goodsSummary,
    String status,
    String statusText,
    String createdAt,
    String updatedAt) {}
