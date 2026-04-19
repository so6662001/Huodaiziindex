package com.huodaizi.backend.dto.inquiry;

public record InquiryH5MemberCreateResponse(
    String subscriptionId,
    String subscriptionNo,
    String merchantId,
    String planCode,
    String planName,
    String status,
    String statusText,
    String amountYuan,
    String nextStepUrl,
    String message) {}
