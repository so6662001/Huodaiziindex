package com.huodaizi.backend.service.admn15;

import com.huodaizi.backend.dto.admn15.Admn15RiskAlertProgressNodeDTO;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketDetailResponse;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketHandleRequest;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketListItemDTO;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketListRequest;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketListResponse;
import com.huodaizi.backend.repository.auth.Admn15RiskAlertTicketEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn15RiskAlertTicketAdminService {
  private final InMemoryAuthRepository repository;

  public Admn15RiskAlertTicketAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn15RiskAlertTicketListResponse list(Admn15RiskAlertTicketListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn15RiskAlertTicketEntity> all =
        repository.listRiskAlertTicketsForAdmin(
            request == null ? null : request.ticketStatus(),
            request == null ? null : request.riskLevel(),
            request == null ? null : request.sourceType(),
            request == null ? null : request.owner(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn15RiskAlertTicketListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();

    int pendingCount = countByStatus(all, "OPEN");
    int processingCount = countByStatus(all, "PROCESSING");
    int escalatedCount = countByStatus(all, "ESCALATED");
    int resolvedCount = countByStatus(all, "RESOLVED");
    int closedCount = countByStatus(all, "CLOSED");

    return new Admn15RiskAlertTicketListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.ticketStatus()),
        request == null ? "" : safeText(request.riskLevel()),
        request == null ? "" : safeText(request.sourceType()),
        request == null ? "" : safeText(request.owner()),
        request == null ? "" : safeText(request.keyword()),
        pendingCount,
        processingCount,
        escalatedCount,
        resolvedCount,
        closedCount,
        records);
  }

  public Admn15RiskAlertTicketDetailResponse detail(String ticketId) {
    return toDetail(repository.getRiskAlertTicketForAdmin(ticketId));
  }

  public Admn15RiskAlertTicketDetailResponse handle(
      String ticketId, Admn15RiskAlertTicketHandleRequest request) {
    Admn15RiskAlertTicketEntity entity =
        repository.handleRiskAlertTicketForAdmin(
            ticketId,
            request.action(),
            request.targetStatus(),
            request.owner(),
            request.followUpPlan(),
            request.solution(),
            request.handleRemark() == null ? request.remark() : request.handleRemark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN15",
        "RISK_ALERT_TICKET_HANDLE",
        "RISK_ALERT_TICKET",
        entity.getTicketId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN15_HANDLE_" + entity.getTicketId(),
        "SUCCESS",
        repository.admn15AuditRiskLevel(entity.getSeverity(), entity.getTicketStatus()),
        "风险预警工单处理：" + entity.getTicketNo() + " -> " + entity.getTicketStatus(),
        "",
        "action="
            + safeText(request.action())
            + ", riskLevel="
            + safeText(entity.getSeverity())
            + ", score="
            + safeText(entity.getRiskScore()),
        "127.0.0.1",
        "admn15-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN15",
        "RISK_ALERT_TICKET_QUERY",
        "RISK_ALERT_TICKET",
        safeText(targetId).isBlank() ? "LIST" : targetId,
        operator,
        "ADMIN",
        traceId,
        "SUCCESS",
        "LOW",
        summary,
        "",
        "",
        "127.0.0.1",
        "admn15-service");
  }

  private int countByStatus(List<Admn15RiskAlertTicketEntity> all, String status) {
    return (int)
        all.stream()
            .filter(item -> status.equalsIgnoreCase(safeText(item.getTicketStatus())))
            .count();
  }

  private Admn15RiskAlertTicketListItemDTO toListItem(Admn15RiskAlertTicketEntity entity) {
    return new Admn15RiskAlertTicketListItemDTO(
        entity.getTicketId(),
        entity.getTicketNo(),
        entity.getRiskCode(),
        entity.getRiskTitle(),
        entity.getSourceType(),
        repository.admn15RiskSourceText(entity.getSourceType()),
        entity.getSeverity(),
        repository.admn15RiskLevelText(entity.getSeverity()),
        entity.getTicketStatus(),
        repository.admn15TicketStatusText(entity.getTicketStatus()),
        entity.getRiskScore(),
        entity.getOwner(),
        entity.getSuggestedAction(),
        calcAgingHours(entity),
        toText(entity.getUpdatedAt()));
  }

  private Admn15RiskAlertTicketDetailResponse toDetail(Admn15RiskAlertTicketEntity entity) {
    List<Admn15RiskAlertProgressNodeDTO> progressNodes =
        entity.getProgressNodes().stream()
            .map(
                item ->
                    new Admn15RiskAlertProgressNodeDTO(
                        item.nodeCode(),
                        item.nodeName(),
                        item.status(),
                        item.statusText(),
                        item.handler(),
                        item.remark(),
                        item.happenedAt()))
            .toList();
    return new Admn15RiskAlertTicketDetailResponse(
        entity.getTicketId(),
        entity.getTicketNo(),
        entity.getSourceType(),
        repository.admn15RiskSourceText(entity.getSourceType()),
        entity.getBizNo(),
        entity.getRiskCode(),
        entity.getRiskTitle(),
        entity.getRiskDetail(),
        entity.getSeverity(),
        repository.admn15RiskLevelText(entity.getSeverity()),
        entity.getRiskScore(),
        entity.getTicketStatus(),
        repository.admn15TicketStatusText(entity.getTicketStatus()),
        entity.getOwner(),
        entity.getHandler(),
        defaultText(entity.getFollowUpPlan(), ""),
        String.valueOf(calcAgingHours(entity)),
        entity.getSuggestedAction(),
        entity.getLatestRemark(),
        entity.getSourceUpdatedAt(),
        "",
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        progressNodes,
        availableActions(entity.getTicketStatus()));
  }

  private List<String> availableActions(String ticketStatus) {
    return switch (safeText(ticketStatus).toUpperCase(Locale.ROOT)) {
      case "OPEN" -> List.of("VIEW", "CLAIM", "ESCALATE");
      case "PROCESSING" -> List.of("VIEW", "FOLLOW_UP", "RESOLVE", "ESCALATE");
      case "ESCALATED" -> List.of("VIEW", "FOLLOW_UP", "RESOLVE");
      case "RESOLVED" -> List.of("VIEW", "REOPEN");
      default -> List.of("VIEW");
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String defaultText(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
  }

  private long calcAgingHours(Admn15RiskAlertTicketEntity entity) {
    return java.time.Duration.between(entity.getCreatedAt(), LocalDateTime.now()).toHours();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
