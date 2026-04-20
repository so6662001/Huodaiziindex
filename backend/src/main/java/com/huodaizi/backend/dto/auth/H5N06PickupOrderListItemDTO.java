package com.huodaizi.backend.dto.auth;

public record H5N06PickupOrderListItemDTO(
    String pickupId,
    String pickupNo,
    String inquiryNo,
    String quoteId,
    String supplierName,
    String goodsSummary,
    String pickupAddress,
    String pickupDate,
    String truckNo,
    String status,
    String statusText,
    String quickActionText,
    String updatedAt) {}
