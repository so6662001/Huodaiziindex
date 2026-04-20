package com.huodaizi.backend.dto.inquiry;

public record InquiryMerchantLeadDetailResponse(
    InquiryMerchantLeadItemDTO lead,
    String specText,
    String deliveryCity,
    String demandQtyTon,
    String invoiceNeed,
    String expectedDeliveryAt,
    String suggestAction) {}
