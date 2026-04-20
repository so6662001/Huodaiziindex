package com.huodaizi.backend.dto.inquiry;

public record InquiryMerchantCreditScoreDimensionDTO(
    String code,
    String name,
    int score,
    int weight,
    String trend,
    String summary) {}
