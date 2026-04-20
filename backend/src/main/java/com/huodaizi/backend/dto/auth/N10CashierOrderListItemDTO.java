package com.huodaizi.backend.dto.auth;

public record N10CashierOrderListItemDTO(
    String cashierOrderId,
    String orderId,
    String orderNo,
    String supplierName,
    String payableAmount,
    String paidAmount,
    String outstandingAmount,
    String payStatus,
    String payStatusText,
    String dueAt,
    String updatedAt) {}
