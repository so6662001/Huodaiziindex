package com.huodaizi.backend.dto.admn15;

import java.util.List;

public record Admn15RiskAlertTicketDetailResponse(
    String ticketId,
    String ticketCode,
    String sourceType,
    String sourceTypeText,
    String bizNo,
    String riskCode,
    String riskTitle,
    String riskDetail,
    String riskLevel,
    String riskLevelText,
    String riskScore,
    String ticketStatus,
    String ticketStatusText,
    String owner,
    String resolver,
    String followUpPlan,
    String agingHours,
    String suggestedAction,
    String latestRemark,
    String alertAt,
    String dueAt,
    String createdAt,
    String updatedAt,
    List<Admn15RiskAlertProgressNodeDTO> progressNodes,
    List<String> availableActions) {}
