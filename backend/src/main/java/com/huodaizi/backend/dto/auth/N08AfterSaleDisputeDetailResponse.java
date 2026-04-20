package com.huodaizi.backend.dto.auth;

public record N08AfterSaleDisputeDetailResponse(
    String disputeId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String buyerCompany,
    String supplierName,
    String issueType,
    String issueTypeText,
    String issueSummary,
    String issueDescription,
    String expectedResolution,
    String contactName,
    String contactPhoneMasked,
    String evidenceFiles,
    String status,
    String statusText,
    String latestRemark,
    String createdAt,
    String updatedAt) {}
