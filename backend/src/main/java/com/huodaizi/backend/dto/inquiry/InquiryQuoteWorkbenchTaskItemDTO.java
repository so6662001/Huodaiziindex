package com.huodaizi.backend.dto.inquiry;

public record InquiryQuoteWorkbenchTaskItemDTO(
    String leadId,
    String leadNo,
    String inquiryId,
    String inquiryNo,
    String merchantId,
    String merchantName,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String invoiceNeed,
    String expectedDeliveryAt,
    String quoteAgeMinutes,
    boolean hasQuoted,
    String status,
    String latestRemark,
    String updatedAt) {}
