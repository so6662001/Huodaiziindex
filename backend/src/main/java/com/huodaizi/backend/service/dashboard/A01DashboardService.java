package com.huodaizi.backend.service.dashboard;

import com.huodaizi.backend.dto.dashboard.A01DashboardFunnelDTO;
import com.huodaizi.backend.dto.dashboard.A01DashboardOperationDTO;
import com.huodaizi.backend.dto.dashboard.A01DashboardOverviewDTO;
import com.huodaizi.backend.dto.dashboard.A01DashboardRequest;
import com.huodaizi.backend.dto.dashboard.A01DashboardResponse;
import com.huodaizi.backend.dto.dashboard.A01DashboardTrendPointDTO;
import com.huodaizi.backend.repository.dashboard.A01DashboardAggregate;
import com.huodaizi.backend.repository.dashboard.A01DashboardAggregateRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class A01DashboardService {
  private final A01DashboardAggregateRepository repository;

  public A01DashboardService(A01DashboardAggregateRepository repository) {
    this.repository = repository;
  }

  public A01DashboardResponse dashboard(A01DashboardRequest request) {
    A01DashboardAggregate aggregate = repository.aggregate(request);

    int inquiryTotal = aggregate.inquiryTotal();
    int inquiryOpenCount = aggregate.inquiryOpenCount();
    int inquiryQuotingCount = aggregate.inquiryQuotingCount();
    int inquiryDealDoneCount = aggregate.inquiryDealDoneCount();
    int inquiryClosedCount = aggregate.inquiryClosedCount();
    int merchantLeadTotal = aggregate.merchantLeadTotal();
    int merchantLeadQuotedCount = aggregate.merchantLeadQuotedCount();
    int merchantLeadWonCount = aggregate.merchantLeadWonCount();

    int pickupOrderTotal = aggregate.pickupOrderTotal();
    int pickupCompletedCount = aggregate.pickupCompletedCount();
    int reconcileOrderTotal = aggregate.reconcileOrderTotal();
    int reconcilePaidCount = aggregate.reconcilePaidCount();

    String leadToDealRate = ratePercent(merchantLeadWonCount, merchantLeadTotal);
    String dealToFulfillmentRate = ratePercent(pickupCompletedCount, inquiryDealDoneCount);
    String paidRate = ratePercent(reconcilePaidCount, reconcileOrderTotal);
    String adLeadConvertedRate = ratePercent(aggregate.adLeadConvertedCount(), aggregate.adLeadTotal());

    A01DashboardOverviewDTO overview =
        new A01DashboardOverviewDTO(
            inquiryTotal,
            inquiryOpenCount,
            inquiryQuotingCount,
            inquiryDealDoneCount,
            inquiryClosedCount,
            merchantLeadTotal,
            aggregate.merchantLeadNewCount(),
            merchantLeadQuotedCount,
            merchantLeadWonCount,
            pickupOrderTotal,
            aggregate.pickupInTransitCount(),
            pickupCompletedCount,
            reconcileOrderTotal,
            aggregate.reconcilePartialPaidCount(),
            reconcilePaidCount,
            aggregate.subscriptionActiveCount(),
            aggregate.subscriptionExpiringSoonCount(),
            aggregate.billingOutstandingCount(),
            toMoney(aggregate.billingOutstandingAmountYuan()),
            aggregate.adLeadTotal(),
            aggregate.adLeadConvertedCount(),
            leadToDealRate,
            dealToFulfillmentRate,
            paidRate,
            adLeadConvertedRate);

    A01DashboardFunnelDTO funnel =
        new A01DashboardFunnelDTO(
            inquiryTotal,
            merchantLeadQuotedCount,
            merchantLeadWonCount,
            inquiryDealDoneCount,
            pickupCompletedCount,
            reconcilePaidCount,
            ratePercent(merchantLeadQuotedCount, inquiryTotal),
            ratePercent(merchantLeadWonCount, merchantLeadQuotedCount),
            ratePercent(inquiryDealDoneCount, inquiryTotal),
            ratePercent(pickupCompletedCount, inquiryDealDoneCount),
            ratePercent(reconcilePaidCount, reconcileOrderTotal));

    A01DashboardOperationDTO operations =
        new A01DashboardOperationDTO(
            aggregate.adLeadSubmittedCount(),
            aggregate.adLeadAssignedCount(),
            aggregate.subscriptionExpiringSoonCount(),
            aggregate.subscriptionExpiredCount(),
            aggregate.reconcileDisputedCount(),
            aggregate.pickupInTransitCount(),
            aggregate.pickupCancelledCount(),
            "建议优先处理争议对账单与临近到期会员，稳定回款与续费转化");

    List<A01DashboardTrendPointDTO> trends =
        aggregate.dailyPoints().stream()
            .map(
                point ->
                    new A01DashboardTrendPointDTO(
                        point.date(),
                        point.inquiryCount(),
                        point.dealCount(),
                        point.pickupCount(),
                        point.paidCount(),
                        point.adLeadCount()))
            .toList();

    return new A01DashboardResponse(
        LocalDateTime.now().toString(),
        request.safeDays(),
        request.safeCity(),
        overview,
        trends,
        funnel,
        operations);
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

  private String toMoney(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }
}
