package com.huodaizi.backend.dto.inquiry;

public record InquiryReconcileOrderDetailResponse(
    InquiryReconcileOrderItemDTO order,
    String latestRemark,
    String taxAmount,
    String payableAmount,
    String paidAmount,
    String unpaidAmount,
    String paymentDeadline,
    String voucherStatus) {}
