package com.huodaizi.backend.dto.admn16;

import java.util.List;

public record Admn16DataApiSubscriptionDetailResponse(
    String subscriptionId,
    String subscriptionCode,
    String merchantId,
    String merchantName,
    String apiPackageCode,
    String apiPackageName,
    String scenarioCode,
    String scenarioText,
    String subscriptionStatus,
    String subscriptionStatusText,
    String authMode,
    String authModeText,
    String dataScope,
    String qpsLimit,
    String dailyQuota,
    String monthlyQuota,
    String usedToday,
    String usedThisMonth,
    String usageRate,
    String expireAt,
    String notifyWebhook,
    String owner,
    String latestRemark,
    String createdAt,
    String updatedAt,
    List<Admn16DataApiQuotaMetricDTO> quotaMetrics,
    List<String> availableActions) {}
