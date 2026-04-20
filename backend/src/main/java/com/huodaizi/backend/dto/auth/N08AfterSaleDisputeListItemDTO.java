package com.huodaizi.backend.dto.auth;

public record N08AfterSaleDisputeListItemDTO(
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
    String createdAt,
    String updatedAt) {}
