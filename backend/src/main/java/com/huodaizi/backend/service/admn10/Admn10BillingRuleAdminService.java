package com.huodaizi.backend.service.admn10;

import com.huodaizi.backend.dto.admn10.Admn10BillingRuleDetailResponse;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleListItemDTO;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleListRequest;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleListResponse;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleStepDTO;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn10BillingRuleEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn10BillingRuleAdminService {
  private final InMemoryAuthRepository repository;

  public Admn10BillingRuleAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn10BillingRuleListResponse list(Admn10BillingRuleListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn10BillingRuleEntity> all =
        repository.listBillingRulesForAdmin(
            request == null ? null : request.ruleStatus(),
            request == null ? null : request.sceneCode(),
            request == null ? null : request.billingMode(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn10BillingRuleListItemDTO> records = all.subList(from, to).stream().map(this::toListItem).toList();
    int activeCount =
        (int) all.stream().filter(item -> "ACTIVE".equalsIgnoreCase(safeText(item.getRuleStatus()))).count();
    int draftCount =
        (int) all.stream().filter(item -> "DRAFT".equalsIgnoreCase(safeText(item.getRuleStatus()))).count();
    return new Admn10BillingRuleListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.ruleStatus()),
        request == null ? "" : safeText(request.sceneCode()),
        request == null ? "" : safeText(request.billingMode()),
        request == null ? "" : safeText(request.keyword()),
        activeCount,
        all.size() - activeCount - draftCount,
        draftCount,
        records);
  }

  public Admn10BillingRuleDetailResponse detail(String ruleId) {
    return toDetail(repository.getBillingRuleForAdmin(ruleId));
  }

  public Admn10BillingRuleDetailResponse upsert(Admn10BillingRuleUpsertRequest request) {
    Admn10BillingRuleEntity entity =
        repository.upsertBillingRuleForAdmin(
            request.ruleCode(),
            request.ruleName(),
            request.sceneCode(),
            request.billingMode(),
            request.feeCurrency(),
            request.basePriceYuan(),
            request.minFeeYuan(),
            request.maxFeeYuan(),
            request.ladderConfig(),
            request.effectiveFrom(),
            request.effectiveTo(),
            request.ruleStatus(),
            request.remark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN10",
        "BILLING_RULE_UPSERT",
        "BILLING_RULE",
        entity.getRuleId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN10_UPSERT_" + entity.getRuleId(),
        "SUCCESS",
        "ACTIVE".equalsIgnoreCase(entity.getRuleStatus()) ? "MEDIUM" : "LOW",
        "计费规则新增/更新：" + entity.getRuleCode(),
        "",
        "sceneCode="
            + entity.getSceneCode()
            + ", mode="
            + entity.getBillingMode()
            + ", status="
            + entity.getRuleStatus(),
        "127.0.0.1",
        "admn10-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN10",
        "BILLING_RULE_QUERY",
        "BILLING_RULE",
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
        "admn10-service");
  }

  private Admn10BillingRuleListItemDTO toListItem(Admn10BillingRuleEntity entity) {
    return new Admn10BillingRuleListItemDTO(
        entity.getRuleId(),
        entity.getRuleCode(),
        entity.getRuleName(),
        entity.getSceneCode(),
        repository.admn10SceneText(entity.getSceneCode()),
        entity.getBillingMode(),
        repository.admn10BillingModeText(entity.getBillingMode()),
        entity.getFeeCurrency(),
        entity.getBasePriceYuan(),
        entity.getMinFeeYuan(),
        entity.getMaxFeeYuan(),
        entity.getRuleStatus(),
        repository.admn10RuleStatusText(entity.getRuleStatus()),
        entity.getEffectiveFrom(),
        entity.getEffectiveTo(),
        safeText(entity.getOperator()),
        toText(entity.getUpdatedAt()));
  }

  private Admn10BillingRuleDetailResponse toDetail(Admn10BillingRuleEntity entity) {
    return new Admn10BillingRuleDetailResponse(
        entity.getRuleId(),
        entity.getRuleCode(),
        entity.getRuleName(),
        entity.getSceneCode(),
        repository.admn10SceneText(entity.getSceneCode()),
        entity.getBillingMode(),
        repository.admn10BillingModeText(entity.getBillingMode()),
        entity.getFeeCurrency(),
        entity.getBasePriceYuan(),
        entity.getMinFeeYuan(),
        entity.getMaxFeeYuan(),
        entity.getLadderConfig(),
        entity.getRuleStatus(),
        repository.admn10RuleStatusText(entity.getRuleStatus()),
        entity.getEffectiveFrom(),
        entity.getEffectiveTo(),
        safeText(entity.getRemark()),
        safeText(entity.getOperator()),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        toRuleSteps(entity.getBillingMode(), entity.getLadderConfig(), entity.getBasePriceYuan()),
        availableActions(entity.getRuleStatus()));
  }

  private List<Admn10BillingRuleStepDTO> toRuleSteps(String billingMode, String ladderConfig, String basePriceYuan) {
    String mode = safeText(billingMode).toUpperCase(Locale.ROOT);
    if ("FIXED".equals(mode)) {
      return List.of(new Admn10BillingRuleStepDTO(1, "固定费用", "每单固定收取", basePriceYuan, "CNY"));
    }
    if ("RATIO".equals(mode)) {
      return List.of(new Admn10BillingRuleStepDTO(1, "比例费率", "按成交额比例计费", basePriceYuan, "PERCENT"));
    }
    String text = safeText(ladderConfig);
    if (text.isBlank()) {
      return List.of();
    }
    String[] rows = text.split(";");
    java.util.ArrayList<Admn10BillingRuleStepDTO> steps = new java.util.ArrayList<>();
    int stepNo = 1;
    for (String row : rows) {
      String trimmed = row == null ? "" : row.trim();
      if (trimmed.isBlank()) {
        continue;
      }
      String[] cols = trimmed.split(":");
      if (cols.length < 2) {
        continue;
      }
      steps.add(new Admn10BillingRuleStepDTO(stepNo++, cols[0], "阶梯计费段", cols[1], "CNY"));
    }
    return steps;
  }

  private List<String> availableActions(String ruleStatus) {
    return switch (safeText(ruleStatus).toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> List.of("VIEW", "EDIT", "DISABLE");
      case "DISABLED" -> List.of("VIEW", "EDIT", "ENABLE");
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
}
