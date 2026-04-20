package com.huodaizi.backend.repository.planpricing;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingFeatureDTO;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingItemDTO;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListRequest;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListResponse;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingUpdateRequest;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingUpdateRequest.A07PlanPricingFeatureRequest;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryBillingOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantSubscriptionEntity;
import com.huodaizi.backend.repository.inquiry.InquirySubscriptionPlanEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class A07PlanPricingRepository {
  private final InMemoryInquiryRepository inquiryRepository;

  public A07PlanPricingRepository(InMemoryInquiryRepository inquiryRepository) {
    this.inquiryRepository = inquiryRepository;
  }

  public A07PlanPricingListResponse list(A07PlanPricingListRequest request) {
    String keyword = normalize(request.keyword());
    String planType = normalize(request.planType());
    List<InquirySubscriptionPlanEntity> allPlans = inquiryRepository.allSubscriptionPlans().stream()
        .filter(item -> planType == null || normalize(item.getPlanType()).equals(planType))
        .filter(item -> !request.hasEnabledFilter() || item.isEnabled() == request.safeEnabledOnly())
        .filter(item -> !request.hasRecommendedFilter() || item.isRecommended() == request.safeRecommendedOnly())
        .filter(item -> keyword == null || containsKeyword(item, keyword))
        .sorted(Comparator.comparing(InquirySubscriptionPlanEntity::getPlanCode))
        .toList();

    List<InquiryMerchantSubscriptionEntity> allSubscriptions = inquiryRepository.allSubscriptions();
    List<InquiryBillingOrderEntity> allBills = inquiryRepository.allBillingOrders();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, allPlans.size());
    List<InquirySubscriptionPlanEntity> paged = from >= allPlans.size() ? List.of() : allPlans.subList(from, to);

    int enabledCount = (int) allPlans.stream().filter(InquirySubscriptionPlanEntity::isEnabled).count();
    int recommendedCount = (int) allPlans.stream().filter(InquirySubscriptionPlanEntity::isRecommended).count();
    int total = allPlans.size();
    int disabledCount = Math.max(total - enabledCount, 0);

    BigDecimal totalMonthly = allPlans.stream()
        .map(item -> parseMoney(item.getPrice(), "price"))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    String avgDiscountRate = calcAvgDiscountRate(allPlans);

    return new A07PlanPricingListResponse(
        paged.stream().map(item -> toItem(item, allSubscriptions, allBills)).toList(),
        total,
        page,
        pageSize,
        enabledCount,
        disabledCount,
        recommendedCount,
        avgDiscountRate,
        toMoney(totalMonthly),
        LocalDateTime.now().toString());
  }

  public A07PlanPricingItemDTO update(String planCode, A07PlanPricingUpdateRequest request) {
    InquirySubscriptionPlanEntity current = inquiryRepository.getSubscriptionPlanByCode(planCode);
    if (!current.getPlanCode().equalsIgnoreCase(request.planCode().trim())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "路径 planCode 与请求体不一致");
    }
    String normalizedCycle = normalizeBillingCycle(request.billingCycle());
    BigDecimal price = parsePositiveMoney(request.price(), "price");
    BigDecimal originalPrice = parsePositiveMoney(request.originalPrice(), "originalPrice");
    if (originalPrice.compareTo(price) < 0) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "originalPrice 不能小于 price");
    }

    List<InquirySubscriptionPlanEntity.FeatureEntity> features = request.features().stream()
        .sorted(Comparator.comparingInt(item -> item.sort() == null ? 99 : item.sort()))
        .map(this::toFeature)
        .toList();

    InquirySubscriptionPlanEntity updated = new InquirySubscriptionPlanEntity(
        current.getPlanId(),
        current.getPlanCode(),
        request.planName().trim(),
        request.planType().trim().toUpperCase(Locale.ROOT),
        normalizedCycle,
        toMoney(price),
        toMoney(originalPrice),
        request.recommended(),
        request.suitableFor().trim(),
        request.enabled(),
        features,
        LocalDateTime.now(),
        request.operator().trim());
    inquiryRepository.saveSubscriptionPlan(updated);

    return toItem(updated, inquiryRepository.allSubscriptions(), inquiryRepository.allBillingOrders());
  }

  private InquirySubscriptionPlanEntity.FeatureEntity toFeature(A07PlanPricingFeatureRequest item) {
    return new InquirySubscriptionPlanEntity.FeatureEntity(
        item.code().trim().toUpperCase(Locale.ROOT),
        item.name().trim(),
        item.value().trim(),
        defaultText(item.highlight(), "标准"));
  }

  private A07PlanPricingItemDTO toItem(
      InquirySubscriptionPlanEntity plan,
      List<InquiryMerchantSubscriptionEntity> allSubscriptions,
      List<InquiryBillingOrderEntity> allBills) {
    int activeSubscriptionCount =
        (int)
            allSubscriptions.stream()
                .filter(item -> item.getPlanCode().equalsIgnoreCase(plan.getPlanCode()))
                .filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus()))
                .count();
    int pendingBillCount =
        (int)
            allBills.stream()
                .filter(item -> item.getPlanCode().equalsIgnoreCase(plan.getPlanCode()))
                .filter(
                    item ->
                        "UNPAID".equalsIgnoreCase(item.getStatus())
                            || "PARTIAL_PAID".equalsIgnoreCase(item.getStatus()))
                .count();
    BigDecimal totalRevenue =
        allBills.stream()
            .filter(item -> item.getPlanCode().equalsIgnoreCase(plan.getPlanCode()))
            .map(item -> parseMoneyOrDefault(item.getPaidAmountYuan(), BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new A07PlanPricingItemDTO(
        plan.getPlanId(),
        plan.getPlanCode(),
        plan.getPlanName(),
        plan.getPlanType(),
        plan.getBillingCycle(),
        plan.getPrice(),
        plan.getOriginalPrice(),
        plan.isRecommended(),
        plan.isEnabled(),
        plan.getSuitableFor(),
        plan.getFeatures().size(),
        activeSubscriptionCount,
        pendingBillCount,
        toMoney(totalRevenue),
        plan.getUpdatedAt().toString(),
        plan.getUpdatedBy(),
        plan.getFeatures().stream()
            .map(feature -> new A07PlanPricingFeatureDTO(feature.key(), feature.label(), feature.value(), feature.highlight()))
            .toList());
  }

  private String calcAvgDiscountRate(List<InquirySubscriptionPlanEntity> plans) {
    if (plans.isEmpty()) {
      return "0.0%";
    }
    BigDecimal sumRate = BigDecimal.ZERO;
    for (InquirySubscriptionPlanEntity plan : plans) {
      BigDecimal price = parsePositiveMoney(plan.getPrice(), "price");
      BigDecimal originalPrice = parsePositiveMoney(plan.getOriginalPrice(), "originalPrice");
      if (originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }
      BigDecimal rate =
          price
              .divide(originalPrice, 4, java.math.RoundingMode.HALF_UP)
              .multiply(new BigDecimal("100"));
      sumRate = sumRate.add(rate);
    }
    BigDecimal avg = sumRate.divide(new BigDecimal(plans.size()), 1, java.math.RoundingMode.HALF_UP);
    return avg.stripTrailingZeros().toPlainString() + "%";
  }

  private String normalizeBillingCycle(String cycle) {
    String normalized = defaultText(cycle, "").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "MONTHLY", "QUARTERLY", "YEARLY" -> normalized;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "billingCycle 仅支持 MONTHLY/QUARTERLY/YEARLY");
    };
  }

  private boolean containsKeyword(InquirySubscriptionPlanEntity item, String keyword) {
    return normalize(item.getPlanCode()).contains(keyword)
        || normalize(item.getPlanName()).contains(keyword)
        || normalize(item.getPlanType()).contains(keyword)
        || normalize(item.getSuitableFor()).contains(keyword);
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }

  private BigDecimal parsePositiveMoney(String text, String field) {
    BigDecimal value = parseMoney(text, field);
    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), field + " 必须大于0");
    }
    return value;
  }

  private BigDecimal parseMoney(String text, String field) {
    String normalized = defaultText(text, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), field + " 不能为空");
    }
    try {
      return new BigDecimal(normalized);
    } catch (Exception ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), field + " 格式错误");
    }
  }

  private BigDecimal parseMoneyOrDefault(String text, BigDecimal fallback) {
    if (text == null || text.isBlank()) {
      return fallback;
    }
    try {
      return new BigDecimal(text.trim());
    } catch (Exception ex) {
      return fallback;
    }
  }

  private String toMoney(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
  }
}
