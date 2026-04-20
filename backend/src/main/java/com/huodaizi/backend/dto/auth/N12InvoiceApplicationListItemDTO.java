package com.huodaizi.backend.dto.auth;

public record N12InvoiceApplicationListItemDTO(
    String applicationId,
    String orderId,
    String orderNo,
    String invoiceType,
    String invoiceTypeText,
    String invoiceStatus,
    String invoiceStatusText,
    String invoiceAmount,
    String titleName,
    String createdAt,
    String updatedAt) {}
