package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class N12InvoiceTitleEntity {
  private final String titleId;
  private final String userId;
  private String titleType;
  private String titleTypeText;
  private String titleName;
  private String taxNo;
  private String bankName;
  private String bankAccountNo;
  private String registeredAddress;
  private String registeredPhone;
  private String email;
  private boolean defaultTitle;
  private String status;
  private String statusText;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public N12InvoiceTitleEntity(
      String titleId,
      String userId,
      String titleType,
      String titleTypeText,
      String titleName,
      String taxNo,
      String bankName,
      String bankAccountNo,
      String registeredAddress,
      String registeredPhone,
      String email,
      boolean defaultTitle,
      String status,
      String statusText,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.titleId = titleId;
    this.userId = userId;
    this.titleType = titleType;
    this.titleTypeText = titleTypeText;
    this.titleName = titleName;
    this.taxNo = taxNo;
    this.bankName = bankName;
    this.bankAccountNo = bankAccountNo;
    this.registeredAddress = registeredAddress;
    this.registeredPhone = registeredPhone;
    this.email = email;
    this.defaultTitle = defaultTitle;
    this.status = status;
    this.statusText = statusText;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getTitleId() {
    return titleId;
  }

  public String getUserId() {
    return userId;
  }

  public String getTitleType() {
    return titleType;
  }

  public String getTitleTypeText() {
    return titleTypeText;
  }

  public String getTitleName() {
    return titleName;
  }

  public String getTaxNo() {
    return taxNo;
  }

  public String getBankName() {
    return bankName;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public String getRegisteredAddress() {
    return registeredAddress;
  }

  public String getRegisteredPhone() {
    return registeredPhone;
  }

  public String getEmail() {
    return email;
  }

  public boolean isDefaultTitle() {
    return defaultTitle;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusText() {
    return statusText;
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
      String titleType,
      String titleTypeText,
      String titleName,
      String taxNo,
      String bankName,
      String bankAccountNo,
      String registeredAddress,
      String registeredPhone,
      String email,
      String operator,
      LocalDateTime now) {
    this.titleType = titleType;
    this.titleTypeText = titleTypeText;
    this.titleName = titleName;
    this.taxNo = taxNo;
    this.bankName = bankName;
    this.bankAccountNo = bankAccountNo;
    this.registeredAddress = registeredAddress;
    this.registeredPhone = registeredPhone;
    this.email = email;
    this.operator = operator;
    this.updatedAt = now;
  }

  public void setDefaultTitle(boolean defaultTitle, LocalDateTime now) {
    this.defaultTitle = defaultTitle;
    this.updatedAt = now;
  }

  public void setStatus(String status, String statusText, String operator, LocalDateTime now) {
    this.status = status;
    this.statusText = statusText;
    this.operator = operator;
    this.updatedAt = now;
  }
}
