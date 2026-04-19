package com.huodaizi.backend.dto.inquiry;

public record InquiryItemDTO(
    String inquiryId,
    String inquiryNo,
    String categoryCode,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String expectedDeliveryAt,
    String invoiceNeed,
    String inquiryStatus,
    int quoteCount,
    String createdAt,
    String updatedAt) {}
