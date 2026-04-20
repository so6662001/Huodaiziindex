package com.huodaizi.backend.dto.admn09;

public record Admn09ArbitrationTicketListItemDTO(
    String ticketId,
    String disputeId,
    String orderNo,
    String inquiryNo,
    String city,
    String buyerCompany,
    String supplierName,
    String issueTypeText,
    String arbitrationStatus,
    String arbitrationStatusText,
    String priorityLevel,
    String priorityLevelText,
    String assignedArbitrator,
    String hearingAt,
    String latestConclusion,
    String updatedAt) {}
