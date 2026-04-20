package com.huodaizi.backend.dto.inquiry;

public record InquiryBillingOrderPaymentResponse(
    String billId,
    String billNo,
    String status,
    String paidAmount,
    String paidAt,
    String message) {}
