package com.huodaizi.backend.dto.inquiry;

public record InquiryH5MerchantLeadQuickQuoteResponse(
    String leadId,
    String status,
    String unitPrice,
    String totalAmount,
    String deliveryDays,
    String message) {}
