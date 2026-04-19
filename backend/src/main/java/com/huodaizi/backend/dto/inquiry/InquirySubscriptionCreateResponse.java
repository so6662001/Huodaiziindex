package com.huodaizi.backend.dto.inquiry;

public record InquirySubscriptionCreateResponse(
    String subscriptionId,
    String subscriptionNo,
    String merchantId,
    String planCode,
    String planName,
    String status,
    String startDate,
    String endDate,
    String payAmount,
    String currency,
    String message) {}
