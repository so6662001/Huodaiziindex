package com.huodaizi.backend.dto.auth;

public record N06OrderListItemDTO(
    String orderId,
    String orderNo,
    String inquiryNo,
    String goodsName,
    String specText,
    String quantityText,
    String supplierName,
    String buyerCompany,
    String orderStatus,
    String orderStatusText,
    String dealAmount,
    String createdAt,
    String updatedAt) {}
