package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryMerchantCreditScoreEntity {
  private final String merchantId;
  private final String merchantName;
  private final String score;
  private final String grade;
  private final String rankPercent;
  private final String scoreVersion;
  private final List<DimensionEntity> dimensions;
  private final List<TrendPointEntity> trendPoints;
  private final List<String> risks;
  private final List<String> suggestions;
  private final LocalDateTime updatedAt;

  public InquiryMerchantCreditScoreEntity(
      String merchantId,
      String merchantName,
      String score,
      String grade,
      String rankPercent,
      String scoreVersion,
      List<DimensionEntity> dimensions,
      List<TrendPointEntity> trendPoints,
      List<String> risks,
      List<String> suggestions,
      LocalDateTime updatedAt) {
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.score = score;
    this.grade = grade;
    this.rankPercent = rankPercent;
    this.scoreVersion = scoreVersion;
    this.dimensions = dimensions;
    this.trendPoints = trendPoints;
    this.risks = risks;
    this.suggestions = suggestions;
    this.updatedAt = updatedAt;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getScore() {
    return score;
  }

  public String getGrade() {
    return grade;
  }

  public String getRankPercent() {
    return rankPercent;
  }

  public String getScoreVersion() {
    return scoreVersion;
  }

  public List<DimensionEntity> getDimensions() {
    return dimensions;
  }

  public List<TrendPointEntity> getTrendPoints() {
    return trendPoints;
  }

  public List<String> getRisks() {
    return risks;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public record DimensionEntity(
      String code,
      String name,
      int score,
      int weight,
      String trend,
      String summary) {}

  public record TrendPointEntity(
      String month, String creditScore, String fulfillmentRate, String disputeRate, String responseMinutes) {}
}
