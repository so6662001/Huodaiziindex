package com.huodaizi.backend.dto.quoteefficiency;

public record A03QuoteEfficiencyTaskItemDTO(
    String leadId,
    String leadNo,
    String inquiryId,
    String inquiryNo,
    String merchantId,
    String merchantName,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String expectedDeliveryAt,
    String quoteAgeMinutes,
    String efficiencyLevel,
    boolean quoteTimeout,
    boolean hasQuoted,
    String status,
    String latestRemark,
    String updatedAt) {}
