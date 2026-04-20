package com.huodaizi.backend.service.admn04;

import com.huodaizi.backend.dto.admn04.Admn04AuditLogDetailResponse;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogListItemDTO;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogListRequest;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogListResponse;
import com.huodaizi.backend.repository.auth.Admn04AuditLogEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class Admn04AuditLogAdminService {
  private final InMemoryAuthRepository repository;

  public Admn04AuditLogAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn04AuditLogListResponse list(Admn04AuditLogListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn04AuditLogEntity> all =
        repository.listAuditLogsForAdmin(
            request == null ? null : request.moduleCode(),
            request == null ? null : request.actionCode(),
            request == null ? null : request.resultStatus(),
            request == null ? null : request.operator(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn04AuditLogListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int successCount =
        (int) all.stream().filter(item -> "SUCCESS".equalsIgnoreCase(safeText(item.getResult()))).count();
    return new Admn04AuditLogListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.keyword()),
        request == null ? "" : safeText(request.moduleCode()),
        request == null ? "" : safeText(request.actionCode()),
        request == null ? "" : safeText(request.resultStatus()),
        successCount,
        all.size() - successCount,
        records);
  }

  public Admn04AuditLogDetailResponse detail(String logId) {
    Admn04AuditLogEntity entity = repository.getAuditLogForAdmin(logId);
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN04",
        "AUDIT_QUERY",
        "AUDIT_LOG",
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
        "admn04-service");
  }

  private Admn04AuditLogListItemDTO toListItem(Admn04AuditLogEntity entity) {
    return new Admn04AuditLogListItemDTO(
        entity.getLogId(),
        entity.getModuleCode(),
        repository.admn04ModuleName(entity.getModuleCode()),
        entity.getActionCode(),
        repository.admn04ActionName(entity.getActionCode()),
        entity.getTargetType(),
        entity.getTargetId(),
        entity.getOperator(),
        entity.getClientIp(),
        entity.getResult(),
        repository.admn04ResultText(entity.getResult()),
        entity.getSummary(),
        toText(entity.getOperateAt()));
  }

  private Admn04AuditLogDetailResponse toDetail(Admn04AuditLogEntity entity) {
    String actionCode = safeText(entity.getActionCode()).toUpperCase();
    String operatorRole =
        "SYSTEM".equalsIgnoreCase(safeText(entity.getOperatorType()))
            ? "SYSTEM"
            : repository.admn04OperatorRole(entity.getOperatorType());
    return new Admn04AuditLogDetailResponse(
        entity.getLogId(),
        entity.getModuleCode(),
        repository.admn04ModuleName(entity.getModuleCode()),
        entity.getActionCode(),
        repository.admn04ActionName(entity.getActionCode()),
        entity.getTargetType(),
        entity.getTargetId(),
        entity.getOperator(),
        operatorRole,
        requestMethodByAction(actionCode),
        requestPathByModule(entity.getModuleCode()),
        entity.getClientIp(),
        entity.getRequestId(),
        entity.getResult(),
        repository.admn04ResultText(entity.getResult()),
        entity.getSummary(),
        entity.getBeforeSnapshot(),
        entity.getAfterSnapshot(),
        repository.admn04Tags(entity.getModuleCode(), entity.getRiskLevel(), entity.getResult()),
        toText(entity.getOperateAt()));
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String requestPathByModule(String moduleCode) {
    return switch (safeText(moduleCode).toUpperCase()) {
      case "ADMN01" -> "/api/admin/merchant-certifications/{certificationId}/review";
      case "ADMN02" -> "/api/admin/buyers/{userId}/blacklist";
      case "ADMN03" -> "/api/admin/rbac/roles/{roleId}/permissions";
      case "ADMN04" -> "/api/admin/audit-logs";
      case "ADMN05" -> "/api/admin/category-spec-dicts";
      case "ADMN06" -> "/api/admin/lead-quality";
      case "ADMN08" -> "/api/admin/deal-funnel";
      case "ADMN09" -> "/api/admin/arbitration-tickets";
      case "ADMN10" -> "/api/admin/billing-rules";
      default -> "/api/admin/unknown";
    };
  }

  private String requestMethodByAction(String actionCode) {
    if (actionCode.contains("QUERY")) {
      return "GET";
    }
    if (actionCode.contains("UPSERT")) {
      return "POST";
    }
    if ("ARBITRATION_REVIEW".equals(actionCode)) {
      return "POST";
    }
    return "PUT";
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
