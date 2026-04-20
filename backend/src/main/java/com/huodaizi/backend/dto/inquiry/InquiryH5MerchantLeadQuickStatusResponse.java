package com.huodaizi.backend.dto.inquiry;

public record InquiryH5MerchantLeadQuickStatusResponse(
    String leadId,
    String status,
    String latestFollow,
    String message) {}
