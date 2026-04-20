package com.huodaizi.backend.dto.admn15;

public record Admn15RiskAlertTicketListItemDTO(
    String ticketId,
    String ticketNo,
    String riskCode,
    String riskTitle,
    String sourceType,
    String sourceText,
    String riskLevel,
    String riskLevelText,
    String ticketStatus,
    String ticketStatusText,
    String riskScore,
    String owner,
    String suggestedAction,
    long agingHours,
    String updatedAt) {}
