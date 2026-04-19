package com.huodaizi.backend.dto.inquiry;

public record InquiryDealConfirmPreviewResponse(
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierId,
    String supplierName,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String unitPrice,
    String totalAmount,
    String paymentTerm,
    String deliveryDays,
    String expectedDeliveryAt,
    String invoiceNeed,
    String riskNotice) {}
