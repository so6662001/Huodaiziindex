package com.huodaizi.backend.repository.dispatchstrategy;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyItemDTO;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListResponse;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest.A06BonusRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest.A06DimensionRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest.A06PenaltyRequest;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryDispatchScoreRuleEntity;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class A06DispatchStrategyRepository {
  private final InMemoryInquiryRepository inquiryRepository;

  public A06DispatchStrategyRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A06DispatchStrategyListResponse list(A06DispatchStrategyListRequest request) {
    String keyword = normalize(request.keyword());
    String sceneCode = normalize(request.sceneCode());
    List<InquiryDispatchScoreRuleEntity> all =
        inquiryRepository.allDispatchScoreRules().stream()
            .filter(item -> sceneCode == null || normalize(item.getSceneCode()).contains(sceneCode))
            .filter(item -> !request.safeEnabledOnly() || hasActiveRules(item))
            .filter(item -> keyword == null || containsKeyword(item, keyword))
            .sorted(
                Comparator.comparing(InquiryDispatchScoreRuleEntity::getUpdatedAt, Comparator.reverseOrder()))
            .toList();
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryDispatchScoreRuleEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);

    int total = all.size();
    int activeSceneCount = (int) all.stream().filter(this::hasActiveRules).count();
    int totalBonusCount = all.stream().mapToInt(item -> item.getBonuses().size()).sum();
    int totalPenaltyCount = all.stream().mapToInt(item -> item.getPenalties().size()).sum();
    int avgWeight =
        total <= 0 ? 0 : all.stream().mapToInt(this::sumDimensionWeight).sum() / total;
    return new A06DispatchStrategyListResponse(
        paged.stream().map(this::toItem).toList(),
        total,
        page,
        pageSize,
        activeSceneCount,
        totalBonusCount,
        totalPenaltyCount,
        String.valueOf(avgWeight),
        LocalDateTime.now().toString());
  }

  public A06DispatchStrategyItemDTO detail(String sceneCode) {
    InquiryDispatchScoreRuleEntity entity = getBySceneCode(sceneCode);
    return toItem(entity);
  }

  public A06DispatchStrategyItemDTO update(String sceneCode, A06DispatchStrategyUpdateRequest request) {
    InquiryDispatchScoreRuleEntity current = getBySceneCode(sceneCode);
    List<InquiryDispatchScoreRuleEntity.DimensionEntity> dimensions =
        request.dimensions().stream()
            .map(
                item ->
                    new InquiryDispatchScoreRuleEntity.DimensionEntity(
                        item.code().trim(),
                        item.name().trim(),
                        item.weight(),
                        defaultText(item.description(), "-"),
                        defaultText(item.scoreMethod(), "-"),
                        defaultText(item.dataSource(), "-")))
            .toList();
    int weightSum = dimensions.stream().mapToInt(InquiryDispatchScoreRuleEntity.DimensionEntity::weight).sum();
    if (weightSum != 100) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "维度权重之和必须为100");
    }
    List<InquiryDispatchScoreRuleEntity.BonusEntity> bonuses =
        request.bonuses().stream()
            .map(
                (A06BonusRequest item) ->
                    new InquiryDispatchScoreRuleEntity.BonusEntity(
                        item.code().trim(),
                        item.name().trim(),
                        item.scoreChange().trim(),
                        defaultText(item.trigger(), "-"),
                        defaultText(item.cap(), "-")))
            .toList();
    List<InquiryDispatchScoreRuleEntity.PenaltyEntity> penalties =
        request.penalties().stream()
            .map(
                (A06PenaltyRequest item) ->
                    new InquiryDispatchScoreRuleEntity.PenaltyEntity(
                        item.code().trim(),
                        item.name().trim(),
                        item.scoreChange().trim(),
                        defaultText(item.trigger(), "-"),
                        defaultText(item.recovery(), "-")))
            .toList();
    InquiryDispatchScoreRuleEntity updated =
        new InquiryDispatchScoreRuleEntity(
            current.getSceneCode(),
            request.sceneName().trim(),
            request.ruleVersion().trim(),
            request.scoreFormula().trim(),
            request.updateCycle().trim(),
            request.paidFactorDesc().trim(),
            dimensions,
            bonuses,
            penalties,
            current.getCases(),
            request.disclosures().stream().map(String::trim).filter(s -> !s.isBlank()).toList(),
            LocalDateTime.now());
    inquiryRepository.saveDispatchScoreRule(updated);
    return toItem(updated);
  }

  private InquiryDispatchScoreRuleEntity getBySceneCode(String sceneCode) {
    String normalized = defaultText(sceneCode, "").trim().toUpperCase(Locale.ROOT);
    return inquiryRepository.allDispatchScoreRules().stream()
        .filter(item -> item.getSceneCode().equalsIgnoreCase(normalized))
        .findFirst()
        .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND.getCode(), "分发策略场景不存在"));
  }

  private A06DispatchStrategyItemDTO toItem(InquiryDispatchScoreRuleEntity item) {
    return new A06DispatchStrategyItemDTO(
        item.getSceneCode(),
        item.getSceneName(),
        item.getRuleVersion(),
        item.getUpdateCycle(),
        item.getPaidFactorDesc(),
        item.getScoreFormula(),
        item.getDimensions().size(),
        item.getBonuses().size(),
        item.getPenalties().size(),
        item.getUpdatedAt().toString(),
        item.getDimensions().stream()
            .map(
                d ->
                    new com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyDimensionDTO(
                        d.code(), d.name(), d.weight(), d.desc(), d.scoreMethod(), d.dataSource()))
            .toList(),
        item.getBonuses().stream()
            .map(
                b ->
                    new com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyBonusDTO(
                        b.code(), b.name(), b.scoreChange(), b.trigger(), b.cap()))
            .toList(),
        item.getPenalties().stream()
            .map(
                p ->
                    new com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyPenaltyDTO(
                        p.code(), p.name(), p.scoreChange(), p.trigger(), p.recovery()))
            .toList());
  }

  private int sumDimensionWeight(InquiryDispatchScoreRuleEntity item) {
    return item.getDimensions().stream().mapToInt(InquiryDispatchScoreRuleEntity.DimensionEntity::weight).sum();
  }

  private boolean hasActiveRules(InquiryDispatchScoreRuleEntity item) {
    return !item.getDimensions().isEmpty()
        && item.getDimensions().stream().mapToInt(InquiryDispatchScoreRuleEntity.DimensionEntity::weight).sum() > 0;
  }

  private boolean containsKeyword(InquiryDispatchScoreRuleEntity item, String keyword) {
    return normalize(item.getSceneCode()).contains(keyword)
        || normalize(item.getSceneName()).contains(keyword)
        || normalize(item.getRuleVersion()).contains(keyword)
        || normalize(item.getScoreFormula()).contains(keyword);
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }
}
