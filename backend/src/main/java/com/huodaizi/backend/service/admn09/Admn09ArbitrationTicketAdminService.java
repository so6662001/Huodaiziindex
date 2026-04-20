package com.huodaizi.backend.service.admn09;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationAssignRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationReviewRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketDetailResponse;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketListItemDTO;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketListRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketListResponse;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketProgressNodeDTO;
import com.huodaizi.backend.repository.auth.Admn09ArbitrationTicketEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.auth.N08AfterSaleDisputeEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn09ArbitrationTicketAdminService {
  private final InMemoryAuthRepository repository;

  public Admn09ArbitrationTicketAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn09ArbitrationTicketListResponse list(Admn09ArbitrationTicketListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn09ArbitrationTicketEntity> all =
        repository.listArbitrationTicketsForAdmin(
            request == null ? null : request.arbitrationStatus(),
            request == null ? null : request.priorityLevel(),
            request == null ? null : request.city(),
            request == null ? null : request.assignedArbitrator(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn09ArbitrationTicketListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int pendingCount =
        (int)
            all.stream()
                .filter(item -> "PENDING_ASSIGN".equalsIgnoreCase(safeText(item.getArbitrationStatus())))
                .count();
    int processingCount =
        (int)
            all.stream()
                .filter(item -> "PROCESSING".equalsIgnoreCase(safeText(item.getArbitrationStatus())))
                .count();
    int resolvedCount =
        (int)
            all.stream()
                .filter(item -> "RESOLVED".equalsIgnoreCase(safeText(item.getArbitrationStatus())))
                .count();
    int closedCount =
        (int)
            all.stream()
                .filter(item -> "CLOSED".equalsIgnoreCase(safeText(item.getArbitrationStatus())))
                .count();
    return new Admn09ArbitrationTicketListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.arbitrationStatus()),
        request == null ? "" : safeText(request.priorityLevel()),
        request == null ? "" : safeText(request.city()),
        request == null ? "" : safeText(request.assignedArbitrator()),
        request == null ? "" : safeText(request.keyword()),
        pendingCount,
        processingCount,
        resolvedCount,
        closedCount,
        records);
  }

  public Admn09ArbitrationTicketDetailResponse detail(String ticketId) {
    Admn09ArbitrationTicketEntity ticket = repository.getArbitrationTicketForAdmin(ticketId);
    return toDetail(ticket);
  }

  public Admn09ArbitrationTicketDetailResponse assign(
      String ticketId, Admn09ArbitrationAssignRequest request) {
    Admn09ArbitrationTicketEntity updated =
        repository.assignArbitrationTicketForAdmin(
            ticketId,
            request.action(),
            request.assignedArbitrator(),
            request.priorityLevel(),
            request.handleRemark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN09",
        "ARBITRATION_ASSIGN",
        "ARBITRATION_TICKET",
        updated.getTicketId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN09_ASSIGN_" + updated.getTicketId(),
        "SUCCESS",
        riskLevelByPriority(updated.getPriorityLevel()),
        "仲裁工单分派：" + updated.getTicketId() + " -> " + updated.getArbitrationStatus(),
        "",
        "action="
            + safeText(request.action())
            + ", priority="
            + safeText(updated.getPriorityLevel())
            + ", arbitrator="
            + safeText(updated.getAssignedArbitrator()),
        "127.0.0.1",
        "admn09-service");
    return toDetail(updated);
  }

  public Admn09ArbitrationTicketDetailResponse review(
      String ticketId, Admn09ArbitrationReviewRequest request) {
    Admn09ArbitrationTicketEntity updated =
        repository.reviewArbitrationTicketForAdmin(
            ticketId,
            request.action(),
            request.resolutionSummary(),
            request.resolutionDetail(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN09",
        "ARBITRATION_REVIEW",
        "ARBITRATION_TICKET",
        updated.getTicketId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN09_REVIEW_" + updated.getTicketId(),
        "SUCCESS",
        riskLevelByPriority(updated.getPriorityLevel()),
        "仲裁工单裁决：" + updated.getTicketId() + " -> " + updated.getArbitrationStatus(),
        "",
        "action="
            + safeText(request.action())
            + ", conclusion="
            + safeText(updated.getLatestConclusion()),
        "127.0.0.1",
        "admn09-service");
    return toDetail(updated);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN09",
        "ARBITRATION_QUERY",
        "ARBITRATION_TICKET",
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
        "admn09-service");
  }

  private Admn09ArbitrationTicketListItemDTO toListItem(Admn09ArbitrationTicketEntity ticket) {
    N08AfterSaleDisputeEntity dispute = safeDispute(ticket.getDisputeId());
    return new Admn09ArbitrationTicketListItemDTO(
        ticket.getTicketId(),
        ticket.getDisputeId(),
        dispute == null ? "-" : safeText(dispute.getOrderNo()),
        dispute == null ? "-" : safeText(dispute.getInquiryNo()),
        safeText(ticket.getCity()),
        dispute == null ? "-" : safeText(dispute.getBuyerCompany()),
        dispute == null ? "-" : safeText(dispute.getSupplierName()),
        dispute == null ? "-" : safeText(dispute.getIssueTypeText()),
        ticket.getArbitrationStatus(),
        repository.admn09ArbitrationStatusText(ticket.getArbitrationStatus()),
        ticket.getPriorityLevel(),
        repository.admn09PriorityText(ticket.getPriorityLevel()),
        safeText(ticket.getAssignedArbitrator()),
        safeText(ticket.getHearingAt()),
        safeText(ticket.getLatestConclusion()),
        toText(ticket.getUpdatedAt()));
  }

  private Admn09ArbitrationTicketDetailResponse toDetail(Admn09ArbitrationTicketEntity ticket) {
    N08AfterSaleDisputeEntity dispute = repository.getAfterSaleDisputeDetailForAdmin(ticket.getDisputeId());
    List<Admn09ArbitrationTicketProgressNodeDTO> progressNodes =
        ticket.getTimelineNodes().stream()
            .map(
                node ->
                    new Admn09ArbitrationTicketProgressNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getRemark(),
                        node.getHappenedAt()))
            .toList();
    return new Admn09ArbitrationTicketDetailResponse(
        ticket.getTicketId(),
        ticket.getDisputeId(),
        dispute.getOrderId(),
        dispute.getOrderNo(),
        dispute.getInquiryNo(),
        dispute.getBuyerCompany(),
        dispute.getSupplierName(),
        dispute.getIssueType(),
        dispute.getIssueTypeText(),
        dispute.getIssueSummary(),
        dispute.getIssueDescription(),
        dispute.getExpectedResolution(),
        dispute.getContactName(),
        dispute.getContactPhoneMasked(),
        dispute.getEvidenceFiles(),
        ticket.getArbitrationStatus(),
        repository.admn09ArbitrationStatusText(ticket.getArbitrationStatus()),
        dispute.getStatus(),
        dispute.getStatusText(),
        currentStageByStatus(ticket.getArbitrationStatus()),
        progressPercentByArbitrationStatus(ticket.getArbitrationStatus()),
        ticket.getPriorityLevel(),
        repository.admn09PriorityText(ticket.getPriorityLevel()),
        safeText(ticket.getAssignedArbitrator()),
        safeText(ticket.getLatestRemark()),
        toText(ticket.getCreatedAt()),
        toText(ticket.getUpdatedAt()),
        progressNodes,
        availableActions(ticket.getArbitrationStatus()));
  }

  private N08AfterSaleDisputeEntity safeDispute(String disputeId) {
    try {
      return repository.getAfterSaleDisputeDetailForAdmin(disputeId);
    } catch (BaseException ignore) {
      return null;
    }
  }

  private List<String> availableActions(String arbitrationStatus) {
    return switch (safeText(arbitrationStatus).toUpperCase(Locale.ROOT)) {
      case "PENDING_ASSIGN" -> List.of("VIEW", "ASSIGN", "TRANSFER");
      case "PROCESSING" -> List.of("VIEW", "REVIEW", "REASSIGN", "CLOSE");
      case "RESOLVED" -> List.of("VIEW", "ARCHIVE", "REOPEN");
      case "CLOSED" -> List.of("VIEW", "REOPEN");
      default -> List.of("VIEW");
    };
  }

  private String riskLevelByPriority(String priorityLevel) {
    String normalized = safeText(priorityLevel).toUpperCase(Locale.ROOT);
    if ("URGENT".equals(normalized) || "HIGH".equals(normalized)) {
      return "HIGH";
    }
    if ("MEDIUM".equals(normalized)) {
      return "MEDIUM";
    }
    return "LOW";
  }

  private int progressPercentByArbitrationStatus(String arbitrationStatus) {
    return switch (safeText(arbitrationStatus).toUpperCase(Locale.ROOT)) {
      case "PENDING_ASSIGN" -> 20;
      case "PROCESSING" -> 65;
      case "RESOLVED" -> 90;
      case "CLOSED" -> 100;
      default -> 0;
    };
  }

  private String currentStageByStatus(String arbitrationStatus) {
    return switch (safeText(arbitrationStatus).toUpperCase(Locale.ROOT)) {
      case "PENDING_ASSIGN" -> "待分派";
      case "PROCESSING" -> "仲裁处理中";
      case "RESOLVED" -> "仲裁裁决";
      case "CLOSED" -> "归档关闭";
      default -> "处理中";
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
