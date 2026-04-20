package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class Admn06LeadQualityEntity {
  private final String qualityId;
  private final String source;
  private final String leadId;
  private final String leadNo;
  private String qualityStatus;
  private int qualityScore;
  private String riskLevel;
  private String issueTags;
  private String reviewRemark;
  private String reviewer;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Admn06LeadQualityEntity(
      String qualityId,
      String source,
      String leadId,
      String leadNo,
      String qualityStatus,
      int qualityScore,
      String riskLevel,
      String issueTags,
      String reviewRemark,
      String reviewer,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.qualityId = qualityId;
    this.source = source;
    this.leadId = leadId;
    this.leadNo = leadNo;
    this.qualityStatus = qualityStatus;
    this.qualityScore = qualityScore;
    this.riskLevel = riskLevel;
    this.issueTags = issueTags;
    this.reviewRemark = reviewRemark;
    this.reviewer = reviewer;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getQualityId() {
    return qualityId;
  }

  public String getSource() {
    return source;
  }

  public String getLeadId() {
    return leadId;
  }

  public String getLeadNo() {
    return leadNo;
  }

  public String getQualityStatus() {
    return qualityStatus;
  }

  public int getQualityScore() {
    return qualityScore;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public String getIssueTags() {
    return issueTags;
  }

  public String getReviewRemark() {
    return reviewRemark;
  }

  public String getReviewer() {
    return reviewer;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void review(
      String newQualityStatus,
      int newQualityScore,
      String newRiskLevel,
      String newIssueTags,
      String newReviewRemark,
      String newReviewer,
      LocalDateTime now) {
    this.qualityStatus = newQualityStatus;
    this.qualityScore = newQualityScore;
    this.riskLevel = newRiskLevel;
    this.issueTags = newIssueTags;
    this.reviewRemark = newReviewRemark;
    this.reviewer = newReviewer;
    this.updatedAt = now;
  }
}
