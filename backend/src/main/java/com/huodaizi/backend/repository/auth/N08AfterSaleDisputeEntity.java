package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class N08AfterSaleDisputeEntity {
  private final String disputeId;
  private final String userId;
  private final String orderId;
  private final String orderNo;
  private final String inquiryNo;
  private final String buyerCompany;
  private final String supplierName;
  private final String issueType;
  private final String issueTypeText;
  private final String issueSummary;
  private final String issueDescription;
  private final String expectedResolution;
  private final String contactName;
  private final String contactPhoneMasked;
  private final String evidenceFiles;
  private String status;
  private String statusText;
  private String latestRemark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public N08AfterSaleDisputeEntity(
      String disputeId,
      String userId,
      String orderId,
      String orderNo,
      String inquiryNo,
      String buyerCompany,
      String supplierName,
      String issueType,
      String issueTypeText,
      String issueSummary,
      String issueDescription,
      String expectedResolution,
      String contactName,
      String contactPhoneMasked,
      String evidenceFiles,
      String status,
      String statusText,
      String latestRemark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.disputeId = disputeId;
    this.userId = userId;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.buyerCompany = buyerCompany;
    this.supplierName = supplierName;
    this.issueType = issueType;
    this.issueTypeText = issueTypeText;
    this.issueSummary = issueSummary;
    this.issueDescription = issueDescription;
    this.expectedResolution = expectedResolution;
    this.contactName = contactName;
    this.contactPhoneMasked = contactPhoneMasked;
    this.evidenceFiles = evidenceFiles;
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = latestRemark;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getDisputeId() {
    return disputeId;
  }

  public String getUserId() {
    return userId;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getIssueType() {
    return issueType;
  }

  public String getIssueTypeText() {
    return issueTypeText;
  }

  public String getIssueSummary() {
    return issueSummary;
  }

  public String getIssueDescription() {
    return issueDescription;
  }

  public String getExpectedResolution() {
    return expectedResolution;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhoneMasked() {
    return contactPhoneMasked;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void updateStatus(String status, String statusText, String remark, LocalDateTime now) {
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = remark;
    this.updatedAt = now;
  }
}
