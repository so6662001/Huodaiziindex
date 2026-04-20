package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N06PickupOrderDetailResponse(
    String pickupId,
    String pickupNo,
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierName,
    String buyerCompany,
    String goodsSummary,
    String pickupAddress,
    String pickupDate,
    String truckNo,
    String driverName,
    String driverPhoneMasked,
    String status,
    String statusText,
    String contactMobileMasked,
    String channel,
    String scanResult,
    List<String> availableActions,
    String latestRemark,
    String createdAt,
    String updatedAt) {}
