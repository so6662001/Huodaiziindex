package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryMerchantSubscriptionEntity {
  private final String subscriptionId;
  private final String subscriptionNo;
  private final String merchantId;
  private final String merchantName;
  private final String planCode;
  private final String planName;
  private final String billingCycle;
  private final String status;
  private final String startAt;
  private final String endAt;
  private final String autoRenew;
  private final String amountYuan;
  private final List<String> entitlements;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public InquiryMerchantSubscriptionEntity(
      String subscriptionId,
      String subscriptionNo,
      String merchantId,
      String merchantName,
      String planCode,
      String planName,
      String billingCycle,
      String status,
      String startAt,
      String endAt,
      String autoRenew,
      String amountYuan,
      List<String> entitlements,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.subscriptionId = subscriptionId;
    this.subscriptionNo = subscriptionNo;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.planCode = planCode;
    this.planName = planName;
    this.billingCycle = billingCycle;
    this.status = status;
    this.startAt = startAt;
    this.endAt = endAt;
    this.autoRenew = autoRenew;
    this.amountYuan = amountYuan;
    this.entitlements = entitlements;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public String getSubscriptionNo() {
    return subscriptionNo;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getPlanCode() {
    return planCode;
  }

  public String getPlanName() {
    return planName;
  }

  public String getBillingCycle() {
    return billingCycle;
  }

  public String getStatus() {
    return status;
  }

  public String getStartAt() {
    return startAt;
  }

  public String getEndAt() {
    return endAt;
  }

  public String getAutoRenew() {
    return autoRenew;
  }

  public String getAmountYuan() {
    return amountYuan;
  }

  public List<String> getEntitlements() {
    return entitlements;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
