package com.huodaizi.backend.dto.inquiry;

public record InquiryH5QuickQuoteInitResponse(
    String merchantId,
    String leadId,
    String inquiryId,
    String inquiryNo,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String invoiceNeed,
    String currentStatus,
    String suggestedUnitPrice,
    String suggestedDeliveryDays,
    String defaultPaymentTerm,
    String tipText) {}
