package com.huodaizi.backend.dto.inquiry;

public record InquiryMerchantCreditScoreTrendPointDTO(
    String month,
    String creditScore,
    String fulfillmentRate,
    String disputeRate,
    String responseMinutes) {}
