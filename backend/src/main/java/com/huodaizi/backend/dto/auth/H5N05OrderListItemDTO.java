package com.huodaizi.backend.dto.auth;

public record H5N05OrderListItemDTO(
    String orderId,
    String orderNo,
    String goodsName,
    String quantityText,
    String supplierName,
    String orderStatus,
    String orderStatusText,
    String dealAmount,
    String updatedAt,
    String quickActionText) {}
