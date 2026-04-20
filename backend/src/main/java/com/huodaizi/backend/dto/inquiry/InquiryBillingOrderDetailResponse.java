package com.huodaizi.backend.dto.inquiry;

public record InquiryBillingOrderDetailResponse(
    InquiryBillingOrderItemDTO order,
    String planDesc,
    String amountYuan,
    String taxRate,
    String taxAmountYuan,
    String netAmountYuan,
    String latestRemark) {}
