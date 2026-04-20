package com.huodaizi.backend.dto.auth;

public record N09AfterSaleProgressListItemDTO(
    String disputeId,
    String orderNo,
    String supplierName,
    String issueTypeText,
    String issueSummary,
    String currentStage,
    String currentStatus,
    String currentStatusText,
    String latestRemark,
    String updatedAt) {}
