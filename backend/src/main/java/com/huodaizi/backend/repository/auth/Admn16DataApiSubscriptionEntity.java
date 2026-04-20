package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn16DataApiSubscriptionEntity {
  private final String subscriptionId;
  private String subscriptionCode;
  private String merchantId;
  private String merchantName;
  private String apiProductCode;
  private String apiProductName;
  private String planCode;
  private String planName;
  private String subscriptionStatus;
  private String billingCycle;
  private String authMode;
  private String qpsLimit;
  private String dailyQuota;
  private String monthlyQuota;
  private String usedDaily;
  private String usedMonthly;
  private String usageRate;
  private String startDate;
  private String endDate;
  private String owner;
  private String remark;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<QuotaMetricSnapshot> quotaMetrics;

  public Admn16DataApiSubscriptionEntity(
      String subscriptionId,
      String subscriptionCode,
      String merchantId,
      String merchantName,
      String apiProductCode,
      String apiProductName,
      String planCode,
      String planName,
      String subscriptionStatus,
      String billingCycle,
      String authMode,
      String qpsLimit,
      String dailyQuota,
      String monthlyQuota,
      String usedDaily,
      String usedMonthly,
      String usageRate,
      String startDate,
      String endDate,
      String owner,
      String remark,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<QuotaMetricSnapshot> quotaMetrics) {
    this.subscriptionId = subscriptionId;
    this.subscriptionCode = subscriptionCode;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.apiProductCode = apiProductCode;
    this.apiProductName = apiProductName;
    this.planCode = planCode;
    this.planName = planName;
    this.subscriptionStatus = subscriptionStatus;
    this.billingCycle = billingCycle;
    this.authMode = authMode;
    this.qpsLimit = qpsLimit;
    this.dailyQuota = dailyQuota;
    this.monthlyQuota = monthlyQuota;
    this.usedDaily = usedDaily;
    this.usedMonthly = usedMonthly;
    this.usageRate = usageRate;
    this.startDate = startDate;
    this.endDate = endDate;
    this.owner = owner;
    this.remark = remark;
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.quotaMetrics = new ArrayList<>(quotaMetrics == null ? List.of() : quotaMetrics);
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public String getSubscriptionCode() {
    return subscriptionCode;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getApiProductCode() {
    return apiProductCode;
  }

  public String getApiProductName() {
    return apiProductName;
  }

  public String getPlanCode() {
    return planCode;
  }

  public String getPlanName() {
    return planName;
  }

  public String getSubscriptionStatus() {
    return subscriptionStatus;
  }

  public String getBillingCycle() {
    return billingCycle;
  }

  public String getAuthMode() {
    return authMode;
  }

  public String getQpsLimit() {
    return qpsLimit;
  }

  public String getDailyQuota() {
    return dailyQuota;
  }

  public String getMonthlyQuota() {
    return monthlyQuota;
  }

  public String getUsedDaily() {
    return usedDaily;
  }

  public String getUsedMonthly() {
    return usedMonthly;
  }

  public String getUsageRate() {
    return usageRate;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getOwner() {
    return owner;
  }

  public String getRemark() {
    return remark;
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

  public List<QuotaMetricSnapshot> getQuotaMetrics() {
    return List.copyOf(quotaMetrics);
  }

  public void update(
      String subscriptionCode,
      String merchantId,
      String merchantName,
      String apiProductCode,
      String apiProductName,
      String planCode,
      String planName,
      String subscriptionStatus,
      String billingCycle,
      String authMode,
      String qpsLimit,
      String dailyQuota,
      String monthlyQuota,
      String usedDaily,
      String usedMonthly,
      String usageRate,
      String startDate,
      String endDate,
      String owner,
      String remark,
      String operator,
      List<QuotaMetricSnapshot> quotaMetrics,
      LocalDateTime now) {
    this.subscriptionCode = subscriptionCode;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.apiProductCode = apiProductCode;
    this.apiProductName = apiProductName;
    this.planCode = planCode;
    this.planName = planName;
    this.subscriptionStatus = subscriptionStatus;
    this.billingCycle = billingCycle;
    this.authMode = authMode;
    this.qpsLimit = qpsLimit;
    this.dailyQuota = dailyQuota;
    this.monthlyQuota = monthlyQuota;
    this.usedDaily = usedDaily;
    this.usedMonthly = usedMonthly;
    this.usageRate = usageRate;
    this.startDate = startDate;
    this.endDate = endDate;
    this.owner = owner;
    this.remark = remark;
    this.operator = operator;
    this.quotaMetrics.clear();
    this.quotaMetrics.addAll(quotaMetrics == null ? List.of() : quotaMetrics);
    this.updatedAt = now;
  }

  public static final class QuotaMetricSnapshot {
    private final String metricCode;
    private final String metricName;
    private final String usedValue;
    private final String quotaValue;
    private final String usageRate;
    private final String trend;

    public QuotaMetricSnapshot(
        String metricCode,
        String metricName,
        String usedValue,
        String quotaValue,
        String usageRate,
        String trend) {
      this.metricCode = metricCode;
      this.metricName = metricName;
      this.usedValue = usedValue;
      this.quotaValue = quotaValue;
      this.usageRate = usageRate;
      this.trend = trend;
    }

    public String metricCode() {
      return metricCode;
    }

    public String metricName() {
      return metricName;
    }

    public String usedValue() {
      return usedValue;
    }

    public String quotaValue() {
      return quotaValue;
    }

    public String usageRate() {
      return usageRate;
    }

    public String trend() {
      return trend;
    }
  }
}
