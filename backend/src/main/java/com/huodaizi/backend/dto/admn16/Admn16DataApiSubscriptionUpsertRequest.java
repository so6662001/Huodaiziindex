package com.huodaizi.backend.dto.admn16;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn16DataApiSubscriptionUpsertRequest(
    @NotBlank(message = "subscriptionCode 不能为空")
    @Size(max = 64, message = "subscriptionCode 最大长度64")
    String subscriptionCode,
    @NotBlank(message = "subscriptionName 不能为空")
    @Size(max = 80, message = "subscriptionName 最大长度80")
    String subscriptionName,
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 32, message = "merchantId 最大长度32")
    String merchantId,
    @NotBlank(message = "merchantName 不能为空")
    @Size(max = 80, message = "merchantName 最大长度80")
    String merchantName,
    @NotBlank(message = "apiPackageCode 不能为空")
    @Size(max = 32, message = "apiPackageCode 最大长度32")
    String apiPackageCode,
    @NotBlank(message = "subscriptionStatus 不能为空")
    @Size(max = 24, message = "subscriptionStatus 最大长度24")
    String subscriptionStatus,
    @NotBlank(message = "billingCycle 不能为空")
    @Size(max = 24, message = "billingCycle 最大长度24")
    String billingCycle,
    @NotBlank(message = "startDate 不能为空")
    @Size(max = 20, message = "startDate 最大长度20")
    String startDate,
    @Size(max = 20, message = "endDate 最大长度20")
    String endDate,
    @Size(max = 24, message = "autoRenewFlag 最大长度24")
    String autoRenewFlag,
    @Size(max = 24, message = "throttlePolicy 最大长度24")
    String throttlePolicy,
    @Size(max = 24, message = "qpsLimit 最大长度24")
    String qpsLimit,
    @Size(max = 24, message = "dailyQuota 最大长度24")
    String dailyQuota,
    @Size(max = 24, message = "monthlyQuota 最大长度24")
    String monthlyQuota,
    @Size(max = 64, message = "owner 最大长度64")
    String owner,
    @NotEmpty(message = "metrics 至少保留1项")
    List<@Valid Admn16DataApiQuotaMetricDTO> metrics,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @NotBlank(message = "operator 不能为空")
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
