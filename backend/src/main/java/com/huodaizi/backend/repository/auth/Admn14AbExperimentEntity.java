package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn14AbExperimentEntity {
  private final String experimentId;
  private String experimentCode;
  private String experimentName;
  private String scenarioCode;
  private String optimizationStage;
  private String experimentStatus;
  private String trafficPercent;
  private String targetMetricCode;
  private String baselineValue;
  private String targetValue;
  private String confidenceLevel;
  private String winnerVariant;
  private String expectedGainRate;
  private String startDate;
  private String endDate;
  private String owner;
  private String reviewer;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<MetricSnapshot> metricSnapshots;

  public Admn14AbExperimentEntity(
      String experimentId,
      String experimentCode,
      String experimentName,
      String scenarioCode,
      String optimizationStage,
      String experimentStatus,
      String trafficPercent,
      String targetMetricCode,
      String baselineValue,
      String targetValue,
      String confidenceLevel,
      String winnerVariant,
      String expectedGainRate,
      String startDate,
      String endDate,
      String owner,
      String reviewer,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<MetricSnapshot> metricSnapshots) {
    this.experimentId = experimentId;
    this.experimentCode = experimentCode;
    this.experimentName = experimentName;
    this.scenarioCode = scenarioCode;
    this.optimizationStage = optimizationStage;
    this.experimentStatus = experimentStatus;
    this.trafficPercent = trafficPercent;
    this.targetMetricCode = targetMetricCode;
    this.baselineValue = baselineValue;
    this.targetValue = targetValue;
    this.confidenceLevel = confidenceLevel;
    this.winnerVariant = winnerVariant;
    this.expectedGainRate = expectedGainRate;
    this.startDate = startDate;
    this.endDate = endDate;
    this.owner = owner;
    this.reviewer = reviewer;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.metricSnapshots =
        metricSnapshots == null ? new ArrayList<>() : new ArrayList<>(metricSnapshots);
  }

  public String getExperimentId() {
    return experimentId;
  }

  public String getExperimentCode() {
    return experimentCode;
  }

  public String getExperimentName() {
    return experimentName;
  }

  public String getScenarioCode() {
    return scenarioCode;
  }

  public String getOptimizationStage() {
    return optimizationStage;
  }

  public String getExperimentStatus() {
    return experimentStatus;
  }

  public String getTrafficPercent() {
    return trafficPercent;
  }

  public String getTargetMetricCode() {
    return targetMetricCode;
  }

  public String getBaselineValue() {
    return baselineValue;
  }

  public String getTargetValue() {
    return targetValue;
  }

  public String getConfidenceLevel() {
    return confidenceLevel;
  }

  public String getWinnerVariant() {
    return winnerVariant;
  }

  public String getExpectedGainRate() {
    return expectedGainRate;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getOwner() {
    return owner;
  }

  public String getReviewer() {
    return reviewer;
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

  public List<MetricSnapshot> getMetricSnapshots() {
    return List.copyOf(metricSnapshots);
  }

  public void update(
      String experimentName,
      String scenarioCode,
      String optimizationStage,
      String experimentStatus,
      String trafficPercent,
      String targetMetricCode,
      String baselineValue,
      String targetValue,
      String confidenceLevel,
      String winnerVariant,
      String expectedGainRate,
      String startDate,
      String endDate,
      String owner,
      String reviewer,
      String remark,
      String operator,
      List<MetricSnapshot> snapshots,
      LocalDateTime now) {
    this.experimentName = experimentName;
    this.scenarioCode = scenarioCode;
    this.optimizationStage = optimizationStage;
    this.experimentStatus = experimentStatus;
    this.trafficPercent = trafficPercent;
    this.targetMetricCode = targetMetricCode;
    this.baselineValue = baselineValue;
    this.targetValue = targetValue;
    this.confidenceLevel = confidenceLevel;
    this.winnerVariant = winnerVariant;
    this.expectedGainRate = expectedGainRate;
    this.startDate = startDate;
    this.endDate = endDate;
    this.owner = owner;
    this.reviewer = reviewer;
    this.remark = remark;
    this.operator = operator;
    this.updatedAt = now;
    this.metricSnapshots.clear();
    if (snapshots != null) {
      this.metricSnapshots.addAll(snapshots);
    }
  }

  public record MetricSnapshot(
      String metricCode,
      String metricName,
      String controlValue,
      String variantValue,
      String upliftRate,
      String confidenceLevel) {}
}
