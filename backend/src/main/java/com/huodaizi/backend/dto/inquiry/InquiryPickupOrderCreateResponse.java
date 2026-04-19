package com.huodaizi.backend.dto.inquiry;

public record InquiryPickupOrderCreateResponse(
    String pickupId,
    String pickupNo,
    String inquiryId,
    String quoteId,
    String status,
    String message) {}
