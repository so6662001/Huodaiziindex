package com.huodaizi.backend.repository.reconcilemonitor;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorItemDTO;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListResponse;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorOverviewDTO;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorRiskBucketDTO;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import org.springframework.stereotype.Repository;

@Repository
public class A05ReconcileMonitorRepository {
  private final InMemoryInquiryRepository inquiryRepository;

  public A05ReconcileMonitorRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A05ReconcileMonitorListResponse list(A05ReconcileMonitorListRequest request) {
    List<InquiryReconcileOrderEntity> all =
        inquiryRepository.allReconcileOrders().stream()
            .filter(
                item ->
                    request.status() == null
                        || request.status().isBlank()
                        || item.getStatus().name().equalsIgnoreCase(request.status().trim()))
            .filter(
                item ->
                    request.keyword() == null
                        || request.keyword().isBlank()
                        || containsKeyword(item, request.keyword().trim()))
            .filter(
                item ->
                    request.supplierName() == null
                        || request.supplierName().isBlank()
                        || normalize(item.getSupplierName()).contains(normalize(request.supplierName())))
            .filter(item -> !request.safeRiskOnly() || isRisk(item))
            .sorted(Comparator.comparing(InquiryReconcileOrderEntity::getUpdatedAt, Comparator.reverseOrder()))
            .toList();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryReconcileOrderEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);

    int createdCount = countByStatus(all, InquiryReconcileOrderStatus.CREATED);
    int invoicePendingCount = countByStatus(all, InquiryReconcileOrderStatus.INVOICE_PENDING);
    int invoicedCount = countByStatus(all, InquiryReconcileOrderStatus.INVOICED);
    int confirmedCount = countByStatus(all, InquiryReconcileOrderStatus.CONFIRMED);
    int partialPaidCount = countByStatus(all, InquiryReconcileOrderStatus.PARTIAL_PAID);
    int paidCount = countByStatus(all, InquiryReconcileOrderStatus.PAID);
    int closedCount = countByStatus(all, InquiryReconcileOrderStatus.CLOSED);
    int disputedCount = countByStatus(all, InquiryReconcileOrderStatus.DISPUTED);
    int total = all.size();
    int riskCount = (int) all.stream().filter(this::isRisk).count();
    String totalReceivable = toMoney(sumMoney(all, InquiryReconcileOrderEntity::getReceivableAmount));
    String totalPaid = toMoney(sumMoney(all, InquiryReconcileOrderEntity::getPaidAmount));
    String totalOutstanding = toMoney(sumMoney(all, InquiryReconcileOrderEntity::getOutstandingAmount));

    A05ReconcileMonitorOverviewDTO overview =
        new A05ReconcileMonitorOverviewDTO(
            total,
            createdCount,
            invoicePendingCount,
            invoicedCount,
            confirmedCount,
            partialPaidCount,
            paidCount,
            closedCount,
            disputedCount,
            riskCount,
            calcRate(paidCount + closedCount, total),
            calcRate(disputedCount, total),
            calcRate(overdueCount(all), total),
            totalReceivable,
            totalPaid,
            totalOutstanding,
            avgAgingDays(all));

    List<A05ReconcileMonitorRiskBucketDTO> riskBuckets =
        List.of(
            new A05ReconcileMonitorRiskBucketDTO(
                overdueCount(all),
                highOutstandingCount(all),
                disputedCount,
                invoicePendingTimeoutCount(all)));

    return new A05ReconcileMonitorListResponse(
        overview,
        riskBuckets,
        paged.stream().map(this::toItem).toList(),
        total,
        page,
        pageSize);
  }

  public A05ReconcileMonitorListResponse batchUpdate(A05ReconcileMonitorBatchUpdateRequest request) {
    List<InquiryReconcileOrderEntity> updated =
        request.reconcileIds().stream()
            .map(id -> inquiryRepository.getReconcileOrderById(id, request.contactMobile()))
            .peek(updateStatusByAdmin(request.status(), request.paidAmount(), request.remark()))
            .toList();

    int createdCount = countByStatus(updated, InquiryReconcileOrderStatus.CREATED);
    int invoicePendingCount = countByStatus(updated, InquiryReconcileOrderStatus.INVOICE_PENDING);
    int invoicedCount = countByStatus(updated, InquiryReconcileOrderStatus.INVOICED);
    int confirmedCount = countByStatus(updated, InquiryReconcileOrderStatus.CONFIRMED);
    int partialPaidCount = countByStatus(updated, InquiryReconcileOrderStatus.PARTIAL_PAID);
    int paidCount = countByStatus(updated, InquiryReconcileOrderStatus.PAID);
    int closedCount = countByStatus(updated, InquiryReconcileOrderStatus.CLOSED);
    int disputedCount = countByStatus(updated, InquiryReconcileOrderStatus.DISPUTED);
    int total = updated.size();
    int riskCount = (int) updated.stream().filter(this::isRisk).count();

    return new A05ReconcileMonitorListResponse(
        new A05ReconcileMonitorOverviewDTO(
            total,
            createdCount,
            invoicePendingCount,
            invoicedCount,
            confirmedCount,
            partialPaidCount,
            paidCount,
            closedCount,
            disputedCount,
            riskCount,
            calcRate(paidCount + closedCount, total),
            calcRate(disputedCount, total),
            calcRate(overdueCount(updated), total),
            toMoney(sumMoney(updated, InquiryReconcileOrderEntity::getReceivableAmount)),
            toMoney(sumMoney(updated, InquiryReconcileOrderEntity::getPaidAmount)),
            toMoney(sumMoney(updated, InquiryReconcileOrderEntity::getOutstandingAmount)),
            avgAgingDays(updated)),
        List.of(),
        updated.stream().map(this::toItem).toList(),
        total,
        1,
        total);
  }

  private A05ReconcileMonitorItemDTO toItem(InquiryReconcileOrderEntity item) {
    return new A05ReconcileMonitorItemDTO(
        item.getReconcileId(),
        item.getReconcileNo(),
        item.getPickupOrderId(),
        item.getPickupOrderNo(),
        item.getInquiryId(),
        item.getInquiryNo(),
        item.getQuoteId(),
        item.getSupplierId(),
        item.getSupplierName(),
        item.getBuyerCompany(),
        item.getStatementMonth(),
        item.getDueDate(),
        item.getInvoiceAmount(),
        item.getReceivableAmount(),
        item.getPaidAmount(),
        item.getOutstandingAmount(),
        item.getStatus().name(),
        statusText(item.getStatus()),
        riskLevel(item),
        agingDays(item),
        item.getLatestRemark(),
        item.getCreatedAt().toString(),
        item.getUpdatedAt().toString());
  }

  private boolean containsKeyword(InquiryReconcileOrderEntity item, String keyword) {
    String normalized = normalize(keyword);
    return normalize(item.getReconcileNo()).contains(normalized)
        || normalize(item.getPickupOrderNo()).contains(normalized)
        || normalize(item.getInquiryNo()).contains(normalized)
        || normalize(item.getSupplierName()).contains(normalized)
        || normalize(item.getGoodsSummary()).contains(normalized);
  }

  private int countByStatus(List<InquiryReconcileOrderEntity> items, InquiryReconcileOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int overdueCount(List<InquiryReconcileOrderEntity> items) {
    return (int) items.stream().filter(this::isOverdue).count();
  }

  private int highOutstandingCount(List<InquiryReconcileOrderEntity> items) {
    return (int) items.stream().filter(this::isHighOutstanding).count();
  }

  private int invoicePendingTimeoutCount(List<InquiryReconcileOrderEntity> items) {
    return (int)
        items.stream()
            .filter(item -> item.getStatus() == InquiryReconcileOrderStatus.INVOICE_PENDING)
            .filter(item -> agingDays(item) >= 7)
            .count();
  }

  private boolean isRisk(InquiryReconcileOrderEntity item) {
    if (item.getStatus() == InquiryReconcileOrderStatus.DISPUTED) {
      return true;
    }
    if (isOverdue(item)) {
      return true;
    }
    if (isHighOutstanding(item)) {
      return true;
    }
    return item.getStatus() == InquiryReconcileOrderStatus.INVOICE_PENDING && agingDays(item) >= 7;
  }

  private boolean isOverdue(InquiryReconcileOrderEntity item) {
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

  private boolean isHighOutstanding(InquiryReconcileOrderEntity item) {
    return parseMoney(item.getOutstandingAmount()).compareTo(new BigDecimal("50000")) >= 0;
  }

  private long agingDays(InquiryReconcileOrderEntity item) {
    return Duration.between(item.getCreatedAt(), LocalDateTime.now()).toDays();
  }

  private String avgAgingDays(List<InquiryReconcileOrderEntity> items) {
    if (items.isEmpty()) {
      return "0";
    }
    long total = items.stream().mapToLong(this::agingDays).sum();
    return String.valueOf(total / items.size());
  }

  private String statusText(InquiryReconcileOrderStatus status) {
    return switch (status) {
      case CREATED -> "已创建";
      case INVOICE_PENDING -> "待开票";
      case INVOICED -> "已开票";
      case CONFIRMED -> "已确认";
      case PARTIAL_PAID -> "部分回款";
      case PAID -> "已回款";
      case CLOSED -> "已关闭";
      case DISPUTED -> "争议中";
    };
  }

  private String riskLevel(InquiryReconcileOrderEntity item) {
    if (item.getStatus() == InquiryReconcileOrderStatus.DISPUTED || isOverdue(item)) {
      return "HIGH";
    }
    if (isHighOutstanding(item)
        || (item.getStatus() == InquiryReconcileOrderStatus.INVOICE_PENDING && agingDays(item) >= 7)) {
      return "MEDIUM";
    }
    return "LOW";
  }

  private Consumer<InquiryReconcileOrderEntity> updateStatusByAdmin(
      String status, String paidAmount, String remark) {
    InquiryReconcileOrderStatus normalized = parseStatus(status);
    BigDecimal providedPaid = parseMoneyOrNull(paidAmount);
    return item -> {
      BigDecimal receivable = parseMoney(item.getReceivableAmount());
      BigDecimal currentPaid = parseMoney(item.getPaidAmount());
      BigDecimal nextPaid = providedPaid == null ? currentPaid : providedPaid;
      if (normalized == InquiryReconcileOrderStatus.PAID) {
        nextPaid = receivable;
      }
      if (nextPaid.compareTo(receivable) > 0) {
        nextPaid = receivable;
      }
      if (nextPaid.compareTo(BigDecimal.ZERO) < 0) {
        nextPaid = BigDecimal.ZERO;
      }
      BigDecimal outstanding = receivable.subtract(nextPaid).max(BigDecimal.ZERO);
      item.updateAmounts(toMoney(nextPaid), toMoney(outstanding));
      item.setStatus(normalized);
      if (remark != null && !remark.isBlank()) {
        item.setLatestRemark(remark.trim());
      }
    };
  }

  private InquiryReconcileOrderStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    try {
      return InquiryReconcileOrderStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    } catch (Exception ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 CREATED/INVOICE_PENDING/INVOICED/CONFIRMED/PARTIAL_PAID/PAID/CLOSED/DISPUTED");
    }
  }

  private BigDecimal sumMoney(
      List<InquiryReconcileOrderEntity> items, java.util.function.Function<InquiryReconcileOrderEntity, String> fn) {
    return items.stream().map(fn).map(this::parseMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private String calcRate(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.0%";
    }
    double pct = numerator * 100.0 / denominator;
    return String.format(Locale.ROOT, "%.1f%%", pct);
  }

  private BigDecimal parseMoney(String text) {
    try {
      if (text == null || text.isBlank()) {
        return BigDecimal.ZERO;
      }
      return new BigDecimal(text.trim());
    } catch (Exception ex) {
      return BigDecimal.ZERO;
    }
  }

  private BigDecimal parseMoneyOrNull(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    try {
      return new BigDecimal(text.trim());
    } catch (Exception ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "paidAmount 非法");
    }
  }

  private String toMoney(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }
}
