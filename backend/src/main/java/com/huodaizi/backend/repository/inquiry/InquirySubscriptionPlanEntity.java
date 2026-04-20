package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;
import java.util.List;

public class InquirySubscriptionPlanEntity {
  private final String planId;
  private final String planCode;
  private final String planName;
  private final String planType;
  private final String billingCycle;
  private final String price;
  private final String originalPrice;
  private final boolean recommended;
  private final String suitableFor;
  private final boolean enabled;
  private final List<FeatureEntity> features;
  private final LocalDateTime updatedAt;
  private final String updatedBy;

  public InquirySubscriptionPlanEntity(
      String planId,
      String planCode,
      String planName,
      String planType,
      String billingCycle,
      String price,
      String originalPrice,
      boolean recommended,
      String suitableFor,
      List<FeatureEntity> features) {
    this(
        planId,
        planCode,
        planName,
        planType,
        billingCycle,
        price,
        originalPrice,
        recommended,
        suitableFor,
        true,
        features,
        LocalDateTime.now(),
        "系统初始化");
  }

  public InquirySubscriptionPlanEntity(
      String planId,
      String planCode,
      String planName,
      String planType,
      String billingCycle,
      String price,
      String originalPrice,
      boolean recommended,
      String suitableFor,
      boolean enabled,
      List<FeatureEntity> features,
      LocalDateTime updatedAt,
      String updatedBy) {
    this.planId = planId;
    this.planCode = planCode;
    this.planName = planName;
    this.planType = planType;
    this.billingCycle = billingCycle;
    this.price = price;
    this.originalPrice = originalPrice;
    this.recommended = recommended;
    this.suitableFor = suitableFor;
    this.enabled = enabled;
    this.features = features;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  public String getPlanId() {
    return planId;
  }

  public String getPlanCode() {
    return planCode;
  }

  public String getPlanName() {
    return planName;
  }

  public String getPlanType() {
    return planType;
  }

  public String getBillingCycle() {
    return billingCycle;
  }

  public String getPrice() {
    return price;
  }

  public String getOriginalPrice() {
    return originalPrice;
  }

  public boolean isRecommended() {
    return recommended;
  }

  public String getSuitableFor() {
    return suitableFor;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public List<FeatureEntity> getFeatures() {
    return features;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public record FeatureEntity(String key, String label, String value, String highlight) {}
}
