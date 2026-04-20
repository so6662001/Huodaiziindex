package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N09AfterSaleProgressDetailResponse(
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
    String currentStatus,
    String currentStatusText,
    String latestRemark,
    String createdAt,
    String updatedAt,
    int progressPercent,
    String currentStage,
    List<N09AfterSaleProgressNodeDTO> nodes) {}
