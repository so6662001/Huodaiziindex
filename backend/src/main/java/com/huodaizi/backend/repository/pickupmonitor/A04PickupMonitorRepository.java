package com.huodaizi.backend.repository.pickupmonitor;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorItemDTO;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListResponse;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorOverviewDTO;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorRiskBucketDTO;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
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
public class A04PickupMonitorRepository {
  private final InMemoryInquiryRepository inquiryRepository;

  public A04PickupMonitorRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A04PickupMonitorListResponse list(A04PickupMonitorListRequest request) {
    List<InquiryPickupOrderEntity> all =
        inquiryRepository.allPickupOrders().stream()
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
            .sorted(Comparator.comparing(InquiryPickupOrderEntity::getUpdatedAt, Comparator.reverseOrder()))
            .toList();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryPickupOrderEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);

    int createdCount = countByStatus(all, InquiryPickupOrderStatus.CREATED);
    int confirmedCount = countByStatus(all, InquiryPickupOrderStatus.CONFIRMED);
    int inTransitCount = countByStatus(all, InquiryPickupOrderStatus.IN_TRANSIT);
    int signedCount = countByStatus(all, InquiryPickupOrderStatus.SIGNED);
    int completedCount = countByStatus(all, InquiryPickupOrderStatus.COMPLETED);
    int cancelledCount = countByStatus(all, InquiryPickupOrderStatus.CANCELLED);
    int total = all.size();
    int riskCount = (int) all.stream().filter(this::isRisk).count();

    A04PickupMonitorOverviewDTO overview =
        new A04PickupMonitorOverviewDTO(
            total,
            createdCount,
            confirmedCount,
            inTransitCount,
            signedCount,
            completedCount,
            cancelledCount,
            riskCount,
            calcRate(completedCount, total),
            calcRate(inTransitCount, total),
            calcRate(cancelledCount, total),
            avgAgingHours(all));

    List<A04PickupMonitorRiskBucketDTO> riskBuckets =
        List.of(
            new A04PickupMonitorRiskBucketDTO(
                (int)
                    all.stream()
                        .filter(item -> item.getStatus() == InquiryPickupOrderStatus.CREATED)
                        .filter(this::isTodayPickupPlan)
                        .filter(item -> agingHours(item) >= 12)
                        .count(),
                (int)
                    all.stream()
                        .filter(item -> item.getStatus() == InquiryPickupOrderStatus.CREATED)
                        .filter(item -> agingHours(item) >= 24)
                        .count(),
                (int)
                    all.stream()
                        .filter(item -> item.getStatus() == InquiryPickupOrderStatus.IN_TRANSIT)
                        .filter(item -> agingHours(item) >= 72)
                        .count(),
                (int) all.stream().filter(item -> item.getStatus() == InquiryPickupOrderStatus.CANCELLED).count()));

    return new A04PickupMonitorListResponse(
        overview,
        riskBuckets,
        paged.stream().map(this::toItem).toList(),
        total,
        page,
        pageSize);
  }

  public A04PickupMonitorListResponse batchUpdate(A04PickupMonitorBatchUpdateRequest request) {
    List<InquiryPickupOrderEntity> updated =
        request.pickupIds().stream()
            .map(id -> inquiryRepository.getPickupOrderById(id, request.contactMobile()))
            .peek(updateStatusByAdmin(request.status()))
            .toList();

    List<A04PickupMonitorItemDTO> items = updated.stream().map(this::toItem).toList();
    int createdCount = countByStatus(updated, InquiryPickupOrderStatus.CREATED);
    int confirmedCount = countByStatus(updated, InquiryPickupOrderStatus.CONFIRMED);
    int inTransitCount = countByStatus(updated, InquiryPickupOrderStatus.IN_TRANSIT);
    int signedCount = countByStatus(updated, InquiryPickupOrderStatus.SIGNED);
    int completedCount = countByStatus(updated, InquiryPickupOrderStatus.COMPLETED);
    int cancelledCount = countByStatus(updated, InquiryPickupOrderStatus.CANCELLED);
    int total = updated.size();
    int riskCount = (int) updated.stream().filter(this::isRisk).count();

    return new A04PickupMonitorListResponse(
        new A04PickupMonitorOverviewDTO(
            total,
            createdCount,
            confirmedCount,
            inTransitCount,
            signedCount,
            completedCount,
            cancelledCount,
            riskCount,
            calcRate(completedCount, total),
            calcRate(inTransitCount, total),
            calcRate(cancelledCount, total),
            avgAgingHours(updated)),
        List.of(),
        items,
        total,
        1,
        items.size());
  }

  private A04PickupMonitorItemDTO toItem(InquiryPickupOrderEntity item) {
    return new A04PickupMonitorItemDTO(
        item.getPickupId(),
        item.getPickupNo(),
        item.getInquiryId(),
        item.getInquiryNo(),
        item.getQuoteId(),
        item.getSupplierId(),
        item.getSupplierName(),
        item.getBuyerCompany(),
        item.getPickupAddress(),
        item.getPickupDate(),
        item.getTruckNo(),
        item.getDriverName(),
        item.getSpecText() + " / " + item.getQuantityTon() + "吨",
        item.getStatus().name(),
        pickupStatusText(item.getStatus()),
        riskLevel(item),
        Math.max(agingHours(item), 0),
        item.getRemark(),
        item.getCreatedAt().toString(),
        item.getUpdatedAt().toString());
  }

  private boolean containsKeyword(InquiryPickupOrderEntity item, String keyword) {
    String normalized = normalize(keyword);
    return normalize(item.getPickupNo()).contains(normalized)
        || normalize(item.getInquiryNo()).contains(normalized)
        || normalize(item.getSupplierName()).contains(normalized)
        || normalize(item.getSpecText()).contains(normalized)
        || normalize(item.getPickupAddress()).contains(normalized)
        || normalize(item.getBuyerCompany()).contains(normalized);
  }

  private int countByStatus(List<InquiryPickupOrderEntity> items, InquiryPickupOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private boolean isRisk(InquiryPickupOrderEntity item) {
    if (item.getStatus() == InquiryPickupOrderStatus.CANCELLED) {
      return true;
    }
    if (item.getStatus() == InquiryPickupOrderStatus.CREATED && agingHours(item) >= 24) {
      return true;
    }
    if (item.getStatus() == InquiryPickupOrderStatus.IN_TRANSIT && agingHours(item) >= 48) {
      return true;
    }
    return isScheduleOverdue(item);
  }

  private boolean isScheduleOverdue(InquiryPickupOrderEntity item) {
    if (item.getStatus() == InquiryPickupOrderStatus.COMPLETED || item.getStatus() == InquiryPickupOrderStatus.CANCELLED) {
      return false;
    }
    if (item.getPickupDate() == null || item.getPickupDate().isBlank()) {
      return false;
    }
    try {
      LocalDate pickupDate = LocalDate.parse(item.getPickupDate().trim());
      return pickupDate.isBefore(LocalDate.now());
    } catch (DateTimeParseException ex) {
      return false;
    }
  }

  private boolean isTodayPickupPlan(InquiryPickupOrderEntity item) {
    if (item.getPickupDate() == null || item.getPickupDate().isBlank()) {
      return false;
    }
    try {
      LocalDate pickupDate = LocalDate.parse(item.getPickupDate().trim());
      return pickupDate.isEqual(LocalDate.now());
    } catch (DateTimeParseException ex) {
      return false;
    }
  }

  private long agingHours(InquiryPickupOrderEntity item) {
    return Duration.between(item.getCreatedAt(), LocalDateTime.now()).toHours();
  }

  private String riskLevel(InquiryPickupOrderEntity item) {
    if (item.getStatus() == InquiryPickupOrderStatus.CANCELLED) {
      return "HIGH";
    }
    if (item.getStatus() == InquiryPickupOrderStatus.IN_TRANSIT && agingHours(item) >= 72) {
      return "HIGH";
    }
    if (item.getStatus() == InquiryPickupOrderStatus.CREATED && agingHours(item) >= 24) {
      return "MEDIUM";
    }
    if (isScheduleOverdue(item)) {
      return "MEDIUM";
    }
    return "LOW";
  }

  private String avgAgingHours(List<InquiryPickupOrderEntity> items) {
    if (items.isEmpty()) {
      return "0";
    }
    long total = items.stream().mapToLong(this::agingHours).sum();
    return String.valueOf(total / items.size());
  }

  private Consumer<InquiryPickupOrderEntity> updateStatusByAdmin(String status) {
    InquiryPickupOrderStatus normalized;
    try {
      normalized = InquiryPickupOrderStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    } catch (Exception ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 非法");
    }
    return item -> item.setStatus(normalized);
  }

  private String calcRate(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.0%";
    }
    double pct = numerator * 100.0 / denominator;
    return String.format(Locale.ROOT, "%.1f%%", pct);
  }

  private String pickupStatusText(InquiryPickupOrderStatus status) {
    return switch (status) {
      case CREATED -> "待确认";
      case CONFIRMED -> "已确认";
      case IN_TRANSIT -> "运输中";
      case SIGNED -> "已签收";
      case COMPLETED -> "已完成";
      case CANCELLED -> "已取消";
    };
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }
}
