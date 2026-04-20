package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.List;

public class N13CreditScoreEntity {
  private final String scoreId;
  private final String userId;
  private final String merchantId;
  private final String merchantName;
  private final String scoreMonth;
  private final String totalScore;
  private final String grade;
  private final String rankPercent;
  private final String riskLevel;
  private final String riskSummary;
  private final String scoreVersion;
  private final String previousScore;
  private final String scoreChange;
  private final String changeTrend;
  private final String fulfillmentRate;
  private final String disputeRate;
  private final String avgResponseMinutes;
  private final String onTimePaymentRate;
  private final List<String> tags;
  private final List<String> suggestions;
  private final List<FactorItem> factors;
  private final List<TimelineItem> timeline;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public N13CreditScoreEntity(
      String scoreId,
      String userId,
      String merchantId,
      String merchantName,
      String scoreMonth,
      String totalScore,
      String grade,
      String rankPercent,
      String riskLevel,
      String riskSummary,
      String scoreVersion,
      String previousScore,
      String scoreChange,
      String changeTrend,
      String fulfillmentRate,
      String disputeRate,
      String avgResponseMinutes,
      String onTimePaymentRate,
      List<String> tags,
      List<String> suggestions,
      List<FactorItem> factors,
      List<TimelineItem> timeline,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.scoreId = scoreId;
    this.userId = userId;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.scoreMonth = scoreMonth;
    this.totalScore = totalScore;
    this.grade = grade;
    this.rankPercent = rankPercent;
    this.riskLevel = riskLevel;
    this.riskSummary = riskSummary;
    this.scoreVersion = scoreVersion;
    this.previousScore = previousScore;
    this.scoreChange = scoreChange;
    this.changeTrend = changeTrend;
    this.fulfillmentRate = fulfillmentRate;
    this.disputeRate = disputeRate;
    this.avgResponseMinutes = avgResponseMinutes;
    this.onTimePaymentRate = onTimePaymentRate;
    this.tags = tags;
    this.suggestions = suggestions;
    this.factors = factors;
    this.timeline = timeline;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getScoreId() {
    return scoreId;
  }

  public String getUserId() {
    return userId;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getScoreMonth() {
    return scoreMonth;
  }

  public String getTotalScore() {
    return totalScore;
  }

  public String getGrade() {
    return grade;
  }

  public String getRankPercent() {
    return rankPercent;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public String getRiskSummary() {
    return riskSummary;
  }

  public String getScoreVersion() {
    return scoreVersion;
  }

  public String getPreviousScore() {
    return previousScore;
  }

  public String getScoreChange() {
    return scoreChange;
  }

  public String getChangeTrend() {
    return changeTrend;
  }

  public String getFulfillmentRate() {
    return fulfillmentRate;
  }

  public String getDisputeRate() {
    return disputeRate;
  }

  public String getAvgResponseMinutes() {
    return avgResponseMinutes;
  }

  public String getOnTimePaymentRate() {
    return onTimePaymentRate;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public List<FactorItem> getFactors() {
    return factors;
  }

  public List<TimelineItem> getTimeline() {
    return timeline;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public static final class FactorItem {
    private final String factorCode;
    private final String factorName;
    private final int score;
    private final int weight;
    private final String weightedScore;
    private final String trend;
    private final String summary;
    private final String improvementTip;

    public FactorItem(
        String factorCode,
        String factorName,
        int score,
        int weight,
        String weightedScore,
        String trend,
        String summary,
        String improvementTip) {
      this.factorCode = factorCode;
      this.factorName = factorName;
      this.score = score;
      this.weight = weight;
      this.weightedScore = weightedScore;
      this.trend = trend;
      this.summary = summary;
      this.improvementTip = improvementTip;
    }

    public String getFactorCode() {
      return factorCode;
    }

    public String getFactorName() {
      return factorName;
    }

    public int getScore() {
      return score;
    }

    public int getWeight() {
      return weight;
    }

    public String getWeightedScore() {
      return weightedScore;
    }

    public String getTrend() {
      return trend;
    }

    public String getSummary() {
      return summary;
    }

    public String getImprovementTip() {
      return improvementTip;
    }
  }

  public static final class TimelineItem {
    private final String nodeCode;
    private final String nodeName;
    private final String impactScore;
    private final String impactDirection;
    private final String description;
    private final String happenedAt;

    public TimelineItem(
        String nodeCode,
        String nodeName,
        String impactScore,
        String impactDirection,
        String description,
        String happenedAt) {
      this.nodeCode = nodeCode;
      this.nodeName = nodeName;
      this.impactScore = impactScore;
      this.impactDirection = impactDirection;
      this.description = description;
      this.happenedAt = happenedAt;
    }

    public String getNodeCode() {
      return nodeCode;
    }

    public String getNodeName() {
      return nodeName;
    }

    public String getImpactScore() {
      return impactScore;
    }

    public String getImpactDirection() {
      return impactDirection;
    }

    public String getDescription() {
      return description;
    }

    public String getHappenedAt() {
      return happenedAt;
    }
  }
}
