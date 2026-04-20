package com.huodaizi.backend.dto.inquiry;

public record InquiryH5PickupOrderCreateResponse(
    String pickupId,
    String pickupNo,
    String inquiryId,
    String quoteId,
    String status,
    String statusText,
    String nextStepUrl,
    String message) {}
