package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class N12InvoiceApplicationEntity {
  private final String applicationId;
  private final String userId;
  private final String orderId;
  private final String orderNo;
  private final String inquiryNo;
  private final String supplierName;
  private final String amount;
  private final String taxRate;
  private final String invoiceType;
  private final String invoiceTypeText;
  private final String titleId;
  private String status;
  private String statusText;
  private String latestRemark;
  private final String recipientEmail;
  private final String recipientMobile;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public N12InvoiceApplicationEntity(
      String applicationId,
      String userId,
      String orderId,
      String orderNo,
      String inquiryNo,
      String supplierName,
      String amount,
      String taxRate,
      String invoiceType,
      String invoiceTypeText,
      String titleId,
      String status,
      String statusText,
      String latestRemark,
      String recipientEmail,
      String recipientMobile,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.applicationId = applicationId;
    this.userId = userId;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.supplierName = supplierName;
    this.amount = amount;
    this.taxRate = taxRate;
    this.invoiceType = invoiceType;
    this.invoiceTypeText = invoiceTypeText;
    this.titleId = titleId;
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = latestRemark;
    this.recipientEmail = recipientEmail;
    this.recipientMobile = recipientMobile;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getApplicationId() {
    return applicationId;
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

  public String getSupplierName() {
    return supplierName;
  }

  public String getAmount() {
    return amount;
  }

  public String getTaxRate() {
    return taxRate;
  }

  public String getInvoiceType() {
    return invoiceType;
  }

  public String getInvoiceTypeText() {
    return invoiceTypeText;
  }

  public String getTitleId() {
    return titleId;
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

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public String getRecipientMobile() {
    return recipientMobile;
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
