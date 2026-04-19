package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquirySubscriptionMineItemDTO(
    String subscriptionId,
    String merchantId,
    String merchantName,
    String planCode,
    String planName,
    String planTier,
    String status,
    String statusText,
    String billingCycle,
    String startAt,
    String expireAt,
    String autoRenew,
    String amountYuan,
    String currency,
    List<String> entitlements,
    String createdAt,
    String updatedAt) {}
