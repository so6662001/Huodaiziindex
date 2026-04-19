package com.huodaizi.backend.dto.inquiry;

public record InquiryCreateResponse(
    String inquiryId, String inquiryNo, String inquiryStatus, String message) {}
