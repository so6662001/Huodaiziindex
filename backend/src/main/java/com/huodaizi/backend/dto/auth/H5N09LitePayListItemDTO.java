package com.huodaizi.backend.dto.auth;

public record H5N09LitePayListItemDTO(
    String cashierOrderId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String goodsName,
    String supplierName,
    String payableAmount,
    String paidAmount,
    String outstandingAmount,
    String payStatus,
    String payStatusText,
    String quickActionText,
    String updatedAt) {}
