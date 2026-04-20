package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryDispatchScoreRuleEntity {
  private final String sceneCode;
  private final String sceneName;
  private final String ruleVersion;
  private final String scoreFormula;
  private final String updateCycle;
  private final String paidFactorDesc;
  private final List<DimensionEntity> dimensions;
  private final List<BonusEntity> bonuses;
  private final List<PenaltyEntity> penalties;
  private final List<CaseEntity> cases;
  private final List<String> disclosures;
  private final LocalDateTime updatedAt;

  public InquiryDispatchScoreRuleEntity(
      String sceneCode,
      String sceneName,
      String ruleVersion,
      String scoreFormula,
      String updateCycle,
      String paidFactorDesc,
      List<DimensionEntity> dimensions,
      List<BonusEntity> bonuses,
      List<PenaltyEntity> penalties,
      List<CaseEntity> cases,
      List<String> disclosures,
      LocalDateTime updatedAt) {
    this.sceneCode = sceneCode;
    this.sceneName = sceneName;
    this.ruleVersion = ruleVersion;
    this.scoreFormula = scoreFormula;
    this.updateCycle = updateCycle;
    this.paidFactorDesc = paidFactorDesc;
    this.dimensions = dimensions;
    this.bonuses = bonuses;
    this.penalties = penalties;
    this.cases = cases;
    this.disclosures = disclosures;
    this.updatedAt = updatedAt;
  }

  public String getSceneCode() {
    return sceneCode;
  }

  public String getSceneName() {
    return sceneName;
  }

  public String getRuleVersion() {
    return ruleVersion;
  }

  public String getScoreFormula() {
    return scoreFormula;
  }

  public String getUpdateCycle() {
    return updateCycle;
  }

  public String getPaidFactorDesc() {
    return paidFactorDesc;
  }

  public List<DimensionEntity> getDimensions() {
    return dimensions;
  }

  public List<BonusEntity> getBonuses() {
    return bonuses;
  }

  public List<PenaltyEntity> getPenalties() {
    return penalties;
  }

  public List<CaseEntity> getCases() {
    return cases;
  }

  public List<String> getDisclosures() {
    return disclosures;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public record DimensionEntity(
      String code,
      String name,
      int weight,
      String desc,
      String scoreMethod,
      String dataSource) {}

  public record BonusEntity(String code, String name, String scoreChange, String trigger, String cap) {}

  public record PenaltyEntity(String code, String name, String scoreChange, String trigger, String recovery) {}

  public record CaseEntity(String merchantId, String merchantName, String level, String score, String explanation) {}
}
