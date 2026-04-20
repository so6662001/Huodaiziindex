package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;

public class InquiryBillingOrderEntity {
  private final String billId;
  private final String billNo;
  private final String merchantId;
  private final String merchantName;
  private final String subscriptionId;
  private final String subscriptionNo;
  private final String planCode;
  private final String planName;
  private final String periodStart;
  private final String periodEnd;
  private final String issueDate;
  private final String amountYuan;
  private String paidAmountYuan;
  private String unpaidAmountYuan;
  private String status;
  private String dueDate;
  private final String paymentMethod;
  private final String invoiceStatus;
  private final String taxRate;
  private final String taxAmountYuan;
  private final String netAmountYuan;
  private String latestPaymentTime;
  private String latestRemark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public InquiryBillingOrderEntity(
      String billId,
      String billNo,
      String merchantId,
      String merchantName,
      String subscriptionId,
      String subscriptionNo,
      String planCode,
      String planName,
      String periodStart,
      String periodEnd,
      String issueDate,
      String amountYuan,
      String paidAmountYuan,
      String unpaidAmountYuan,
      String status,
      String dueDate,
      String paymentMethod,
      String invoiceStatus,
      String taxRate,
      String taxAmountYuan,
      String netAmountYuan,
      String latestPaymentTime,
      String latestRemark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.billId = billId;
    this.billNo = billNo;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.subscriptionId = subscriptionId;
    this.subscriptionNo = subscriptionNo;
    this.planCode = planCode;
    this.planName = planName;
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
    this.issueDate = issueDate;
    this.amountYuan = amountYuan;
    this.paidAmountYuan = paidAmountYuan;
    this.unpaidAmountYuan = unpaidAmountYuan;
    this.status = status;
    this.dueDate = dueDate;
    this.paymentMethod = paymentMethod;
    this.invoiceStatus = invoiceStatus;
    this.taxRate = taxRate;
    this.taxAmountYuan = taxAmountYuan;
    this.netAmountYuan = netAmountYuan;
    this.latestPaymentTime = latestPaymentTime;
    this.latestRemark = latestRemark;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getBillId() {
    return billId;
  }

  public String getBillNo() {
    return billNo;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public String getSubscriptionNo() {
    return subscriptionNo;
  }

  public String getPlanCode() {
    return planCode;
  }

  public String getPlanName() {
    return planName;
  }

  public String getPeriodStart() {
    return periodStart;
  }

  public String getPeriodEnd() {
    return periodEnd;
  }

  public String getIssueDate() {
    return issueDate;
  }

  public String getAmountYuan() {
    return amountYuan;
  }

  public String getPaidAmountYuan() {
    return paidAmountYuan;
  }

  public String getUnpaidAmountYuan() {
    return unpaidAmountYuan;
  }

  public String getStatus() {
    return status;
  }

  public String getDueDate() {
    return dueDate;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public String getInvoiceStatus() {
    return invoiceStatus;
  }

  public String getTaxRate() {
    return taxRate;
  }

  public String getTaxAmountYuan() {
    return taxAmountYuan;
  }

  public String getNetAmountYuan() {
    return netAmountYuan;
  }

  public String getLatestPaymentTime() {
    return latestPaymentTime;
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

  public void registerPayment(
      String status,
      String paidAmountYuan,
      String unpaidAmountYuan,
      String latestPaymentTime,
      String remark) {
    this.status = status;
    this.paidAmountYuan = paidAmountYuan;
    this.unpaidAmountYuan = unpaidAmountYuan;
    this.latestPaymentTime = latestPaymentTime;
    if (remark != null && !remark.isBlank()) {
      this.latestRemark = remark.trim();
    }
    this.updatedAt = LocalDateTime.now();
  }

  public void setStatus(String status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void setDueDate(String dueDate) {
    this.dueDate = dueDate;
    this.updatedAt = LocalDateTime.now();
  }
}
