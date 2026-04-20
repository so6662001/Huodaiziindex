package com.huodaizi.backend.dto.inquiry;

public record InquiryReconcileOrderItemDTO(
    String reconcileId,
    String reconcileNo,
    String pickupOrderId,
    String pickupOrderNo,
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierId,
    String supplierName,
    String buyerCompany,
    String contactPhoneMasked,
    String goodsSummary,
    String totalAmount,
    String paidAmount,
    String unpaidAmount,
    String invoiceStatus,
    String settleDueDate,
    String status,
    String statusText,
    String createdAt,
    String updatedAt) {}
