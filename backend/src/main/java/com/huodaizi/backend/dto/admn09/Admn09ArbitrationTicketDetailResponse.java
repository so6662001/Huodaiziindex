package com.huodaizi.backend.dto.admn09;

import java.util.List;

public record Admn09ArbitrationTicketDetailResponse(
    String ticketId,
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
    String arbitrationStatus,
    String arbitrationStatusText,
    String sourceStatus,
    String sourceStatusText,
    String currentStage,
    int progressPercent,
    String priorityLevel,
    String priorityLevelText,
    String assignedArbitrator,
    String latestRemark,
    String createdAt,
    String updatedAt,
    List<Admn09ArbitrationTicketProgressNodeDTO> progressNodes,
    List<String> availableActions) {}
