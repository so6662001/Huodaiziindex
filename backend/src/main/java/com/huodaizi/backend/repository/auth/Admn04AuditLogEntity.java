package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class Admn04AuditLogEntity {
  private final String logId;
  private final String moduleCode;
  private final String actionCode;
  private final String targetType;
  private final String targetId;
  private final String operator;
  private final String operatorType;
  private final String requestId;
  private final String result;
  private final String riskLevel;
  private final String summary;
  private final String beforeSnapshot;
  private final String afterSnapshot;
  private final String clientIp;
  private final String userAgent;
  private final LocalDateTime operateAt;

  public Admn04AuditLogEntity(
      String logId,
      String moduleCode,
      String actionCode,
      String targetType,
      String targetId,
      String operator,
      String operatorType,
      String requestId,
      String result,
      String riskLevel,
      String summary,
      String beforeSnapshot,
      String afterSnapshot,
      String clientIp,
      String userAgent,
      LocalDateTime operateAt) {
    this.logId = logId;
    this.moduleCode = moduleCode;
    this.actionCode = actionCode;
    this.targetType = targetType;
    this.targetId = targetId;
    this.operator = operator;
    this.operatorType = operatorType;
    this.requestId = requestId;
    this.result = result;
    this.riskLevel = riskLevel;
    this.summary = summary;
    this.beforeSnapshot = beforeSnapshot;
    this.afterSnapshot = afterSnapshot;
    this.clientIp = clientIp;
    this.userAgent = userAgent;
    this.operateAt = operateAt;
  }

  public String getLogId() {
    return logId;
  }

  public String getModuleCode() {
    return moduleCode;
  }

  public String getActionCode() {
    return actionCode;
  }

  public String getTargetType() {
    return targetType;
  }

  public String getTargetId() {
    return targetId;
  }

  public String getOperator() {
    return operator;
  }

  public String getOperatorType() {
    return operatorType;
  }

  public String getRequestId() {
    return requestId;
  }

  public String getResult() {
    return result;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public String getSummary() {
    return summary;
  }

  public String getBeforeSnapshot() {
    return beforeSnapshot;
  }

  public String getAfterSnapshot() {
    return afterSnapshot;
  }

  public String getClientIp() {
    return clientIp;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public LocalDateTime getOperateAt() {
    return operateAt;
  }
}
