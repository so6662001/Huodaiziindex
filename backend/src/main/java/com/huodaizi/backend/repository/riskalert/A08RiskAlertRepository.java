package com.huodaizi.backend.repository.riskalert;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertBatchUpdateRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertBucketDTO;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertItemDTO;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListResponse;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertOverviewDTO;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryBillingOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class A08RiskAlertRepository {
  private static final String STATUS_OPEN = "OPEN";
  private static final String STATUS_PROCESSING = "PROCESSING";
  private static final String STATUS_RESOLVED = "RESOLVED";

  private final InMemoryInquiryRepository inquiryRepository;
  private final Map<String, AlertResolution> resolutionStore = new ConcurrentHashMap<>();

  public A08RiskAlertRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A08RiskAlertListResponse list(A08RiskAlertListRequest request) {
    List<AlertCandidate> all =
        buildAlerts().stream()
            .filter(
                item ->
                    request.source() == null
                        || request.source().isBlank()
                        || item.alert().sourceType().equalsIgnoreCase(request.source().trim()))
            .filter(
                item ->
                    request.level() == null
                        || request.level().isBlank()
                        || item.alert().severity().equalsIgnoreCase(request.level().trim()))
            .filter(
                item ->
                    request.status() == null
                        || request.status().isBlank()
                        || item.alert().status().equalsIgnoreCase(request.status().trim()))
            .filter(
                item ->
                    request.owner() == null
                        || request.owner().isBlank()
                        || normalize(item.alert().owner()).contains(normalize(request.owner())))
            .filter(item -> !request.safeHighOnly() || "HIGH".equalsIgnoreCase(item.alert().severity()))
            .filter(item -> matchedContact(item, request.contactMobile()))
            .filter(item -> keywordMatched(item.alert(), request.keyword()))
            .sorted(Comparator.comparing(AlertCandidate::updatedAt, Comparator.reverseOrder()))
            .toList();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<A08RiskAlertItemDTO> paged =
        from >= all.size() ? List.of() : all.subList(from, to).stream().map(AlertCandidate::alert).toList();

    int total = all.size();
    int highCount = countSeverity(all, "HIGH");
    int mediumCount = countSeverity(all, "MEDIUM");
    int lowCount = countSeverity(all, "LOW");
    int openCount = countStatus(all, STATUS_OPEN);
    int processingCount = countStatus(all, STATUS_PROCESSING);
    int resolvedCount = countStatus(all, STATUS_RESOLVED);
    int overdueCount = countOverdue(all);

    A08RiskAlertOverviewDTO overview =
        new A08RiskAlertOverviewDTO(
            total,
            highCount,
            mediumCount,
            lowCount,
            openCount,
            processingCount,
            resolvedCount,
            overdueCount,
            calcRate(highCount, total),
            calcRate(resolvedCount, total),
            avgAgingHours(all));

    A08RiskAlertBucketDTO bucket =
        new A08RiskAlertBucketDTO(
            countRiskCode(all, "LEAD_TIMEOUT"),
            countRiskCode(all, "PICKUP_OVERDUE"),
            countRiskCode(all, "RECONCILE_OVERDUE"),
            countRiskCode(all, "BILLING_OVERDUE"),
            highCount,
            mediumCount,
            openCount + processingCount);

    return new A08RiskAlertListResponse(overview, List.of(bucket), paged, total, page, pageSize);
  }

  public A08RiskAlertListResponse batchUpdate(A08RiskAlertBatchUpdateRequest request) {
    LocalDateTime now = LocalDateTime.now();
    String status = normalizeAlertStatus(request.status());
    String operator = defaultText(request.operator(), "A08-ADMIN");
    String remark = defaultText(request.remark(), "");
    for (String alertId : request.alertIds()) {
      String normalized = defaultText(alertId, "");
      if (normalized.isBlank()) {
        continue;
      }
      resolutionStore.put(normalized, new AlertResolution(status, operator, remark, now));
    }
    return list(new A08RiskAlertListRequest(null, null, null, null, null, null, null, 1, 500));
  }

  private List<AlertCandidate> buildAlerts() {
    List<AlertCandidate> leadAlerts =
        inquiryRepository.allMerchantLeads().stream()
            .flatMap(lead -> buildLeadAlerts(lead).stream())
            .toList();
    List<AlertCandidate> pickupAlerts =
        inquiryRepository.allPickupOrders().stream()
            .flatMap(order -> buildPickupAlerts(order).stream())
            .toList();
    List<AlertCandidate> reconcileAlerts =
        inquiryRepository.allReconcileOrders().stream()
            .flatMap(order -> buildReconcileAlerts(order).stream())
            .toList();
    List<AlertCandidate> billingAlerts =
        inquiryRepository.allBillingOrders().stream()
            .flatMap(order -> buildBillingAlerts(order).stream())
            .toList();
    return Stream.of(leadAlerts, pickupAlerts, reconcileAlerts, billingAlerts)
        .flatMap(List::stream)
        .toList();
  }

  private List<AlertCandidate> buildLeadAlerts(InquiryMerchantLeadEntity lead) {
    long aging = agingHours(lead.getCreatedAt());
    if (lead.getStatus() == InquiryMerchantLeadStatus.NEW && aging >= 2) {
      return List.of(
          toAlert(
              "LEAD_TIMEOUT-" + lead.getId(),
              "LEAD",
              lead.getId(),
              lead.getLeadNo(),
              lead.getMerchantId(),
              lead.getMerchantName(),
              lead.getDeliveryCity(),
              "HIGH",
              "LEAD_TIMEOUT",
              "线索超时未报价",
              "新线索超过2小时仍未报价，存在流失风险",
              "88",
              defaultText(lead.getOwner(), lead.getMerchantName()),
              "联系商家并在30分钟内完成报价/说明",
              aging,
              lead.getContactMobileMasked(),
              lead.getCreatedAt(),
              lead.getUpdatedAt()));
    }
    if ((lead.getStatus() == InquiryMerchantLeadStatus.FOLLOWING
            || lead.getStatus() == InquiryMerchantLeadStatus.CONTACTED)
        && aging >= 24) {
      return List.of(
          toAlert(
              "LEAD_FOLLOW_STALE-" + lead.getId(),
              "LEAD",
              lead.getId(),
              lead.getLeadNo(),
              lead.getMerchantId(),
              lead.getMerchantName(),
              lead.getDeliveryCity(),
              "MEDIUM",
              "LEAD_FOLLOW_STALE",
              "线索跟进停滞",
              "线索跟进超过24小时无实质推进，需复盘推进路径",
              "68",
              defaultText(lead.getOwner(), lead.getMerchantName()),
              "补充跟进记录并确认报价/关闭结论",
              aging,
              lead.getContactMobileMasked(),
              lead.getCreatedAt(),
              lead.getUpdatedAt()));
    }
    return List.of();
  }

  private List<AlertCandidate> buildPickupAlerts(InquiryPickupOrderEntity order) {
    long aging = agingHours(order.getCreatedAt());
    if ((order.getStatus() == InquiryPickupOrderStatus.CREATED
            || order.getStatus() == InquiryPickupOrderStatus.CONFIRMED)
        && isPickupDateOverdue(order.getPickupDate())) {
      return List.of(
          toAlert(
              "PICKUP_OVERDUE-" + order.getPickupId(),
              "PICKUP",
              order.getPickupId(),
              order.getPickupNo(),
              order.getSupplierId(),
              order.getSupplierName(),
              extractCity(order.getPickupAddress()),
              "HIGH",
              "PICKUP_OVERDUE",
              "提货计划逾期",
              "提货计划时间已过仍未完成，影响履约评分",
              "86",
              order.getSupplierName(),
              "立即核实车辆进场状态并重新排期",
              aging,
              order.getBuyerPhone(),
              order.getCreatedAt(),
              order.getUpdatedAt()));
    }
    if (order.getStatus() == InquiryPickupOrderStatus.IN_TRANSIT && aging >= 72) {
      return List.of(
          toAlert(
              "PICKUP_TRANSIT_LONG-" + order.getPickupId(),
              "PICKUP",
              order.getPickupId(),
              order.getPickupNo(),
              order.getSupplierId(),
              order.getSupplierName(),
              extractCity(order.getPickupAddress()),
              "MEDIUM",
              "PICKUP_TRANSIT_LONG",
              "提货在途超时",
              "在途时间超过72小时，存在签收延迟风险",
              "65",
              order.getSupplierName(),
              "联系司机与仓库确认运输异常并更新ETA",
              aging,
              order.getBuyerPhone(),
              order.getCreatedAt(),
              order.getUpdatedAt()));
    }
    return List.of();
  }

  private List<AlertCandidate> buildReconcileAlerts(InquiryReconcileOrderEntity order) {
    long aging = agingHours(order.getCreatedAt());
    java.util.ArrayList<AlertCandidate> alerts = new java.util.ArrayList<>();
    if (isReconcileOverdue(order)) {
      alerts.add(
          toAlert(
              "RECONCILE_OVERDUE-" + order.getReconcileId(),
              "RECONCILE",
              order.getReconcileId(),
              order.getReconcileNo(),
              order.getSupplierId(),
              order.getSupplierName(),
              "-",
              "HIGH",
              "RECONCILE_OVERDUE",
              "对账逾期未回款",
              "对账单超过到期日仍存在未收款项",
              "90",
              order.getSupplierName(),
              "发起催收并升级财务风控跟进",
              aging,
              order.getContactMobile(),
              order.getCreatedAt(),
              order.getUpdatedAt()));
    }
    if (order.getStatus() == InquiryReconcileOrderStatus.DISPUTED) {
      alerts.add(
          toAlert(
              "RECONCILE_DISPUTE-" + order.getReconcileId(),
              "RECONCILE",
              order.getReconcileId(),
              order.getReconcileNo(),
              order.getSupplierId(),
              order.getSupplierName(),
              "-",
              "HIGH",
              "RECONCILE_DISPUTE",
              "对账争议待处理",
              "存在争议未结案，可能影响回款与续约",
              "84",
              order.getSupplierName(),
              "安排法务与运营协同处理争议",
              aging,
              order.getContactMobile(),
              order.getCreatedAt(),
              order.getUpdatedAt()));
    }
    return alerts;
  }

  private List<AlertCandidate> buildBillingAlerts(InquiryBillingOrderEntity order) {
    if (!isBillingOverdue(order)) {
      return List.of();
    }
    long aging = agingHours(order.getCreatedAt());
    return List.of(
        toAlert(
            "BILLING_OVERDUE-" + order.getBillId(),
            "BILLING",
            order.getBillId(),
            order.getBillNo(),
            order.getMerchantId(),
            order.getMerchantName(),
            "-",
            "MEDIUM",
            "BILLING_OVERDUE",
            "账单逾期未支付",
            "账单超过到期日仍未回款，存在停服风险",
            "62",
            order.getMerchantName(),
            "触发自动催缴并提醒业务跟进",
            aging,
            "",
            order.getCreatedAt(),
            order.getUpdatedAt()));
  }

  private AlertCandidate toAlert(
      String alertId,
      String sourceType,
      String bizId,
      String bizNo,
      String merchantId,
      String merchantName,
      String city,
      String severity,
      String riskCode,
      String riskTitle,
      String riskDetail,
      String riskScore,
      String owner,
      String suggestedAction,
      long agingHours,
      String contactMobile,
      LocalDateTime createdAt,
      LocalDateTime sourceUpdatedAt) {
    AlertResolution resolution = resolutionStore.get(alertId);
    String status = resolution == null ? STATUS_OPEN : resolution.status();
    LocalDateTime updatedAt = resolution == null ? sourceUpdatedAt : resolution.handledAt();
    A08RiskAlertItemDTO dto =
        new A08RiskAlertItemDTO(
            alertId,
            sourceType,
            sourceText(sourceType),
            bizId,
            bizNo,
            merchantId,
            merchantName,
            city,
            severity,
            severityText(severity),
            riskCode,
            riskTitle,
            riskDetail,
            riskScore,
            owner,
            status,
            statusText(status),
            Math.max(agingHours, 0),
            suggestedAction,
            createdAt.toString(),
            updatedAt.toString());
    return new AlertCandidate(dto, defaultText(contactMobile, ""), updatedAt);
  }

  private boolean matchedContact(AlertCandidate item, String contactMobile) {
    if (contactMobile == null || contactMobile.isBlank()) {
      return true;
    }
    String mobile = contactMobile.trim();
    String raw = defaultText(item.contactMobile(), "");
    if (raw.isBlank()) {
      return false;
    }
    if (raw.contains("*")) {
      String tail = mobile.length() >= 4 ? mobile.substring(mobile.length() - 4) : mobile;
      return raw.endsWith(tail);
    }
    return raw.equals(mobile);
  }

  private boolean keywordMatched(A08RiskAlertItemDTO item, String keyword) {
    String normalized = normalize(keyword);
    if (normalized == null) {
      return true;
    }
    return normalize(item.sourceType()).contains(normalized)
        || normalize(item.bizNo()).contains(normalized)
        || normalize(item.riskCode()).contains(normalized)
        || normalize(item.riskTitle()).contains(normalized)
        || normalize(item.riskDetail()).contains(normalized)
        || normalize(item.owner()).contains(normalized)
        || normalize(item.merchantName()).contains(normalized);
  }

  private int countSeverity(List<AlertCandidate> items, String severity) {
    return (int)
        items.stream()
            .filter(item -> severity.equalsIgnoreCase(item.alert().severity()))
            .count();
  }

  private int countStatus(List<AlertCandidate> items, String status) {
    return (int) items.stream().filter(item -> status.equalsIgnoreCase(item.alert().status())).count();
  }

  private int countOverdue(List<AlertCandidate> items) {
    return (int)
        items.stream()
            .filter(
                item ->
                    item.alert().riskCode().contains("OVERDUE")
                        || item.alert().riskCode().contains("TIMEOUT"))
            .count();
  }

  private int countRiskCode(List<AlertCandidate> items, String riskCode) {
    return (int)
        items.stream()
            .filter(item -> riskCode.equalsIgnoreCase(item.alert().riskCode()))
            .count();
  }

  private String calcRate(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.0%";
    }
    BigDecimal rate =
        new BigDecimal(numerator)
            .multiply(new BigDecimal("100"))
            .divide(new BigDecimal(denominator), 1, java.math.RoundingMode.HALF_UP);
    return rate.stripTrailingZeros().toPlainString() + "%";
  }

  private String avgAgingHours(List<AlertCandidate> items) {
    if (items.isEmpty()) {
      return "0";
    }
    long total = items.stream().mapToLong(item -> item.alert().agingHours()).sum();
    return String.valueOf(total / items.size());
  }

  private long agingHours(LocalDateTime createdAt) {
    return Duration.between(createdAt, LocalDateTime.now()).toHours();
  }

  private boolean isPickupDateOverdue(String pickupDate) {
    if (pickupDate == null || pickupDate.isBlank()) {
      return false;
    }
    try {
      return LocalDate.parse(pickupDate.trim()).isBefore(LocalDate.now());
    } catch (DateTimeParseException ex) {
      return false;
    }
  }

  private boolean isReconcileOverdue(InquiryReconcileOrderEntity item) {
    if (item.getStatus() == InquiryReconcileOrderStatus.PAID
        || item.getStatus() == InquiryReconcileOrderStatus.CLOSED) {
      return false;
    }
    if (item.getDueDate() == null || item.getDueDate().isBlank()) {
      return false;
    }
    try {
      LocalDate dueDate = LocalDate.parse(item.getDueDate().trim());
      return dueDate.isBefore(LocalDate.now())
          && parseMoney(item.getOutstandingAmount()).compareTo(BigDecimal.ZERO) > 0;
    } catch (DateTimeParseException ex) {
      return false;
    }
  }

  private boolean isBillingOverdue(InquiryBillingOrderEntity item) {
    if ("PAID".equalsIgnoreCase(item.getStatus())) {
      return false;
    }
    if (item.getDueDate() == null || item.getDueDate().isBlank()) {
      return false;
    }
    try {
      LocalDate dueDate = LocalDate.parse(item.getDueDate().trim());
      return dueDate.isBefore(LocalDate.now())
          && parseMoney(item.getUnpaidAmountYuan()).compareTo(BigDecimal.ZERO) > 0;
    } catch (DateTimeParseException ex) {
      return false;
    }
  }

  private BigDecimal parseMoney(String text) {
    if (text == null || text.isBlank()) {
      return BigDecimal.ZERO;
    }
    try {
      return new BigDecimal(text.trim());
    } catch (Exception ex) {
      return BigDecimal.ZERO;
    }
  }

  private String normalizeAlertStatus(String status) {
    String normalized = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case STATUS_OPEN, STATUS_PROCESSING, STATUS_RESOLVED -> normalized;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 OPEN/PROCESSING/RESOLVED");
    };
  }

  private String statusText(String status) {
    return switch (defaultText(status, "").toUpperCase(Locale.ROOT)) {
      case STATUS_RESOLVED -> "已处理";
      case STATUS_PROCESSING -> "处理中";
      default -> "待处理";
    };
  }

  private String sourceText(String sourceType) {
    return switch (defaultText(sourceType, "").toUpperCase(Locale.ROOT)) {
      case "LEAD" -> "线索";
      case "PICKUP" -> "提货";
      case "RECONCILE" -> "对账";
      case "BILLING" -> "账单";
      default -> "-";
    };
  }

  private String severityText(String severity) {
    return switch (defaultText(severity, "").toUpperCase(Locale.ROOT)) {
      case "HIGH" -> "高";
      case "MEDIUM" -> "中";
      default -> "低";
    };
  }

  private String extractCity(String pickupAddress) {
    String address = defaultText(pickupAddress, "");
    if (address.isBlank()) {
      return "-";
    }
    return address.length() <= 2 ? address : address.substring(0, 2);
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }

  private record AlertResolution(String status, String operator, String remark, LocalDateTime handledAt) {}

  private record AlertCandidate(A08RiskAlertItemDTO alert, String contactMobile, LocalDateTime updatedAt) {}
}
