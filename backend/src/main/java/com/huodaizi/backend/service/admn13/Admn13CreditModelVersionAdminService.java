package com.huodaizi.backend.service.admn13;

import com.huodaizi.backend.dto.admn13.Admn13CreditModelFactorWeightDTO;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionDetailResponse;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionListItemDTO;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionListRequest;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionListResponse;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn13CreditModelVersionEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class Admn13CreditModelVersionAdminService {
  private final InMemoryAuthRepository repository;

  public Admn13CreditModelVersionAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn13CreditModelVersionListResponse list(Admn13CreditModelVersionListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn13CreditModelVersionEntity> all =
        repository.listCreditModelVersionsForAdmin(
            request == null ? null : request.versionStatus(),
            null,
            request == null ? null : request.riskLevel(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn13CreditModelVersionListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();

    int onlineCount =
        (int)
            all.stream()
                .filter(item -> "ONLINE".equalsIgnoreCase(safeText(item.getVersionStatus())))
                .count();
    int draftCount =
        (int)
            all.stream()
                .filter(item -> "DRAFT".equalsIgnoreCase(safeText(item.getVersionStatus())))
                .count();
    int archivedCount =
        (int)
            all.stream()
                .filter(item -> "ARCHIVED".equalsIgnoreCase(safeText(item.getVersionStatus())))
                .count();

    return new Admn13CreditModelVersionListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.versionStatus()),
        request == null ? "" : safeText(request.riskLevel()),
        request == null ? "" : safeText(request.keyword()),
        onlineCount,
        draftCount,
        archivedCount,
        records);
  }

  public Admn13CreditModelVersionDetailResponse detail(String versionId) {
    return toDetail(repository.getCreditModelVersionForAdmin(versionId));
  }

  public Admn13CreditModelVersionDetailResponse upsert(Admn13CreditModelVersionUpsertRequest request) {
    List<Admn13CreditModelVersionEntity.FactorWeight> factorWeights =
        (request.factors() == null ? List.<Admn13CreditModelFactorWeightDTO>of() : request.factors())
            .stream()
            .map(
                factor ->
                    new Admn13CreditModelVersionEntity.FactorWeight(
                        safeText(factor.factorCode()),
                        safeText(factor.factorName()),
                        safeText(factor.weightPercent()),
                        safeText(factor.scoreCap())))
            .toList();

    String riskThresholdJson =
        "{\"pass\":\""
            + safeText(request.passThreshold())
            + "\",\"risk\":\""
            + safeText(request.riskThreshold())
            + "\"}";

    Admn13CreditModelVersionEntity entity =
        repository.upsertCreditModelVersionForAdmin(
            request.modelCode(),
            request.modelName(),
            request.versionNo(),
            request.versionStatus(),
            request.applicableScope(),
            request.effectiveFrom(),
            request.effectiveTo(),
            request.baseScore(),
            request.passThreshold(),
            request.riskThreshold(),
            factorWeights,
            request.remark(),
            request.operator());

    repository.appendAuditLogForAdmin(
        "ADMN13",
        "CREDIT_MODEL_VERSION_UPSERT",
        "CREDIT_MODEL_VERSION",
        entity.getVersionId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN13_UPSERT_" + entity.getVersionId(),
        "SUCCESS",
        repository.admn13AuditRiskLevel(entity.getVersionStatus(), entity.getScenarioCode()),
        "信用模型版本新增/更新：" + entity.getModelCode() + "-" + entity.getModelVersion(),
        "",
        "scenario=" + entity.getScenarioCode() + ", status=" + entity.getVersionStatus(),
        "127.0.0.1",
        "admn13-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN13",
        "CREDIT_MODEL_VERSION_QUERY",
        "CREDIT_MODEL_VERSION",
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
        "admn13-service");
  }

  private Admn13CreditModelVersionListItemDTO toListItem(Admn13CreditModelVersionEntity entity) {
    int sampleSize = 0;
    try {
      sampleSize = Integer.parseInt(safeText(entity.getSampleSize()));
    } catch (NumberFormatException ignored) {
      sampleSize = 0;
    }
    return new Admn13CreditModelVersionListItemDTO(
        entity.getVersionId(),
        entity.getModelVersion(),
        entity.getModelName(),
        entity.getVersionStatus(),
        repository.admn13ModelStatusText(entity.getVersionStatus()),
        entity.getScenarioCode(),
        releaseTypeText(entity.getScenarioCode()),
        entity.getScoreScale(),
        sampleSize,
        entity.getEffectiveFrom(),
        entity.getEffectiveTo(),
        toText(entity.getUpdatedAt()));
  }

  private Admn13CreditModelVersionDetailResponse toDetail(Admn13CreditModelVersionEntity entity) {
    List<Admn13CreditModelFactorWeightDTO> factors =
        entity.getFactorWeights().stream()
            .map(
                factor ->
                    new Admn13CreditModelFactorWeightDTO(
                        factor.factorCode(),
                        factor.factorName(),
                        factor.weightPercent(),
                        factor.impactDirection(),
                        "0"))
            .toList();
    return new Admn13CreditModelVersionDetailResponse(
        entity.getVersionId(),
        entity.getModelVersion(),
        entity.getModelName(),
        entity.getVersionStatus(),
        repository.admn13ModelStatusText(entity.getVersionStatus()),
        entity.getScenarioCode(),
        releaseTypeText(entity.getScenarioCode()),
        entity.getEffectiveFrom(),
        entity.getEffectiveTo(),
        entity.getRiskThresholdJson(),
        entity.getSampleSize(),
        "30",
        "10%",
        entity.getOwner(),
        "风控评审组",
        entity.getRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        factors,
        availableActions(entity.getVersionStatus()));
  }

  private String releaseTypeText(String scenarioCode) {
    return switch (safeText(scenarioCode).toUpperCase()) {
      case "MERCHANT" -> "商家评分";
      case "LEAD" -> "线索预估";
      case "BUYER" -> "买家信用";
      default -> "通用模型";
    };
  }

  private List<String> availableActions(String versionStatus) {
    return switch (safeText(versionStatus).toUpperCase()) {
      case "ONLINE" -> List.of("VIEW", "CLONE", "ARCHIVE");
      case "DRAFT" -> List.of("VIEW", "EDIT", "PUBLISH");
      case "ARCHIVED" -> List.of("VIEW", "CLONE");
      default -> List.of("VIEW", "EDIT");
    };
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
