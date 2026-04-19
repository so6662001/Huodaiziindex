package com.huodaizi.backend.repository.inquiry;

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
  private final List<FeatureEntity> features;

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
    this.planId = planId;
    this.planCode = planCode;
    this.planName = planName;
    this.planType = planType;
    this.billingCycle = billingCycle;
    this.price = price;
    this.originalPrice = originalPrice;
    this.recommended = recommended;
    this.suitableFor = suitableFor;
    this.features = features;
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

  public List<FeatureEntity> getFeatures() {
    return features;
  }

  public record FeatureEntity(String key, String label, String value, String highlight) {}
}
