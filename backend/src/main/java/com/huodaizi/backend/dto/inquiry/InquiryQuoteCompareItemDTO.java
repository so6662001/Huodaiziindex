package com.huodaizi.backend.dto.inquiry;

public record InquiryQuoteCompareItemDTO(
    String quoteId,
    String inquiryNo,
    String supplierId,
    String supplierName,
    String supplierCity,
    String supplierLevel,
    String serviceScore,
    String fulfillmentRate,
    String unitPrice,
    String totalAmount,
    String taxMode,
    String canInvoice,
    String canFreight,
    String deliveryDays,
    String responseMinutes,
    String paymentTerm,
    String quoteRemark,
    String quoteStatus,
    String quoteAt) {}
