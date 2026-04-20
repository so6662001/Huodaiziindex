package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn13CreditModelVersionEntity {
  private final String versionId;
  private String modelCode;
  private String modelName;
  private String modelVersion;
  private String versionStatus;
  private String scenarioCode;
  private String scoreScale;
  private String riskThresholdJson;
  private String effectiveFrom;
  private String effectiveTo;
  private String sampleSize;
  private String hitRate;
  private String ksValue;
  private String aucValue;
  private String owner;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<FactorWeight> factorWeights;

  public Admn13CreditModelVersionEntity(
      String versionId,
      String modelCode,
      String modelName,
      String modelVersion,
      String versionStatus,
      String scenarioCode,
      String scoreScale,
      String riskThresholdJson,
      String effectiveFrom,
      String effectiveTo,
      String sampleSize,
      String hitRate,
      String ksValue,
      String aucValue,
      String owner,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<FactorWeight> factorWeights) {
    this.versionId = versionId;
    this.modelCode = modelCode;
    this.modelName = modelName;
    this.modelVersion = modelVersion;
    this.versionStatus = versionStatus;
    this.scenarioCode = scenarioCode;
    this.scoreScale = scoreScale;
    this.riskThresholdJson = riskThresholdJson;
    this.effectiveFrom = effectiveFrom;
    this.effectiveTo = effectiveTo;
    this.sampleSize = sampleSize;
    this.hitRate = hitRate;
    this.ksValue = ksValue;
    this.aucValue = aucValue;
    this.owner = owner;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.factorWeights = factorWeights == null ? new ArrayList<>() : new ArrayList<>(factorWeights);
  }

  public String getVersionId() {
    return versionId;
  }

  public String getModelCode() {
    return modelCode;
  }

  public String getModelName() {
    return modelName;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public String getVersionStatus() {
    return versionStatus;
  }

  public String getScenarioCode() {
    return scenarioCode;
  }

  public String getScoreScale() {
    return scoreScale;
  }

  public String getRiskThresholdJson() {
    return riskThresholdJson;
  }

  public String getEffectiveFrom() {
    return effectiveFrom;
  }

  public String getEffectiveTo() {
    return effectiveTo;
  }

  public String getSampleSize() {
    return sampleSize;
  }

  public String getHitRate() {
    return hitRate;
  }

  public String getKsValue() {
    return ksValue;
  }

  public String getAucValue() {
    return aucValue;
  }

  public String getOwner() {
    return owner;
  }

  public String getRemark() {
    return remark;
  }

  public String getOperator() {
    return operator;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<FactorWeight> getFactorWeights() {
    return List.copyOf(factorWeights);
  }

  public void update(
      String modelName,
      String versionStatus,
      String scenarioCode,
      String scoreScale,
      String riskThresholdJson,
      String effectiveFrom,
      String effectiveTo,
      String sampleSize,
      String hitRate,
      String ksValue,
      String aucValue,
      String owner,
      String remark,
      String operator,
      List<FactorWeight> factorWeights,
      LocalDateTime now) {
    this.modelName = modelName;
    this.versionStatus = versionStatus;
    this.scenarioCode = scenarioCode;
    this.scoreScale = scoreScale;
    this.riskThresholdJson = riskThresholdJson;
    this.effectiveFrom = effectiveFrom;
    this.effectiveTo = effectiveTo;
    this.sampleSize = sampleSize;
    this.hitRate = hitRate;
    this.ksValue = ksValue;
    this.aucValue = aucValue;
    this.owner = owner;
    this.remark = remark;
    this.operator = operator;
    this.updatedAt = now;
    this.factorWeights.clear();
    if (factorWeights != null) {
      this.factorWeights.addAll(factorWeights);
    }
  }

  public record FactorWeight(String factorCode, String factorName, String weightPercent, String impactDirection) {}
}
