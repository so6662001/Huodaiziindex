package com.huodaizi.backend.dto.inquiry;

public record InquiryBillingOrderItemDTO(
    String billId,
    String billNo,
    String merchantId,
    String subscriptionId,
    String subscriptionNo,
    String planCode,
    String planName,
    String periodStart,
    String periodEnd,
    String issueDate,
    String dueDate,
    String amountYuan,
    String paidAmountYuan,
    String unpaidAmountYuan,
    String status,
    String statusText,
    String paymentMethod,
    String invoiceStatus,
    String createdAt,
    String updatedAt) {}
