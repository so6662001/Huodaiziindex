package com.huodaizi.backend.dto.auth;

public record H5N07ReconcileListItemDTO(
    String reconcileId,
    String reconcileNo,
    String pickupOrderNo,
    String supplierName,
    String goodsSummary,
    String totalAmount,
    String paidAmount,
    String unpaidAmount,
    String status,
    String statusText,
    String quickActionText,
    String updatedAt) {}
