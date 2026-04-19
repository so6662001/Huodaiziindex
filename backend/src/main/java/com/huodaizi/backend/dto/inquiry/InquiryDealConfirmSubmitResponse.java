package com.huodaizi.backend.dto.inquiry;

public record InquiryDealConfirmSubmitResponse(
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String dealStatus,
    String supplierId,
    String supplierName,
    String totalAmount,
    String message) {}
