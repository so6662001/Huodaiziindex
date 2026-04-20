package com.huodaizi.backend.dto.pickupmonitor;

public record A04PickupMonitorItemDTO(
    String pickupId,
    String pickupNo,
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierId,
    String supplierName,
    String buyerCompany,
    String pickupAddress,
    String pickupDate,
    String truckNo,
    String driverName,
    String goodsSummary,
    String status,
    String statusText,
    String riskLevel,
    long agingHours,
    String latestRemark,
    String createdAt,
    String updatedAt) {}
