package com.huodaizi.backend.dto.auth;

public record H5N08AfterSaleListItemDTO(
    String disputeId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String supplierName,
    String issueType,
    String issueTypeText,
    String issueSummary,
    String status,
    String statusText,
    String quickActionText,
    String updatedAt) {}
