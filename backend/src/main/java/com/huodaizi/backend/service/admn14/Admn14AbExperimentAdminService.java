package com.huodaizi.backend.service.admn14;

import com.huodaizi.backend.dto.admn14.Admn14AbExperimentDetailResponse;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentListItemDTO;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentListRequest;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentListResponse;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentMetricDTO;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn14AbExperimentEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn14AbExperimentAdminService {
  private final InMemoryAuthRepository repository;

  public Admn14AbExperimentAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn14AbExperimentListResponse list(Admn14AbExperimentListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn14AbExperimentEntity> all =
        repository.listAbExperimentsForAdmin(
            request == null ? null : request.experimentStatus(),
            request == null ? null : request.scenarioCode(),
            request == null ? null : request.optimizationStage(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn14AbExperimentListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int runningCount =
        (int)
            all.stream()
                .filter(item -> "RUNNING".equalsIgnoreCase(safeText(item.getExperimentStatus())))
                .count();
    int draftCount =
        (int)
            all.stream()
                .filter(item -> "DRAFT".equalsIgnoreCase(safeText(item.getExperimentStatus())))
                .count();
    int completedCount =
        (int)
            all.stream()
                .filter(item -> "COMPLETED".equalsIgnoreCase(safeText(item.getExperimentStatus())))
                .count();
    int pausedCount =
        (int)
            all.stream()
                .filter(item -> "PAUSED".equalsIgnoreCase(safeText(item.getExperimentStatus())))
                .count();
    return new Admn14AbExperimentListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.experimentStatus()),
        request == null ? "" : safeText(request.scenarioCode()),
        request == null ? "" : safeText(request.optimizationStage()),
        request == null ? "" : safeText(request.keyword()),
        runningCount,
        draftCount,
        completedCount,
        pausedCount,
        records);
  }

  public Admn14AbExperimentDetailResponse detail(String experimentId) {
    return toDetail(repository.getAbExperimentForAdmin(experimentId));
  }

  public Admn14AbExperimentDetailResponse upsert(Admn14AbExperimentUpsertRequest request) {
    List<Admn14AbExperimentEntity.MetricSnapshot> metrics =
        (request.metrics() == null ? List.<Admn14AbExperimentMetricDTO>of() : request.metrics()).stream()
            .map(
                metric ->
                    new Admn14AbExperimentEntity.MetricSnapshot(
                        safeText(metric.metricCode()),
                        safeText(metric.metricName()),
                        safeText(metric.controlValue()),
                        safeText(metric.variantValue()),
                        safeText(metric.upliftRate()),
                        safeText(metric.confidenceLevel())))
            .toList();
    Admn14AbExperimentEntity entity =
        repository.upsertAbExperimentForAdmin(
            request.experimentCode(),
            request.experimentName(),
            request.scenarioCode(),
            request.experimentStatus(),
            request.optimizationStage(),
            request.trafficPercent(),
            request.targetMetricCode(),
            request.baselineValue(),
            request.targetValue(),
            request.startDate(),
            request.endDate(),
            request.owner(),
            metrics,
            request.remark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN14",
        "AB_EXPERIMENT_UPSERT",
        "AB_EXPERIMENT",
        entity.getExperimentId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN14_UPSERT_" + entity.getExperimentId(),
        "SUCCESS",
        repository.admn14AuditRiskLevel(entity.getExperimentStatus(), entity.getOptimizationStage()),
        "A/B实验配置新增/更新：" + entity.getExperimentCode(),
        "",
        "scenario="
            + entity.getScenarioCode()
            + ", status="
            + entity.getExperimentStatus()
            + ", stage="
            + entity.getOptimizationStage(),
        "127.0.0.1",
        "admn14-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN14",
        "AB_EXPERIMENT_QUERY",
        "AB_EXPERIMENT",
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
        "admn14-service");
  }

  private Admn14AbExperimentListItemDTO toListItem(Admn14AbExperimentEntity entity) {
    return new Admn14AbExperimentListItemDTO(
        entity.getExperimentId(),
        entity.getExperimentCode(),
        entity.getExperimentName(),
        entity.getScenarioCode(),
        repository.admn14ScenarioText(entity.getScenarioCode()),
        entity.getExperimentStatus(),
        repository.admn14ExperimentStatusText(entity.getExperimentStatus()),
        entity.getOptimizationStage(),
        repository.admn14OptimizationStageText(entity.getOptimizationStage()),
        entity.getTrafficPercent(),
        entity.getTargetMetricCode(),
        entity.getExpectedGainRate(),
        entity.getConfidenceLevel(),
        safeText(entity.getOwner()),
        toText(entity.getUpdatedAt()));
  }

  private Admn14AbExperimentDetailResponse toDetail(Admn14AbExperimentEntity entity) {
    List<Admn14AbExperimentMetricDTO> metrics =
        entity.getMetricSnapshots().stream()
            .map(
                metric ->
                    new Admn14AbExperimentMetricDTO(
                        metric.metricCode(),
                        metric.metricName(),
                        metric.controlValue(),
                        metric.variantValue(),
                        metric.upliftRate(),
                        metric.confidenceLevel()))
            .toList();
    String trafficSplitPlan = buildTrafficSplitPlan(entity.getTrafficPercent());
    return new Admn14AbExperimentDetailResponse(
        entity.getExperimentId(),
        entity.getExperimentCode(),
        entity.getExperimentName(),
        entity.getExperimentStatus(),
        repository.admn14ExperimentStatusText(entity.getExperimentStatus()),
        entity.getScenarioCode(),
        repository.admn14ScenarioText(entity.getScenarioCode()),
        entity.getOptimizationStage(),
        repository.admn14OptimizationStageText(entity.getOptimizationStage()),
        entity.getTargetMetricCode(),
        entity.getBaselineValue(),
        entity.getTargetValue(),
        entity.getConfidenceLevel(),
        trafficSplitPlan,
        entity.getWinnerVariant(),
        entity.getExpectedGainRate(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getOwner(),
        entity.getReviewer(),
        entity.getRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        metrics,
        availableActions(entity.getExperimentStatus()));
  }

  private List<String> availableActions(String experimentStatus) {
    return switch (safeText(experimentStatus).toUpperCase(Locale.ROOT)) {
      case "RUNNING" -> List.of("VIEW", "EDIT", "PAUSE", "MARK_COMPLETED");
      case "PAUSED" -> List.of("VIEW", "EDIT", "RESUME");
      case "COMPLETED" -> List.of("VIEW", "CLONE");
      case "DRAFT" -> List.of("VIEW", "EDIT", "PUBLISH");
      default -> List.of("VIEW", "EDIT");
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }

  private String buildTrafficSplitPlan(String trafficPercent) {
    int control = parseIntSafe(trafficPercent, 50);
    int variant = Math.max(0, 100 - control);
    return "A:" + control + "% / B:" + variant + "%";
  }

  private int parseIntSafe(String value, int fallback) {
    String text = safeText(value);
    if (text.isBlank()) {
      return fallback;
    }
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException ex) {
      return fallback;
    }
  }
}
