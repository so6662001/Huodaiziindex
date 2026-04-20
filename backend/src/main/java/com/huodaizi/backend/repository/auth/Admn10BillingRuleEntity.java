package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class Admn10BillingRuleEntity {
  private final String ruleId;
  private String ruleCode;
  private String ruleName;
  private String sceneCode;
  private String billingMode;
  private String feeCurrency;
  private String basePriceYuan;
  private String minFeeYuan;
  private String maxFeeYuan;
  private String ladderConfig;
  private String effectiveFrom;
  private String effectiveTo;
  private String ruleStatus;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Admn10BillingRuleEntity(
      String ruleId,
      String ruleCode,
      String ruleName,
      String sceneCode,
      String billingMode,
      String feeCurrency,
      String basePriceYuan,
      String minFeeYuan,
      String maxFeeYuan,
      String ladderConfig,
      String effectiveFrom,
      String effectiveTo,
      String ruleStatus,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.ruleId = ruleId;
    this.ruleCode = ruleCode;
    this.ruleName = ruleName;
    this.sceneCode = sceneCode;
    this.billingMode = billingMode;
    this.feeCurrency = feeCurrency;
    this.basePriceYuan = basePriceYuan;
    this.minFeeYuan = minFeeYuan;
    this.maxFeeYuan = maxFeeYuan;
    this.ladderConfig = ladderConfig;
    this.effectiveFrom = effectiveFrom;
    this.effectiveTo = effectiveTo;
    this.ruleStatus = ruleStatus;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getRuleId() {
    return ruleId;
  }

  public String getRuleCode() {
    return ruleCode;
  }

  public String getRuleName() {
    return ruleName;
  }

  public String getSceneCode() {
    return sceneCode;
  }

  public String getBillingMode() {
    return billingMode;
  }

  public String getFeeCurrency() {
    return feeCurrency;
  }

  public String getBasePriceYuan() {
    return basePriceYuan;
  }

  public String getMinFeeYuan() {
    return minFeeYuan;
  }

  public String getMaxFeeYuan() {
    return maxFeeYuan;
  }

  public String getLadderConfig() {
    return ladderConfig;
  }

  public String getEffectiveFrom() {
    return effectiveFrom;
  }

  public String getEffectiveTo() {
    return effectiveTo;
  }

  public String getRuleStatus() {
    return ruleStatus;
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

  public void update(
      String newRuleCode,
      String newRuleName,
      String newSceneCode,
      String newBillingMode,
      String newFeeCurrency,
      String newBasePriceYuan,
      String newMinFeeYuan,
      String newMaxFeeYuan,
      String newLadderConfig,
      String newEffectiveFrom,
      String newEffectiveTo,
      String newRuleStatus,
      String newRemark,
      String newOperator,
      LocalDateTime now) {
    this.ruleCode = newRuleCode;
    this.ruleName = newRuleName;
    this.sceneCode = newSceneCode;
    this.billingMode = newBillingMode;
    this.feeCurrency = newFeeCurrency;
    this.basePriceYuan = newBasePriceYuan;
    this.minFeeYuan = newMinFeeYuan;
    this.maxFeeYuan = newMaxFeeYuan;
    this.ladderConfig = newLadderConfig;
    this.effectiveFrom = newEffectiveFrom;
    this.effectiveTo = newEffectiveTo;
    this.ruleStatus = newRuleStatus;
    this.remark = newRemark;
    this.operator = newOperator;
    this.updatedAt = now;
  }
}
