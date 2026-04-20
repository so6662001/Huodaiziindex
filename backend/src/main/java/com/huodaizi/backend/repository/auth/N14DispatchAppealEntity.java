package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.List;

public class N14DispatchAppealEntity {
  private final String appealId;
  private final String userId;
  private final String merchantId;
  private final String merchantName;
  private final String sceneCode;
  private final String sceneName;
  private final String scoreVersion;
  private final String targetObjectId;
  private final String targetObjectType;
  private final String appealType;
  private final String appealTypeText;
  private final String title;
  private final String description;
  private final String evidenceFiles;
  private String status;
  private String statusText;
  private String latestRemark;
  private final List<TimelineItem> timeline;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public N14DispatchAppealEntity(
      String appealId,
      String userId,
      String merchantId,
      String merchantName,
      String sceneCode,
      String sceneName,
      String scoreVersion,
      String targetObjectId,
      String targetObjectType,
      String appealType,
      String appealTypeText,
      String title,
      String description,
      String evidenceFiles,
      String status,
      String statusText,
      String latestRemark,
      List<TimelineItem> timeline,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.appealId = appealId;
    this.userId = userId;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.sceneCode = sceneCode;
    this.sceneName = sceneName;
    this.scoreVersion = scoreVersion;
    this.targetObjectId = targetObjectId;
    this.targetObjectType = targetObjectType;
    this.appealType = appealType;
    this.appealTypeText = appealTypeText;
    this.title = title;
    this.description = description;
    this.evidenceFiles = evidenceFiles;
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = latestRemark;
    this.timeline = timeline;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getAppealId() {
    return appealId;
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

  public String getSceneCode() {
    return sceneCode;
  }

  public String getSceneName() {
    return sceneName;
  }

  public String getScoreVersion() {
    return scoreVersion;
  }

  public String getTargetObjectId() {
    return targetObjectId;
  }

  public String getTargetObjectType() {
    return targetObjectType;
  }

  public String getAppealType() {
    return appealType;
  }

  public String getAppealTypeText() {
    return appealTypeText;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getEvidenceFiles() {
    return evidenceFiles;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusText() {
    return statusText;
  }

  public String getLatestRemark() {
    return latestRemark;
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

  public void updateStatus(
      String status, String statusText, String operator, String remark, LocalDateTime now) {
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = remark;
    this.updatedAt = now;
    this.timeline.add(
        new TimelineItem(
            status,
            statusText,
            operator,
            remark,
            now.toString()));
  }

  public static final class TimelineItem {
    private final String nodeCode;
    private final String nodeName;
    private final String operator;
    private final String remark;
    private final String happenedAt;

    public TimelineItem(
        String nodeCode,
        String nodeName,
        String operator,
        String remark,
        String happenedAt) {
      this.nodeCode = nodeCode;
      this.nodeName = nodeName;
      this.operator = operator;
      this.remark = remark;
      this.happenedAt = happenedAt;
    }

    public String getNodeCode() {
      return nodeCode;
    }

    public String getNodeName() {
      return nodeName;
    }

    public String getOperator() {
      return operator;
    }

    public String getRemark() {
      return remark;
    }

    public String getHappenedAt() {
      return happenedAt;
    }
  }
}
