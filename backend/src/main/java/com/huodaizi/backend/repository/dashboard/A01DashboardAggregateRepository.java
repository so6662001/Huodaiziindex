package com.huodaizi.backend.repository.dashboard;

import com.huodaizi.backend.dto.dashboard.A01DashboardRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryBillingOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantSubscriptionEntity;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class A01DashboardAggregateRepository {

  private final InMemoryInquiryRepository inquiryRepository;
  private final InMemorySiteAdLeadRepository siteAdLeadRepository;

  public A01DashboardAggregateRepository(
      InMemoryInquiryRepository inquiryRepository, InMemorySiteAdLeadRepository siteAdLeadRepository) {
    this.inquiryRepository = inquiryRepository;
    this.siteAdLeadRepository = siteAdLeadRepository;
  }

  public A01DashboardAggregate aggregate(A01DashboardRequest request) {
    int windowDays = request.safeDays();
    LocalDate today = LocalDate.now();
    LocalDate startDate = today.minusDays(Math.max(windowDays - 1L, 0L));

    List<InquiryEntity> inquiries = inquiryRepository.allInquiries();
    List<InquiryMerchantLeadEntity> leads = inquiryRepository.allMerchantLeads();
    List<InquiryBillingOrderEntity> billings = inquiryRepository.allBillingOrders();
    List<InquiryPickupOrderEntity> pickups = inquiryRepository.allPickupOrders();
    List<InquiryReconcileOrderEntity> reconciles = inquiryRepository.allReconcileOrders();
    List<InquiryMerchantSubscriptionEntity> subscriptions = inquiryRepository.allSubscriptions();
    List<SiteAdLeadEntity> adLeads = siteAdLeadRepository.allLeads();

    int inquiryTotal = inquiries.size();
    int inquiryOpenCount = countInquiryByStatus(inquiries, InquiryStatus.OPEN);
    int inquiryQuotingCount = countInquiryByStatus(inquiries, InquiryStatus.QUOTING);
    int inquiryDealDoneCount = countInquiryByStatus(inquiries, InquiryStatus.DEAL_DONE);
    int inquiryClosedCount = countInquiryByStatus(inquiries, InquiryStatus.CLOSED);

    List<InquiryMerchantLeadEntity> scopedLeads = filterLeadsByDate(leads, startDate, today);
    int leadTotal = scopedLeads.size();
    int leadNewCount = countLeadByStatus(scopedLeads, InquiryMerchantLeadStatus.NEW);
    int leadQuotedCount = countLeadByStatus(scopedLeads, InquiryMerchantLeadStatus.QUOTED);
    int leadWonCount = countLeadByStatus(scopedLeads, InquiryMerchantLeadStatus.WON);
    int leadLostCount = countLeadByStatus(scopedLeads, InquiryMerchantLeadStatus.LOST);

    int pickupTotal = pickups.size();
    int pickupInTransitCount = countPickupByStatus(pickups, InquiryPickupOrderStatus.IN_TRANSIT);
    int pickupCompletedCount = countPickupByStatus(pickups, InquiryPickupOrderStatus.COMPLETED);
    int pickupCancelledCount = countPickupByStatus(pickups, InquiryPickupOrderStatus.CANCELLED);

    int reconcileTotal = reconciles.size();
    int reconcilePartialPaidCount =
        countReconcileByStatus(reconciles, InquiryReconcileOrderStatus.PARTIAL_PAID);
    int reconcilePaidCount = countReconcileByStatus(reconciles, InquiryReconcileOrderStatus.PAID);
    int reconcileDisputedCount = countReconcileByStatus(reconciles, InquiryReconcileOrderStatus.DISPUTED);

    int memberTotal = subscriptions.size();
    int subscriptionActiveCount =
        (int) subscriptions.stream().filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus())).count();
    int memberExpiringSoonCount =
        (int) subscriptions.stream().filter(item -> isExpiringWithinDays(item.getEndAt(), 15)).count();
    int memberExpiredCount =
        (int) subscriptions.stream().filter(item -> isExpired(item.getEndAt())).count();

    int billingOutstandingCount =
        (int)
            billings.stream()
                .filter(item -> !"PAID".equalsIgnoreCase(defaultText(item.getStatus(), "")))
                .count();
    BigDecimal billingOutstandingAmountYuan =
        billings.stream()
            .map(item -> parseMoneyOrZero(item.getUnpaidAmountYuan()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    int adLeadSubmittedCount = countAdLeadByStatus(adLeads, SiteAdLeadStatus.SUBMITTED);
    int adLeadAssignedCount = countAdLeadByStatus(adLeads, SiteAdLeadStatus.ASSIGNED);
    int adLeadContactedCount = countAdLeadByStatus(adLeads, SiteAdLeadStatus.CONTACTED);
    int adLeadProposalSentCount = countAdLeadByStatus(adLeads, SiteAdLeadStatus.PROPOSAL_SENT);
    int adLeadTotal = adLeads.size();
    int adLeadConvertedCount = countAdLeadByStatus(adLeads, SiteAdLeadStatus.CONVERTED);

    BigDecimal paidAmountYuan =
        reconciles.stream()
            .map(item -> parseMoneyOrZero(item.getPaidAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal receivableAmountYuan =
        reconciles.stream()
            .map(item -> parseMoneyOrZero(item.getReceivableAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal outstandingAmountYuan =
        receivableAmountYuan.subtract(paidAmountYuan).max(BigDecimal.ZERO);

    BigDecimal leadAmountYuan =
        leads.stream()
            .map(item -> parseMoneyOrZero(item.getTotalAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal subscriptionAmountYuan =
        billings.stream()
            .map(item -> parseMoneyOrZero(item.getAmountYuan()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal adLeadBudgetAmountYuan =
        adLeads.stream()
            .map(item -> parseBudgetMidpoint(item.getBudget()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    String averageResponseMinutes =
        averageResponseMinutes(
            leads.stream().map(item -> item.getResponseMinutes()).collect(Collectors.toList()));
    String pickupOnTimeRate = calcPickupOnTimeRate(pickupCompletedCount, pickupTotal);

    List<A01DashboardAggregate.DailyPoint> dailyPoints =
        buildDailyPoints(
            windowDays, today, inquiries, scopedLeads, pickups, reconciles, subscriptions, adLeads);

    return new A01DashboardAggregate(
        inquiryTotal,
        inquiryOpenCount,
        inquiryQuotingCount,
        inquiryDealDoneCount,
        inquiryClosedCount,
        leadTotal,
        leadNewCount,
        leadQuotedCount,
        leadWonCount,
        leadLostCount,
        pickupTotal,
        pickupInTransitCount,
        0,
        pickupCompletedCount,
        pickupCancelledCount,
        reconcileTotal,
        reconcilePartialPaidCount,
        reconcilePaidCount,
        reconcileDisputedCount,
        memberTotal,
        subscriptionActiveCount,
        memberExpiringSoonCount,
        memberExpiredCount,
        billingOutstandingCount,
        billingOutstandingAmountYuan,
        adLeadTotal,
        adLeadSubmittedCount,
        adLeadAssignedCount,
        adLeadContactedCount,
        adLeadProposalSentCount,
        adLeadConvertedCount,
        paidAmountYuan,
        receivableAmountYuan,
        outstandingAmountYuan,
        leadAmountYuan,
        subscriptionAmountYuan,
        adLeadBudgetAmountYuan,
        pickupOnTimeRate,
        averageResponseMinutes,
        dailyPoints);
  }

  private List<InquiryMerchantLeadEntity> filterLeadsByDate(
      List<InquiryMerchantLeadEntity> leads, LocalDate startDate, LocalDate endDate) {
    return leads.stream()
        .filter(
            item -> {
              LocalDate date = item.getCreatedAt().toLocalDate();
              return !(date.isBefore(startDate) || date.isAfter(endDate));
            })
        .toList();
  }

  private boolean isExpiringWithinDays(String dateText, int days) {
    LocalDate end = parseLocalDate(dateText);
    if (end == null) {
      return false;
    }
    LocalDate now = LocalDate.now();
    return !end.isBefore(now) && !end.isAfter(now.plusDays(days));
  }

  private boolean isExpired(String dateText) {
    LocalDate end = parseLocalDate(dateText);
    return end != null && end.isBefore(LocalDate.now());
  }

  private LocalDate parseLocalDate(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return LocalDate.parse(value.trim());
    } catch (Exception ex) {
      return null;
    }
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String averageResponseMinutes(List<String> responseMinutes) {
    BigDecimal total = BigDecimal.ZERO;
    int count = 0;
    for (String item : responseMinutes) {
      BigDecimal val = parseMoneyOrZero(item);
      if (val.compareTo(BigDecimal.ZERO) > 0) {
        total = total.add(val);
        count++;
      }
    }
    if (count == 0) {
      return "0";
    }
    return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).toPlainString();
  }

  private String calcPickupOnTimeRate(int completed, int total) {
    if (total <= 0) {
      return "0.00%";
    }
    BigDecimal value =
        BigDecimal.valueOf(completed)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    return value.toPlainString() + "%";
  }

  private List<A01DashboardAggregate.DailyPoint> buildDailyPoints(
      int windowDays,
      LocalDate today,
      List<InquiryEntity> inquiries,
      List<InquiryMerchantLeadEntity> leads,
      List<InquiryPickupOrderEntity> pickups,
      List<InquiryReconcileOrderEntity> reconciles,
      List<InquiryMerchantSubscriptionEntity> subscriptions,
      List<SiteAdLeadEntity> adLeads) {
    Map<String, A01DashboardAggregate.DailyPoint> pointMap = new LinkedHashMap<>();
    for (int i = windowDays - 1; i >= 0; i--) {
      LocalDate d = today.minusDays(i);
      pointMap.put(
          d.toString(), new A01DashboardAggregate.DailyPoint(d.toString(), 0, 0, 0, 0, 0, 0, 0, "0.00"));
    }

    for (InquiryEntity item : inquiries) {
      updatePoint(pointMap, item.getCreatedAt().toLocalDate(), 1, 0, 0, 0, 0, 0, 0, BigDecimal.ZERO);
      if (item.getStatus() == InquiryStatus.DEAL_DONE) {
        updatePoint(pointMap, item.getUpdatedAt().toLocalDate(), 0, 0, 1, 0, 0, 0, 0, BigDecimal.ZERO);
      }
    }
    for (InquiryMerchantLeadEntity item : leads) {
      if (item.getStatus() == InquiryMerchantLeadStatus.QUOTED
          || item.getStatus() == InquiryMerchantLeadStatus.WON) {
        updatePoint(pointMap, item.getUpdatedAt().toLocalDate(), 0, 1, 0, 0, 0, 0, 0, BigDecimal.ZERO);
      }
    }
    for (InquiryPickupOrderEntity item : pickups) {
      if (item.getStatus() == InquiryPickupOrderStatus.COMPLETED) {
        updatePoint(pointMap, item.getUpdatedAt().toLocalDate(), 0, 0, 0, 1, 0, 0, 0, BigDecimal.ZERO);
      }
    }
    for (InquiryReconcileOrderEntity item : reconciles) {
      if (item.getStatus() == InquiryReconcileOrderStatus.PAID) {
        BigDecimal paidAmount = parseMoneyOrZero(item.getPaidAmount());
        updatePoint(pointMap, item.getUpdatedAt().toLocalDate(), 0, 0, 0, 0, 1, 0, 0, paidAmount);
      }
    }
    for (InquiryMerchantSubscriptionEntity item : subscriptions) {
      updatePoint(pointMap, item.getCreatedAt().toLocalDate(), 0, 0, 0, 0, 0, 1, 0, BigDecimal.ZERO);
    }
    for (SiteAdLeadEntity item : adLeads) {
      if (item.getStatus() == SiteAdLeadStatus.CONVERTED) {
        updatePoint(pointMap, item.getUpdatedAt().toLocalDate(), 0, 0, 0, 0, 0, 0, 1, BigDecimal.ZERO);
      }
    }

    return pointMap.values().stream().sorted(Comparator.comparing(A01DashboardAggregate.DailyPoint::date)).toList();
  }

  private void updatePoint(
      Map<String, A01DashboardAggregate.DailyPoint> pointMap,
      LocalDate date,
      int inquiryInc,
      int quoteInc,
      int dealInc,
      int pickupInc,
      int paidInc,
      int memberInc,
      int adLeadInc,
      BigDecimal paidAmountYuanInc) {
    if (date == null) {
      return;
    }
    A01DashboardAggregate.DailyPoint current = pointMap.get(date.toString());
    if (current == null) {
      return;
    }
    pointMap.put(
        date.toString(),
        new A01DashboardAggregate.DailyPoint(
            current.date(),
            current.inquiryCount() + inquiryInc,
            current.quoteCount() + quoteInc,
            current.dealCount() + dealInc,
            current.pickupCount() + pickupInc,
            current.paidCount() + paidInc,
            current.memberOpenCount() + memberInc,
            current.adLeadCount() + adLeadInc,
            parseMoneyOrZero(current.paidAmountYuan())
                .add(paidAmountYuanInc == null ? BigDecimal.ZERO : paidAmountYuanInc)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString()));
  }

  private int countInquiryByStatus(List<InquiryEntity> items, InquiryStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int countLeadByStatus(List<InquiryMerchantLeadEntity> items, InquiryMerchantLeadStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int countPickupByStatus(List<InquiryPickupOrderEntity> items, InquiryPickupOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int countReconcileByStatus(List<InquiryReconcileOrderEntity> items, InquiryReconcileOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int countAdLeadByStatus(List<SiteAdLeadEntity> items, SiteAdLeadStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
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

  private BigDecimal parseBudgetMidpoint(String budget) {
    if (budget == null || budget.isBlank()) {
      return BigDecimal.ZERO;
    }
    String normalized = budget.replaceAll("\\s+", "");
    String[] parts = normalized.split("-");
    if (parts.length == 2) {
      BigDecimal left = parseMoneyOrZero(parts[0]);
      BigDecimal right = parseMoneyOrZero(parts[1]);
      if (left.compareTo(BigDecimal.ZERO) > 0 && right.compareTo(BigDecimal.ZERO) > 0) {
        return left.add(right).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
      }
    }
    return parseMoneyOrZero(normalized);
  }
}
