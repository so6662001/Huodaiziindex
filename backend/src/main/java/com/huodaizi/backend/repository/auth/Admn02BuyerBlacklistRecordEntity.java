package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class Admn02BuyerBlacklistRecordEntity {
  private final String userId;
  private boolean blacklisted;
  private String reasonCode;
  private String remark;
  private String operator;
  private LocalDateTime operateAt;

  public Admn02BuyerBlacklistRecordEntity(
      String userId,
      boolean blacklisted,
      String reasonCode,
      String remark,
      String operator,
      LocalDateTime operateAt) {
    this.userId = userId;
    this.blacklisted = blacklisted;
    this.reasonCode = reasonCode;
    this.remark = remark;
    this.operator = operator;
    this.operateAt = operateAt;
  }

  public String getUserId() {
    return userId;
  }

  public boolean isBlacklisted() {
    return blacklisted;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public String getRemark() {
    return remark;
  }

  public String getOperator() {
    return operator;
  }

  public LocalDateTime getOperateAt() {
    return operateAt;
  }

  public void update(
      boolean newBlacklisted,
      String newReasonCode,
      String newRemark,
      String newOperator,
      LocalDateTime newOperateAt) {
    this.blacklisted = newBlacklisted;
    this.reasonCode = newReasonCode;
    this.remark = newRemark;
    this.operator = newOperator;
    this.operateAt = newOperateAt;
  }
}
