package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N08AfterSaleDetailResponse(
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
    String channel,
    List<String> availableActions,
    String latestRemark,
    String tipText,
    String createdAt,
    String updatedAt) {}
