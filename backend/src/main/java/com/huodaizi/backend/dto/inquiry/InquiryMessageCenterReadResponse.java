package com.huodaizi.backend.dto.inquiry;

public record InquiryMessageCenterReadResponse(
    String merchantId,
    String messageId,
    String status,
    String statusText,
    String readAt,
    String message) {}
