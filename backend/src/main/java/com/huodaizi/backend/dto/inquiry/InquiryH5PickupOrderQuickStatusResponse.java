package com.huodaizi.backend.dto.inquiry;

public record InquiryH5PickupOrderQuickStatusResponse(
    String pickupId, String status, String statusText, String message) {}
