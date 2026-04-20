package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn15RiskAlertTicketEntity {
  private final String ticketId;
  private String ticketNo;
  private String sourceType;
  private String bizNo;
  private String merchantId;
  private String merchantName;
  private String city;
  private String severity;
  private String riskCode;
  private String riskTitle;
  private String riskDetail;
  private String riskScore;
  private String suggestedAction;
  private String ticketStatus;
  private String owner;
  private String latestRemark;
  private String followUpPlan;
  private String contactMobileMasked;
  private String sourceCreatedAt;
  private String sourceUpdatedAt;
  private String handledAt;
  private String handler;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<ProgressNode> progressNodes;

  public Admn15RiskAlertTicketEntity(
      String ticketId,
      String ticketNo,
      String sourceType,
      String bizNo,
      String merchantId,
      String merchantName,
      String city,
      String severity,
      String riskCode,
      String riskTitle,
      String riskDetail,
      String riskScore,
      String suggestedAction,
      String ticketStatus,
      String owner,
      String latestRemark,
      String followUpPlan,
      String contactMobileMasked,
      String sourceCreatedAt,
      String sourceUpdatedAt,
      String handledAt,
      String handler,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.ticketId = ticketId;
    this.ticketNo = ticketNo;
    this.sourceType = sourceType;
    this.bizNo = bizNo;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.city = city;
    this.severity = severity;
    this.riskCode = riskCode;
    this.riskTitle = riskTitle;
    this.riskDetail = riskDetail;
    this.riskScore = riskScore;
    this.suggestedAction = suggestedAction;
    this.ticketStatus = ticketStatus;
    this.owner = owner;
    this.latestRemark = latestRemark;
    this.followUpPlan = followUpPlan;
    this.contactMobileMasked = contactMobileMasked;
    this.sourceCreatedAt = sourceCreatedAt;
    this.sourceUpdatedAt = sourceUpdatedAt;
    this.handledAt = handledAt;
    this.handler = handler;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.progressNodes = new ArrayList<>();
  }

  public String getTicketId() {
    return ticketId;
  }

  public String getTicketNo() {
    return ticketNo;
  }

  public String getSourceType() {
    return sourceType;
  }

  public String getBizNo() {
    return bizNo;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getCity() {
    return city;
  }

  public String getSeverity() {
    return severity;
  }

  public String getRiskCode() {
    return riskCode;
  }

  public String getRiskTitle() {
    return riskTitle;
  }

  public String getRiskDetail() {
    return riskDetail;
  }

  public String getRiskScore() {
    return riskScore;
  }

  public String getSuggestedAction() {
    return suggestedAction;
  }

  public String getTicketStatus() {
    return ticketStatus;
  }

  public String getOwner() {
    return owner;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public String getFollowUpPlan() {
    return followUpPlan;
  }

  public String getContactMobileMasked() {
    return contactMobileMasked;
  }

  public String getSourceCreatedAt() {
    return sourceCreatedAt;
  }

  public String getSourceUpdatedAt() {
    return sourceUpdatedAt;
  }

  public String getHandledAt() {
    return handledAt;
  }

  public String getHandler() {
    return handler;
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

  public List<ProgressNode> getProgressNodes() {
    return List.copyOf(progressNodes);
  }

  public void handle(
      String ticketStatus,
      String owner,
      String latestRemark,
      String followUpPlan,
      String handledAt,
      String handler,
      String operator,
      LocalDateTime now) {
    this.ticketStatus = ticketStatus;
    this.owner = owner;
    this.latestRemark = latestRemark;
    this.followUpPlan = followUpPlan;
    this.handledAt = handledAt;
    this.handler = handler;
    this.operator = operator;
    this.updatedAt = now;
  }

  public void appendProgress(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {
    this.progressNodes.add(
        new ProgressNode(nodeCode, nodeName, status, statusText, handler, remark, happenedAt));
  }

  public record ProgressNode(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {}
}
