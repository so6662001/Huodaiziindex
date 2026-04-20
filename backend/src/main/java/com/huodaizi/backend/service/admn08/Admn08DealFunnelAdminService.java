package com.huodaizi.backend.service.admn08;

import com.huodaizi.backend.dto.admn08.Admn08DealFunnelNodeDTO;
import com.huodaizi.backend.dto.admn08.Admn08DealFunnelRequest;
import com.huodaizi.backend.dto.admn08.Admn08DealFunnelResponse;
import com.huodaizi.backend.dto.admn08.Admn08DealFunnelTrendPointDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class Admn08DealFunnelAdminService {
  private final InMemoryInquiryRepository inquiryRepository;
  private final InMemorySiteAdLeadRepository siteAdLeadRepository;
  private final InMemoryAuthRepository authRepository;

  public Admn08DealFunnelAdminService(
      InMemoryInquiryRepository inquiryRepository,
      InMemorySiteAdLeadRepository siteAdLeadRepository,
      InMemoryAuthRepository authRepository) {
    this.inquiryRepository = inquiryRepository;
    this.siteAdLeadRepository = siteAdLeadRepository;
    this.authRepository = authRepository;
  }

  public Admn08DealFunnelResponse funnel(Admn08DealFunnelRequest request) {
    int days = request == null ? 30 : request.safeDays();
    String city = request == null ? "全国" : request.safeCity();
    LocalDate today = LocalDate.now();
    LocalDate startDate = today.minusDays(Math.max(days - 1L, 0L));

    List<InquiryEntity> inquiries = scopedInquiries(city, startDate, today);
    List<InquiryMerchantLeadEntity> leads = scopedLeads(city, startDate, today);
    List<InquiryPickupOrderEntity> pickups = scopedPickups(city, startDate, today);
    List<InquiryReconcileOrderEntity> reconciles = scopedReconciles(city, startDate, today);
    List<SiteAdLeadEntity> adLeads = scopedAdLeads(city, startDate, today);

    int inquiryCount = inquiries.size();
    int quotedCount = countLeadQuoted(leads);
    int wonCount = countLeadWon(leads);
    int dealCount = countInquiryDealDone(inquiries);
    int pickupCompletedCount = countPickupCompleted(pickups);
    int paidCount = countReconcilePaid(reconciles);

    BigDecimal paidAmountYuan =
        reconciles.stream()
            .filter(item -> item.getStatus() == InquiryReconcileOrderStatus.PAID)
            .map(item -> parseMoneyOrZero(item.getPaidAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    String inquiryToQuoteRate = ratePercent(quotedCount, inquiryCount);
    String quoteToWinRate = ratePercent(wonCount, quotedCount);
    String wonToDealRate = ratePercent(dealCount, wonCount);
    String dealToPickupRate = ratePercent(pickupCompletedCount, dealCount);
    String pickupToPaidRate = ratePercent(paidCount, pickupCompletedCount);

    String avgDealDays = avgDealDays(inquiries, InquiryStatus.DEAL_DONE);
    String avgPickupDays = avgPickupDaysFromPickup(pickups, InquiryPickupOrderStatus.COMPLETED);

    List<Admn08DealFunnelNodeDTO> nodes =
        List.of(
            node(
                "INQUIRY",
                "询价创建",
                inquiryCount,
                amountByInquiry(inquiries),
                "100.00%",
                dropRate(quotedCount, inquiryCount)),
            node(
                "QUOTED",
                "商家报价",
                quotedCount,
                amountByLeads(leads, false),
                inquiryToQuoteRate,
                dropRate(wonCount, quotedCount)),
            node(
                "WON",
                "线索赢单",
                wonCount,
                amountByLeads(leads, true),
                quoteToWinRate,
                dropRate(dealCount, wonCount)),
            node(
                "DEAL_DONE",
                "成交确认",
                dealCount,
                amountByInquiryDeal(inquiries),
                wonToDealRate,
                dropRate(pickupCompletedCount, dealCount)),
            node(
                "PICKUP_DONE",
                "提货完成",
                pickupCompletedCount,
                amountByPickup(pickups),
                dealToPickupRate,
                dropRate(paidCount, pickupCompletedCount)),
            node(
                "PAID",
                "回款完成",
                paidCount,
                paidAmountYuan.toPlainString(),
                pickupToPaidRate,
                "0.00%"));

    List<Admn08DealFunnelTrendPointDTO> trends =
        buildTrend(days, today, inquiries, leads, reconciles).stream()
            .sorted(Comparator.comparing(Admn08DealFunnelTrendPointDTO::date))
            .toList();

    List<String> insights =
        buildInsights(
            inquiryCount,
            quotedCount,
            wonCount,
            dealCount,
            pickupCompletedCount,
            paidCount,
            adLeads);

    return new Admn08DealFunnelResponse(
        LocalDateTime.now().toString(),
        days,
        city,
        inquiryCount,
        quotedCount,
        wonCount,
        dealCount,
        pickupCompletedCount,
        paidCount,
        inquiryToQuoteRate,
        quoteToWinRate,
        wonToDealRate,
        dealToPickupRate,
        pickupToPaidRate,
        paidAmountYuan.toPlainString(),
        avgDealDays,
        avgPickupDays,
        nodes,
        trends,
        insights);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId) {
    authRepository.appendAuditLogForAdmin(
        "ADMN08",
        "DEAL_FUNNEL_QUERY",
        "FUNNEL",
        "OVERVIEW",
        operator,
        "ADMIN",
        traceId,
        "SUCCESS",
        "LOW",
        summary,
        "",
        "",
        "127.0.0.1",
        "admn08-service");
  }

  private List<InquiryEntity> scopedInquiries(String city, LocalDate startDate, LocalDate endDate) {
    return inquiryRepository.allInquiries().stream()
        .filter(item -> inWindow(item.getCreatedAt().toLocalDate(), startDate, endDate))
        .filter(item -> matchCity(city, item.getDeliveryCity()))
        .toList();
  }

  private List<InquiryMerchantLeadEntity> scopedLeads(String city, LocalDate startDate, LocalDate endDate) {
    return inquiryRepository.allMerchantLeads().stream()
        .filter(item -> inWindow(item.getCreatedAt().toLocalDate(), startDate, endDate))
        .filter(item -> matchCity(city, item.getDeliveryCity()))
        .toList();
  }

  private List<InquiryPickupOrderEntity> scopedPickups(String city, LocalDate startDate, LocalDate endDate) {
    return inquiryRepository.allPickupOrders().stream()
        .filter(item -> inWindow(item.getCreatedAt().toLocalDate(), startDate, endDate))
        .filter(item -> matchCity(city, item.getPickupAddress()))
        .toList();
  }

  private List<InquiryReconcileOrderEntity> scopedReconciles(String city, LocalDate startDate, LocalDate endDate) {
    return inquiryRepository.allReconcileOrders().stream()
        .filter(item -> inWindow(item.getCreatedAt().toLocalDate(), startDate, endDate))
        .filter(item -> matchCity(city, item.getBuyerCompany()))
        .toList();
  }

  private List<SiteAdLeadEntity> scopedAdLeads(String city, LocalDate startDate, LocalDate endDate) {
    return siteAdLeadRepository.allLeads().stream()
        .filter(item -> inWindow(item.getCreatedAt().toLocalDate(), startDate, endDate))
        .filter(item -> matchCity(city, item.getCity()))
        .toList();
  }

  private boolean inWindow(LocalDate date, LocalDate start, LocalDate end) {
    return date != null && !date.isBefore(start) && !date.isAfter(end);
  }

  private boolean matchCity(String city, String value) {
    String target = safeText(city);
    if (target.isBlank() || "全国".equals(target)) {
      return true;
    }
    return safeText(value).contains(target);
  }

  private int countLeadQuoted(List<InquiryMerchantLeadEntity> leads) {
    return (int)
        leads.stream()
            .filter(
                item ->
                    item.getStatus() == InquiryMerchantLeadStatus.QUOTED
                        || item.getStatus() == InquiryMerchantLeadStatus.WON
                        || item.getStatus() == InquiryMerchantLeadStatus.LOST
                        || item.getStatus() == InquiryMerchantLeadStatus.CLOSED)
            .count();
  }

  private int countLeadWon(List<InquiryMerchantLeadEntity> leads) {
    return (int) leads.stream().filter(item -> item.getStatus() == InquiryMerchantLeadStatus.WON).count();
  }

  private int countInquiryDealDone(List<InquiryEntity> inquiries) {
    return (int) inquiries.stream().filter(item -> item.getStatus() == InquiryStatus.DEAL_DONE).count();
  }

  private int countPickupCompleted(List<InquiryPickupOrderEntity> pickups) {
    return (int) pickups.stream().filter(item -> item.getStatus() == InquiryPickupOrderStatus.COMPLETED).count();
  }

  private int countReconcilePaid(List<InquiryReconcileOrderEntity> reconciles) {
    return (int) reconciles.stream().filter(item -> item.getStatus() == InquiryReconcileOrderStatus.PAID).count();
  }

  private String amountByInquiry(List<InquiryEntity> inquiries) {
    BigDecimal value =
        inquiries.stream()
            .map(item -> parseMoneyOrZero(item.getDemandQtyTon()).multiply(new BigDecimal("3500")))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private String amountByLeads(List<InquiryMerchantLeadEntity> leads, boolean onlyWon) {
    BigDecimal value =
        leads.stream()
            .filter(item -> !onlyWon || item.getStatus() == InquiryMerchantLeadStatus.WON)
            .map(item -> parseMoneyOrZero(item.getTotalAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private String amountByInquiryDeal(List<InquiryEntity> inquiries) {
    BigDecimal value =
        inquiries.stream()
            .filter(item -> item.getStatus() == InquiryStatus.DEAL_DONE)
            .map(item -> parseMoneyOrZero(item.getDemandQtyTon()).multiply(new BigDecimal("3600")))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private String amountByPickup(List<InquiryPickupOrderEntity> pickups) {
    BigDecimal value =
        pickups.stream()
            .filter(item -> item.getStatus() == InquiryPickupOrderStatus.COMPLETED)
            .map(item -> parseMoneyOrZero(item.getQuantityTon()).multiply(new BigDecimal("3600")))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private Admn08DealFunnelNodeDTO node(
      String stageCode,
      String stageName,
      int stageCount,
      String amountYuan,
      String conversionRate,
      String dropRate) {
    return new Admn08DealFunnelNodeDTO(
        stageCode, stageName, stageCount, amountYuan, conversionRate, dropRate);
  }

  private String ratePercent(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.00%";
    }
    BigDecimal value =
        BigDecimal.valueOf(numerator)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    return value.toPlainString() + "%";
  }

  private String dropRate(int nextCount, int currentCount) {
    if (currentCount <= 0) {
      return "0.00%";
    }
    int drop = Math.max(currentCount - nextCount, 0);
    return ratePercent(drop, currentCount);
  }

  private String avgDealDays(List<InquiryEntity> inquiries, InquiryStatus targetStatus) {
    BigDecimal total = BigDecimal.ZERO;
    int count = 0;
    for (InquiryEntity item : inquiries) {
      if (item.getStatus() == targetStatus) {
        long days =
            Math.max(
                item.getUpdatedAt().toLocalDate().toEpochDay()
                    - item.getCreatedAt().toLocalDate().toEpochDay(),
                0L);
        total = total.add(BigDecimal.valueOf(days));
        count++;
      }
    }
    if (count == 0) {
      return "0.00";
    }
    return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).toPlainString();
  }

  private String avgPickupDaysFromPickup(
      List<InquiryPickupOrderEntity> pickups, InquiryPickupOrderStatus targetStatus) {
    BigDecimal total = BigDecimal.ZERO;
    int count = 0;
    for (InquiryPickupOrderEntity item : pickups) {
      if (item.getStatus() == targetStatus) {
        long days =
            Math.max(
                item.getUpdatedAt().toLocalDate().toEpochDay()
                    - item.getCreatedAt().toLocalDate().toEpochDay(),
                0L);
        total = total.add(BigDecimal.valueOf(days));
        count++;
      }
    }
    if (count == 0) {
      return "0.00";
    }
    return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).toPlainString();
  }

  private List<Admn08DealFunnelTrendPointDTO> buildTrend(
      int days,
      LocalDate today,
      List<InquiryEntity> inquiries,
      List<InquiryMerchantLeadEntity> leads,
      List<InquiryReconcileOrderEntity> reconciles) {
    Map<String, Admn08DealFunnelTrendPointDTO> map = new LinkedHashMap<>();
    for (int i = days - 1; i >= 0; i--) {
      LocalDate d = today.minusDays(i);
      map.put(d.toString(), new Admn08DealFunnelTrendPointDTO(d.toString(), 0, 0, 0, 0, 0));
    }

    for (InquiryEntity item : inquiries) {
      mergeTrend(map, item.getCreatedAt().toLocalDate(), 1, 0, 0, 0, 0);
      if (item.getStatus() == InquiryStatus.DEAL_DONE) {
        mergeTrend(map, item.getUpdatedAt().toLocalDate(), 0, 0, 0, 1, 0);
      }
    }
    for (InquiryMerchantLeadEntity item : leads) {
      if (item.getStatus() == InquiryMerchantLeadStatus.QUOTED || item.getStatus() == InquiryMerchantLeadStatus.WON) {
        mergeTrend(map, item.getUpdatedAt().toLocalDate(), 0, 1, 0, 0, 0);
      }
      if (item.getStatus() == InquiryMerchantLeadStatus.WON) {
        mergeTrend(map, item.getUpdatedAt().toLocalDate(), 0, 0, 1, 0, 0);
      }
    }
    for (InquiryReconcileOrderEntity item : reconciles) {
      if (item.getStatus() == InquiryReconcileOrderStatus.PAID) {
        mergeTrend(map, item.getUpdatedAt().toLocalDate(), 0, 0, 0, 0, 1);
      }
    }
    return new ArrayList<>(map.values());
  }

  private void mergeTrend(
      Map<String, Admn08DealFunnelTrendPointDTO> map,
      LocalDate date,
      int leadInc,
      int quoteInc,
      int wonInc,
      int dealInc,
      int paidInc) {
    if (date == null) {
      return;
    }
    Admn08DealFunnelTrendPointDTO current = map.get(date.toString());
    if (current == null) {
      return;
    }
    map.put(
        date.toString(),
        new Admn08DealFunnelTrendPointDTO(
            current.date(),
            current.leadCreatedCount() + leadInc,
            current.quotedCount() + quoteInc,
            current.wonCount() + wonInc,
            current.dealCount() + dealInc,
            current.paidCount() + paidInc));
  }

  private List<String> buildInsights(
      int inquiryCount,
      int quotedCount,
      int wonCount,
      int dealCount,
      int pickupCompletedCount,
      int paidCount,
      List<SiteAdLeadEntity> adLeads) {
    List<String> insights = new ArrayList<>();
    insights.add("询价→报价转化率 " + ratePercent(quotedCount, inquiryCount) + "，建议优先补齐高频规格自动报价规则。");
    insights.add("赢单→成交确认转化率 " + ratePercent(dealCount, Math.max(wonCount, 1)) + "，建议缩短赢单后确认时滞。");
    insights.add("履约→回款转化率 " + ratePercent(paidCount, Math.max(pickupCompletedCount, 1)) + "，建议关注争议单与账期拉长客户。");
    int convertedAd = (int) adLeads.stream().filter(item -> item.getStatus() == SiteAdLeadStatus.CONVERTED).count();
    insights.add("广告线索成交 " + convertedAd + " 条，可联合 A02 线索运营做来源质量分层。");
    return insights;
  }

  private BigDecimal parseMoneyOrZero(String value) {
    if (value == null || value.isBlank()) {
      return BigDecimal.ZERO;
    }
    try {
      return new BigDecimal(value.trim());
    } catch (NumberFormatException ex) {
      return BigDecimal.ZERO;
    }
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }
}
