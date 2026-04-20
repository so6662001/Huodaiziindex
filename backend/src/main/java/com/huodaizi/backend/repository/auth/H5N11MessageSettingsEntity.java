package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class H5N11MessageSettingsEntity {
  private final String settingId;
  private final String userId;
  private final String contactMobileMasked;
  private final boolean globalPushEnabled;
  private final boolean appPushEnabled;
  private final boolean smsPushEnabled;
  private final boolean marketingEnabled;
  private final boolean transactionEnabled;
  private final boolean riskEnabled;
  private final boolean doNotDisturbEnabled;
  private final String doNotDisturbStart;
  private final String doNotDisturbEnd;
  private final String latestRemark;
  private final String channel;
  private final String updatedBy;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public H5N11MessageSettingsEntity(
      String settingId,
      String userId,
      String contactMobileMasked,
      boolean globalPushEnabled,
      boolean appPushEnabled,
      boolean smsPushEnabled,
      boolean marketingEnabled,
      boolean transactionEnabled,
      boolean riskEnabled,
      boolean doNotDisturbEnabled,
      String doNotDisturbStart,
      String doNotDisturbEnd,
      String latestRemark,
      String channel,
      String updatedBy,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.settingId = settingId;
    this.userId = userId;
    this.contactMobileMasked = contactMobileMasked;
    this.globalPushEnabled = globalPushEnabled;
    this.appPushEnabled = appPushEnabled;
    this.smsPushEnabled = smsPushEnabled;
    this.marketingEnabled = marketingEnabled;
    this.transactionEnabled = transactionEnabled;
    this.riskEnabled = riskEnabled;
    this.doNotDisturbEnabled = doNotDisturbEnabled;
    this.doNotDisturbStart = doNotDisturbStart;
    this.doNotDisturbEnd = doNotDisturbEnd;
    this.latestRemark = latestRemark;
    this.channel = channel;
    this.updatedBy = updatedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getSettingId() {
    return settingId;
  }

  public String getUserId() {
    return userId;
  }

  public String getContactMobileMasked() {
    return contactMobileMasked;
  }

  public boolean isGlobalPushEnabled() {
    return globalPushEnabled;
  }

  public boolean isAppPushEnabled() {
    return appPushEnabled;
  }

  public boolean isSmsPushEnabled() {
    return smsPushEnabled;
  }

  public boolean isMarketingEnabled() {
    return marketingEnabled;
  }

  public boolean isTransactionEnabled() {
    return transactionEnabled;
  }

  public boolean isRiskEnabled() {
    return riskEnabled;
  }

  public boolean isDoNotDisturbEnabled() {
    return doNotDisturbEnabled;
  }

  public String getDoNotDisturbStart() {
    return doNotDisturbStart;
  }

  public String getDoNotDisturbEnd() {
    return doNotDisturbEnd;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public String getChannel() {
    return channel;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
