package com.huodaizi.backend.service.admn16;

import com.huodaizi.backend.dto.admn16.Admn16DataApiQuotaMetricDTO;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionDetailResponse;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionListItemDTO;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionListRequest;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionListResponse;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn16DataApiSubscriptionEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn16DataApiSubscriptionAdminService {
  private final InMemoryAuthRepository repository;

  public Admn16DataApiSubscriptionAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn16DataApiSubscriptionListResponse list(Admn16DataApiSubscriptionListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn16DataApiSubscriptionEntity> all =
        repository.listDataApiSubscriptionsForAdmin(
            request == null ? null : request.subscriptionStatus(),
            request == null ? null : request.apiProductCode(),
            request == null ? null : request.billingCycle(),
            request == null ? null : request.owner(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn16DataApiSubscriptionListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int activeCount = countByStatus(all, "ACTIVE");
    int trialingCount = countByStatus(all, "TRIALING");
    int suspendedCount = countByStatus(all, "SUSPENDED");
    int expiredCount = countByStatus(all, "EXPIRED");
    return new Admn16DataApiSubscriptionListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.subscriptionStatus()),
        request == null ? "" : safeText(request.apiProductCode()),
        request == null ? "" : safeText(request.billingCycle()),
        request == null ? "" : safeText(request.owner()),
        request == null ? "" : safeText(request.keyword()),
        activeCount,
        suspendedCount,
        expiredCount,
        trialingCount,
        records);
  }

  public Admn16DataApiSubscriptionDetailResponse detail(String subscriptionId) {
    return toDetail(repository.getDataApiSubscriptionForAdmin(subscriptionId));
  }

  public Admn16DataApiSubscriptionDetailResponse upsert(Admn16DataApiSubscriptionUpsertRequest request) {
    List<Admn16DataApiSubscriptionEntity.QuotaMetricSnapshot> quotaMetrics =
        (request.metrics() == null ? List.<Admn16DataApiQuotaMetricDTO>of() : request.metrics()).stream()
            .map(
                metric ->
                    new Admn16DataApiSubscriptionEntity.QuotaMetricSnapshot(
                        safeText(metric.metricCode()),
                        safeText(metric.metricName()),
                        safeText(metric.usedValue()),
                        safeText(metric.quotaValue()),
                        safeText(metric.usageRate()),
                        safeText(metric.trend())))
            .toList();
    String usedDaily = quotaMetrics.isEmpty() ? "0" : safeText(quotaMetrics.get(0).usedValue());
    String usedMonthly = quotaMetrics.size() > 1 ? safeText(quotaMetrics.get(1).usedValue()) : usedDaily;
    Admn16DataApiSubscriptionEntity entity =
        repository.upsertDataApiSubscriptionForAdmin(
            request.subscriptionCode(),
            request.subscriptionName(),
            request.merchantId(),
            request.merchantName(),
            request.apiPackageCode(),
            repository.admn16ApiProductText(request.apiPackageCode()),
            request.subscriptionStatus(),
            request.billingCycle(),
            request.throttlePolicy(),
            request.qpsLimit(),
            request.dailyQuota(),
            request.monthlyQuota(),
            usedDaily,
            usedMonthly,
            quotaMetrics,
            request.startDate(),
            request.endDate(),
            request.owner(),
            request.remark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN16",
        "DATA_API_SUBSCRIPTION_UPSERT",
        "DATA_API_SUBSCRIPTION",
        entity.getSubscriptionId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN16_UPSERT_" + entity.getSubscriptionId(),
        "SUCCESS",
        repository.admn16AuditRiskLevel(entity.getSubscriptionStatus(), entity.getUsageRate()),
        "数据API订阅新增/更新：" + entity.getSubscriptionCode(),
        "",
        "product="
            + entity.getApiProductCode()
            + ", status="
            + entity.getSubscriptionStatus()
            + ", usageRate="
            + entity.getUsageRate(),
        "127.0.0.1",
        "admn16-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN16",
        "DATA_API_SUBSCRIPTION_QUERY",
        "DATA_API_SUBSCRIPTION",
        safeText(targetId).isBlank() ? "LIST" : targetId,
        operator,
        "ADMIN",
        traceId,
        "SUCCESS",
        "LOW",
        summary,
        "",
        "",
        "127.0.0.1",
        "admn16-service");
  }

  private int countByStatus(List<Admn16DataApiSubscriptionEntity> all, String status) {
    return (int)
        all.stream()
            .filter(item -> status.equalsIgnoreCase(safeText(item.getSubscriptionStatus())))
            .count();
  }

  private Admn16DataApiSubscriptionListItemDTO toListItem(Admn16DataApiSubscriptionEntity entity) {
    return new Admn16DataApiSubscriptionListItemDTO(
        entity.getSubscriptionId(),
        entity.getSubscriptionCode(),
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getApiProductCode(),
        entity.getApiProductName(),
        entity.getPlanCode(),
        entity.getPlanName(),
        entity.getBillingCycle(),
        repository.admn16BillingCycleText(entity.getBillingCycle()),
        entity.getSubscriptionStatus(),
        repository.admn16SubscriptionStatusText(entity.getSubscriptionStatus()),
        entity.getMonthlyQuota(),
        entity.getUsedMonthly(),
        entity.getUsageRate(),
        entity.getEndDate(),
        toText(entity.getUpdatedAt()));
  }

  private Admn16DataApiSubscriptionDetailResponse toDetail(Admn16DataApiSubscriptionEntity entity) {
    List<Admn16DataApiQuotaMetricDTO> quotaMetrics =
        entity.getQuotaMetrics().stream()
            .map(
                metric ->
                    new Admn16DataApiQuotaMetricDTO(
                        metric.metricCode(),
                        metric.metricName(),
                        metric.usedValue(),
                        metric.quotaValue(),
                        metric.usageRate(),
                        metric.trend()))
            .toList();
    return new Admn16DataApiSubscriptionDetailResponse(
        entity.getSubscriptionId(),
        entity.getSubscriptionCode(),
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getApiProductCode(),
        entity.getApiProductName(),
        entity.getPlanCode(),
        entity.getPlanName(),
        entity.getSubscriptionStatus(),
        repository.admn16SubscriptionStatusText(entity.getSubscriptionStatus()),
        entity.getAuthMode(),
        repository.admn16SecurityPolicyText(entity.getAuthMode()),
        "",
        entity.getQpsLimit(),
        entity.getDailyQuota(),
        entity.getMonthlyQuota(),
        entity.getUsedDaily(),
        entity.getUsedMonthly(),
        entity.getUsageRate(),
        entity.getEndDate(),
        "",
        entity.getOwner(),
        entity.getRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        quotaMetrics,
        availableActions(entity.getSubscriptionStatus()));
  }

  private List<String> availableActions(String subscriptionStatus) {
    return switch (safeText(subscriptionStatus).toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> List.of("VIEW", "EDIT", "SUSPEND", "ROTATE_KEY");
      case "SUSPENDED" -> List.of("VIEW", "EDIT", "RESUME");
      case "EXPIRED" -> List.of("VIEW", "RENEW", "CLONE");
      case "TRIALING" -> List.of("VIEW", "EDIT", "ACTIVATE", "SUSPEND");
      default -> List.of("VIEW", "EDIT");
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
