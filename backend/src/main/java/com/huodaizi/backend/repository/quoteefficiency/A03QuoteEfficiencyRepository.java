package com.huodaizi.backend.repository.quoteefficiency;

import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchBatchUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyAgingBucketDTO;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyBatchUpdateRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListResponse;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyOverviewDTO;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyQuickQuoteRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyTaskItemDTO;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class A03QuoteEfficiencyRepository {
  private final InMemoryInquiryRepository inquiryRepository;

  public A03QuoteEfficiencyRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A03QuoteEfficiencyListResponse list(A03QuoteEfficiencyListRequest request) {
    List<InquiryMerchantLeadEntity> all =
        inquiryRepository.workbenchTasks(
            new InquiryQuoteWorkbenchTaskRequest(
                request.merchantId(),
                request.status(),
                request.safeQuoteTimeoutOnly(),
                request.sortBy(),
                request.keyword(),
                1,
                Integer.MAX_VALUE));

    List<InquiryMerchantLeadEntity> filtered =
        all.stream()
            .filter(
                item ->
                    request.city() == null
                        || request.city().isBlank()
                        || normalize(item.getDeliveryCity()).contains(normalize(request.city())))
            .sorted(
                Comparator.comparing(
                    InquiryMerchantLeadEntity::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<InquiryMerchantLeadEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);

    int total = filtered.size();
    int newCount = countByStatus(filtered, InquiryMerchantLeadStatus.NEW);
    int contactedCount = countByStatus(filtered, InquiryMerchantLeadStatus.CONTACTED);
    int followingCount = countByStatus(filtered, InquiryMerchantLeadStatus.FOLLOWING);
    int quotedCount = countByStatus(filtered, InquiryMerchantLeadStatus.QUOTED);
    int wonCount = countByStatus(filtered, InquiryMerchantLeadStatus.WON);
    int lostCount = countByStatus(filtered, InquiryMerchantLeadStatus.LOST);
    int pendingQuoteCount = newCount + contactedCount + followingCount;
    int timeoutCount = calcTimeoutCount(filtered);
    String avgResponseMinutes = averageResponseMinutes(filtered);
    String quoteRate = calcRate(quotedCount, total);
    String winRate = calcRate(wonCount, quotedCount);

    A03QuoteEfficiencyOverviewDTO overview =
        new A03QuoteEfficiencyOverviewDTO(
            total,
            pendingQuoteCount,
            timeoutCount,
            quotedCount,
            wonCount,
            lostCount,
            quoteRate,
            winRate,
            avgResponseMinutes);

    A03QuoteEfficiencyAgingBucketDTO agingBucket =
        new A03QuoteEfficiencyAgingBucketDTO(
            countByAgingMinutes(filtered, 0, 10),
            countByAgingMinutes(filtered, 10, 30),
            countByAgingMinutes(filtered, 30, 60),
            countByAgingMinutes(filtered, 60, Integer.MAX_VALUE),
            timeoutCount);

    return new A03QuoteEfficiencyListResponse(
        overview,
        List.of(agingBucket),
        paged.stream().map(this::toTaskItem).toList(),
        total,
        page,
        pageSize);
  }

  public A03QuoteEfficiencyListResponse batchUpdate(A03QuoteEfficiencyBatchUpdateRequest request) {
    List<InquiryMerchantLeadEntity> updated =
        inquiryRepository.workbenchBatchUpdate(
            new InquiryQuoteWorkbenchBatchUpdateRequest(
                request.merchantId(),
                request.leadIds(),
                request.status(),
                request.operator(),
                request.comment()));
    List<A03QuoteEfficiencyTaskItemDTO> items = updated.stream().map(this::toTaskItem).toList();
    int quotedCount = (int) updated.stream().filter(item -> item.getStatus() == InquiryMerchantLeadStatus.QUOTED).count();
    int wonCount = (int) updated.stream().filter(item -> item.getStatus() == InquiryMerchantLeadStatus.WON).count();
    int timeoutCount = calcTimeoutCount(updated);
    return new A03QuoteEfficiencyListResponse(
        new A03QuoteEfficiencyOverviewDTO(
            updated.size(),
            countByStatus(updated, InquiryMerchantLeadStatus.NEW)
                + countByStatus(updated, InquiryMerchantLeadStatus.CONTACTED)
                + countByStatus(updated, InquiryMerchantLeadStatus.FOLLOWING),
            timeoutCount,
            quotedCount,
            wonCount,
            countByStatus(updated, InquiryMerchantLeadStatus.LOST),
            calcRate(quotedCount, updated.size()),
            calcRate(wonCount, quotedCount),
            averageResponseMinutes(updated)),
        List.of(
            new A03QuoteEfficiencyAgingBucketDTO(
                countByAgingMinutes(updated, 0, 10),
                countByAgingMinutes(updated, 10, 30),
                countByAgingMinutes(updated, 30, 60),
                countByAgingMinutes(updated, 60, Integer.MAX_VALUE),
                timeoutCount)),
        items,
        items.size(),
        1,
        items.size());
  }

  public A03QuoteEfficiencyTaskItemDTO quickQuote(String leadId, A03QuoteEfficiencyQuickQuoteRequest request) {
    String totalAmount = calcTotalAmount(leadId, request.unitPrice());
    InquiryMerchantLeadEntity updated =
        inquiryRepository.merchantLeadQuote(
            leadId,
            new InquiryMerchantLeadQuoteRequest(
                request.merchantId().trim(),
                request.supplierName().trim(),
                request.unitPrice().trim(),
                totalAmount,
                "含税到厂",
                request.deliveryDays().trim(),
                request.paymentTerm() == null || request.paymentTerm().isBlank()
                    ? "月结30天"
                    : request.paymentTerm().trim(),
                "YES",
                request.quoteRemark(),
                request.operator() == null || request.operator().isBlank()
                    ? "a03-efficiency-center"
                    : request.operator().trim()));
    return toTaskItem(updated);
  }

  private String calcTotalAmount(String leadId, String unitPrice) {
    InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
    BigDecimal qty = new BigDecimal(lead.getDemandQtyTon());
    BigDecimal unit = new BigDecimal(unitPrice.trim());
    return unit.multiply(qty).setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private A03QuoteEfficiencyTaskItemDTO toTaskItem(InquiryMerchantLeadEntity item) {
    String status = item.getStatus().name();
    String statusText =
        switch (status) {
          case "NEW" -> "待报价";
          case "FOLLOWING", "CONTACTED" -> "跟进中";
          case "QUOTED" -> "已报价";
          case "WON" -> "已赢单";
          case "LOST" -> "已丢单";
          case "CLOSED" -> "已关闭";
          default -> status;
        };
    long agingMinutes = calcAgingMinutes(item);
    boolean timeout = isTimeout(item);
    String efficiencyLevel = efficiencyLevel(agingMinutes);
    return new A03QuoteEfficiencyTaskItemDTO(
        item.getId(),
        item.getLeadNo(),
        item.getInquiryId(),
        item.getInquiryNo(),
        item.getMerchantId(),
        item.getMerchantName(),
        item.getSpecText(),
        item.getDemandQtyTon(),
        item.getDeliveryCity(),
        item.getExpectedDeliveryAt(),
        String.valueOf(Math.max(agingMinutes, 0)),
        efficiencyLevel,
        timeout,
        item.getUnitPrice() != null && !item.getUnitPrice().isBlank(),
        status,
        item.getQuoteRemark(),
        item.getUpdatedAt().toString());
  }

  private int countByStatus(List<InquiryMerchantLeadEntity> items, InquiryMerchantLeadStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int calcTimeoutCount(List<InquiryMerchantLeadEntity> items) {
    return (int) items.stream().filter(this::isTimeout).count();
  }

  private boolean isTimeout(InquiryMerchantLeadEntity item) {
    if (!(item.getStatus() == InquiryMerchantLeadStatus.NEW
        || item.getStatus() == InquiryMerchantLeadStatus.CONTACTED
        || item.getStatus() == InquiryMerchantLeadStatus.FOLLOWING)) {
      return false;
    }
    return calcAgingMinutes(item) >= 120;
  }

  private int countByAgingMinutes(List<InquiryMerchantLeadEntity> items, int minInclusive, int maxExclusive) {
    return (int)
        items.stream()
            .map(this::calcAgingMinutes)
            .filter(v -> v >= minInclusive && v < maxExclusive)
            .count();
  }

  private long calcAgingMinutes(InquiryMerchantLeadEntity item) {
    return Math.max(Duration.between(item.getCreatedAt(), item.getUpdatedAt()).toMinutes(), 0);
  }

  private String averageResponseMinutes(List<InquiryMerchantLeadEntity> items) {
    List<Integer> values =
        items.stream()
            .map(InquiryMerchantLeadEntity::getResponseMinutes)
            .filter(v -> v != null && !v.isBlank())
            .map(
                v -> {
                  try {
                    return Integer.parseInt(v.trim());
                  } catch (Exception ex) {
                    return null;
                  }
                })
            .filter(v -> v != null)
            .toList();
    if (values.isEmpty()) {
      return "0";
    }
    int sum = values.stream().mapToInt(Integer::intValue).sum();
    return String.valueOf(sum / values.size());
  }

  private String calcRate(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.0%";
    }
    double pct = numerator * 100.0 / denominator;
    return String.format(Locale.ROOT, "%.1f%%", pct);
  }

  private String efficiencyLevel(long minutes) {
    if (minutes <= 10) {
      return "FAST";
    }
    if (minutes <= 30) {
      return "NORMAL";
    }
    if (minutes <= 60) {
      return "WARNING";
    }
    return "SLOW";
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }
}
