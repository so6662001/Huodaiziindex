package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteSortBy;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderPaymentRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterReadAllRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterReadRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1SaveRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep2SubmitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchBatchUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchOverviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleRequest;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryInquiryRepository {
  private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final AtomicLong pickupSeq = new AtomicLong(20260418000L);
  private final AtomicLong reconcileSeq = new AtomicLong(20260418000L);
  private final AtomicLong subscriptionSeq = new AtomicLong(20260418000L);
  private final AtomicLong billingSeq = new AtomicLong(20260418000L);
  private final AtomicLong h5InquiryDraftSeq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, InquiryEntity> store = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, List<InquiryQuoteCompareEntity>> quoteStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantLeadEntity> merchantLeadStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantCreditScoreEntity> merchantCreditScoreStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquirySubscriptionPlanEntity> subscriptionPlanStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantSubscriptionEntity> merchantSubscriptionStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryBillingOrderEntity> billingOrderStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryDispatchScoreRuleEntity> dispatchRuleStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMessageCenterEntity> messageCenterStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryH5HomeEntity> h5HomeStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryH5InquiryStep1DraftEntity> h5InquiryStep1DraftStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryPickupOrderEntity> pickupOrderStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryReconcileOrderEntity> reconcileOrderStore =
      new ConcurrentHashMap<>();

  public InMemoryInquiryRepository() {
    seed();
  }

  public InquiryEntity create(InquiryCreateRequest request) {
    String id = "IQ" + seq.incrementAndGet();
    String inquiryNo = "INQ-" + NO_FMT.format(LocalDateTime.now()) + "-" + id;
    InquiryEntity entity =
        new InquiryEntity(
            id,
            inquiryNo,
            request.contactMobile().trim(),
            request.categoryCode().trim().toUpperCase(Locale.ROOT),
            request.specText().trim(),
            request.deliveryCity().trim(),
            request.demandQtyTon().stripTrailingZeros().toPlainString(),
            defaultText(request.expectedDeliveryAt(), "-"),
            defaultText(request.invoiceNeed(), "ANY").trim().toUpperCase(Locale.ROOT),
            defaultText(request.remark(), "-"),
            InquiryStatus.OPEN,
            0,
            LocalDateTime.now(),
            LocalDateTime.now());
    store.put(id, entity);
    quoteStore.put(id, mockQuoteRows(id));
    return entity;
  }

  public List<InquiryEntity> listMine(InquiryListRequest request) {
    String phone = normalizePhoneOrNull(request.contactMobile());
    String keyword = normalize(request.keyword());
    InquiryStatus status = normalizeStatusOrNull(request.status());
    return store.values().stream()
        .filter(item -> phone == null || normalizePhone(item.getContactMobile()).equals(phone))
        .filter(item -> status == null || item.getStatus() == status)
        .filter(
            item ->
                keyword == null
                    || normalize(item.getInquiryNo()).contains(keyword)
                    || normalize(item.getSpecText()).contains(keyword)
                    || normalize(item.getDeliveryCity()).contains(keyword))
        .sorted(Comparator.comparing(InquiryEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<InquiryQuoteCompareEntity> listQuoteCompareItems(InquiryQuoteCompareRequest request) {
    // ensure inquiry exists first
    getById(request.inquiryId());

    List<InquiryQuoteCompareEntity> quoteRows = quoteStore.getOrDefault(request.inquiryId(), List.of());
    String deliveryCycle = normalize(request.deliveryCycle());
    String invoiceType = normalize(request.invoiceType());
    InquiryQuoteSortBy sortBy = InquiryQuoteSortBy.fromOrDefault(request.sortBy());

    Comparator<InquiryQuoteCompareEntity> comparator =
        switch (sortBy) {
          case TOTAL_PRICE ->
              Comparator.comparing(
                  row -> new BigDecimal(defaultText(row.getTotalAmount(), "0")));
          case UNIT_PRICE ->
              Comparator.comparing(
                  row -> new BigDecimal(defaultText(row.getPricePerTon(), "0")));
          case DELIVERY_HOURS ->
              Comparator.comparing(
                  row -> new BigDecimal(defaultText(row.getDeliveryDays(), "0")));
          case RESPONSE_MINUTES ->
              Comparator.comparing(
                  row -> new BigDecimal(defaultText(row.getResponseMinutes(), "0")));
          case SUPPLIER_SCORE ->
              Comparator.comparing(
                      (InquiryQuoteCompareEntity row) ->
                          new BigDecimal(defaultText(row.getServiceScore(), "0")))
                  .reversed();
        };

    return quoteRows.stream()
        .filter(row -> deliveryCycle == null || normalize(row.getDeliveryDays()).equals(deliveryCycle))
        .filter(row -> invoiceType == null || normalize(row.getCanInvoice()).equals(invoiceType))
        .sorted(comparator)
        .toList();
  }

  public List<InquiryMerchantLeadEntity> listMerchantLeads(InquiryMerchantLeadListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    String keyword = normalize(request.keyword());
    InquiryMerchantLeadStatus status = normalizeMerchantLeadStatusOrNull(request.status());
    return merchantLeadStore.values().stream()
        .filter(item -> item.getMerchantId().equalsIgnoreCase(merchantId))
        .filter(item -> status == null || item.getStatus() == status)
        .filter(
            item ->
                keyword == null
                    || normalize(item.getInquiryNo()).contains(keyword)
                    || normalize(item.getSpecText()).contains(keyword)
                    || normalize(item.getMerchantName()).contains(keyword)
                    || normalize(item.getDeliveryCity()).contains(keyword))
        .sorted(Comparator.comparing(InquiryMerchantLeadEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public InquiryMerchantLeadEntity merchantLeadDetail(String leadId, String merchantId) {
    InquiryMerchantLeadEntity entity = requireMerchantLead(leadId);
    if (merchantId != null
        && !merchantId.isBlank()
        && !entity.getMerchantId().equalsIgnoreCase(merchantId.trim())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "线索不存在");
    }
    return entity;
  }

  public InquiryMerchantLeadEntity getMerchantLeadById(String leadId) {
    return requireMerchantLead(leadId);
  }

  public InquiryMerchantLeadEntity merchantLeadQuote(
      String leadId, InquiryMerchantLeadQuoteRequest request) {
    InquiryMerchantLeadEntity entity = requireMerchantLead(leadId);
    if (!entity.getMerchantId().equalsIgnoreCase(request.merchantId().trim())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "线索不存在");
    }
    entity.updateQuote(
        request.unitPrice().trim(),
        request.totalAmount().trim(),
        request.deliveryDays().trim(),
        defaultText(request.paymentTerm(), "-"),
        defaultText(request.quoteRemark(), "-"));
    return entity;
  }

  public InquiryMerchantLeadEntity merchantLeadUpdateStatus(
      String leadId, InquiryMerchantLeadStatusUpdateRequest request) {
    InquiryMerchantLeadEntity entity = requireMerchantLead(leadId);
    if (!entity.getMerchantId().equalsIgnoreCase(request.merchantId().trim())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "线索不存在");
    }
    InquiryMerchantLeadStatus status = normalizeMerchantLeadStatus(request.status());
    entity.updateStatus(status, request.comment());
    return entity;
  }

  public InquiryMerchantCreditScoreEntity merchantCreditScore(InquiryMerchantCreditScoreRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    InquiryMerchantCreditScoreEntity entity = merchantCreditScoreStore.get(merchantId.toUpperCase(Locale.ROOT));
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "商家信用评分不存在");
    }
    return entity;
  }

  public List<InquirySubscriptionPlanEntity> listSubscriptionPlans(InquirySubscriptionPlanListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    return subscriptionPlanStore.values().stream()
        .sorted(Comparator.comparing(InquirySubscriptionPlanEntity::getPlanCode))
        .toList();
  }

  public InquiryMerchantSubscriptionEntity createSubscription(InquirySubscriptionCreateRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    InquirySubscriptionPlanEntity plan = requireSubscriptionPlan(request.planCode());
    LocalDateTime now = LocalDateTime.now();
    String subscriptionId = "SUB" + subscriptionSeq.incrementAndGet();
    String status = "ACTIVE";
    String startDate = now.toLocalDate().toString();
    int periodMonths = "YEARLY".equalsIgnoreCase(request.billingCycle()) ? 12 : 1;
    String endDate = now.toLocalDate().plusMonths(periodMonths).toString();
    String amountYuan = plan.getPrice();
    if ("YEARLY".equalsIgnoreCase(request.billingCycle())) {
      amountYuan = toMoney(parseMoney(plan.getPrice(), "price").multiply(new BigDecimal("12")));
    }
    List<String> entitlements =
        plan.getFeatures().stream().map(InquirySubscriptionPlanEntity.FeatureEntity::label).toList();
    InquiryMerchantSubscriptionEntity entity =
        new InquiryMerchantSubscriptionEntity(
            subscriptionId,
            "SUB-" + NO_FMT.format(now) + "-" + subscriptionId,
            merchantId.toUpperCase(Locale.ROOT),
            merchantId.toUpperCase(Locale.ROOT),
            plan.getPlanCode(),
            plan.getPlanName(),
            request.billingCycle().trim().toUpperCase(Locale.ROOT),
            status,
            startDate,
            endDate,
            "Y",
            amountYuan,
            entitlements,
            now,
            now);
    merchantSubscriptionStore.put(subscriptionId, entity);
    createBillingOrderForSubscription(entity, now, defaultText(request.operator(), "系统"));
    return entity;
  }

  public List<InquiryMerchantSubscriptionEntity> listSubscriptions(InquirySubscriptionMineRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    return merchantSubscriptionStore.values().stream()
        .filter(item -> item.getMerchantId().equalsIgnoreCase(merchantId))
        .sorted(Comparator.comparing(InquiryMerchantSubscriptionEntity::getCreatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<InquiryBillingOrderEntity> listBillingOrders(InquiryBillingOrderListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    String status = normalizeBillingStatusOrNull(request.status());
    String keyword = normalize(request.keyword());
    return billingOrderStore.values().stream()
        .filter(item -> item.getMerchantId().equalsIgnoreCase(merchantId))
        .filter(item -> status == null || item.getStatus().equalsIgnoreCase(status))
        .filter(
            item ->
                keyword == null
                    || normalize(item.getBillNo()).contains(keyword)
                    || normalize(item.getSubscriptionNo()).contains(keyword)
                    || normalize(item.getPlanName()).contains(keyword)
                    || normalize(item.getPeriodStart()).contains(keyword)
                    || normalize(item.getPeriodEnd()).contains(keyword))
        .sorted(Comparator.comparing(InquiryBillingOrderEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public InquiryBillingOrderEntity getBillingOrderById(
      String billId, InquiryBillingOrderListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    InquiryBillingOrderEntity entity = requireBillingOrder(billId);
    if (!entity.getMerchantId().equalsIgnoreCase(merchantId)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "账单不存在");
    }
    return entity;
  }

  public InquiryBillingOrderEntity payBillingOrder(
      String billId, InquiryBillingOrderPaymentRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    InquiryBillingOrderEntity entity = requireBillingOrder(billId);
    if (!entity.getMerchantId().equalsIgnoreCase(merchantId)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "账单不存在");
    }
    BigDecimal payAmount = parsePositiveMoney(request.payAmount(), "payAmount");
    BigDecimal total = parseMoney(entity.getAmountYuan(), "amountYuan");
    BigDecimal paid = parseMoneyOrDefault(entity.getPaidAmountYuan(), BigDecimal.ZERO, "paidAmountYuan");
    BigDecimal paidAfter = paid.add(payAmount);
    if (paidAfter.compareTo(total) > 0) {
      paidAfter = total;
    }
    BigDecimal unpaidAfter = total.subtract(paidAfter).max(BigDecimal.ZERO);
    String status = unpaidAfter.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "PARTIAL_PAID";
    String remark =
        List.of(
                defaultText(request.payChannel(), ""),
                defaultText(request.operator(), ""),
                defaultText(request.remark(), ""))
            .stream()
            .filter(item -> !item.isBlank())
            .collect(Collectors.joining(" | "));
    entity.registerPayment(
        status, toMoney(paidAfter), toMoney(unpaidAfter), LocalDateTime.now().toString(), remark);
    return entity;
  }

  public InquiryDispatchScoreRuleEntity dispatchScoreRule(InquiryDispatchScoreRuleRequest request) {
    String scene = defaultText(request.sceneCode(), "MERCHANT_LEAD").trim().toUpperCase(Locale.ROOT);
    InquiryDispatchScoreRuleEntity entity = dispatchRuleStore.get(scene);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分发评分规则不存在");
    }
    return entity;
  }

  public List<InquiryMessageCenterEntity> listMessageCenter(InquiryMessageCenterListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    String type = normalize(request.messageType());
    String readStatus = normalize(request.readStatus());
    String keyword = normalize(request.keyword());
    return messageCenterStore.values().stream()
        .filter(item -> item.getMerchantId().equalsIgnoreCase(merchantId))
        .filter(item -> type == null || normalize(item.getBizType()).equals(type))
        .filter(item -> readStatus == null || normalize(item.getReadStatus()).equals(readStatus))
        .filter(
            item ->
                keyword == null
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getContent()).contains(keyword)
                    || normalize(item.getBizId()).contains(keyword))
        .sorted(Comparator.comparing(InquiryMessageCenterEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public InquiryMessageCenterEntity messageCenterDetail(
      String messageId, InquiryMessageCenterListRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    InquiryMessageCenterEntity entity = requireMessage(messageId);
    if (!entity.getMerchantId().equalsIgnoreCase(merchantId)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "消息不存在");
    }
    return entity;
  }

  public InquiryMessageCenterEntity messageCenterRead(
      String messageId, InquiryMessageCenterReadRequest request) {
    InquiryMessageCenterEntity entity =
        messageCenterDetail(
            messageId,
            new InquiryMessageCenterListRequest(
                request.merchantId(), null, null, null, 1, Integer.MAX_VALUE));
    if (!"READ".equalsIgnoreCase(entity.getReadStatus())) {
      entity.markRead(LocalDateTime.now());
    }
    return entity;
  }

  public int messageCenterReadAll(InquiryMessageCenterReadAllRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    int affected = 0;
    LocalDateTime now = LocalDateTime.now();
    for (InquiryMessageCenterEntity item : messageCenterStore.values()) {
      if (!item.getMerchantId().equalsIgnoreCase(merchantId)) {
        continue;
      }
      if (!"READ".equalsIgnoreCase(item.getReadStatus())) {
        item.markRead(now);
        affected++;
      }
    }
    return affected;
  }

  public InquiryH5HomeEntity h5Home(InquiryH5HomeRequest request) {
    String city = defaultText(request.city(), "全国").trim();
    List<InquiryH5HomeEntity.BannerEntity> banners =
        List.of(
            new InquiryH5HomeEntity.BannerEntity(
                "BNR001",
                "今日钢价速递",
                "全国主流城市热卷价格走势更新",
                "/inquiry/create",
                "https://cdn.huodaizi.com/h5/banner-market.png"),
            new InquiryH5HomeEntity.BannerEntity(
                "BNR002",
                "AI询价限时提速",
                "3步提交，10分钟内拿到首批报价",
                "/h5/inquiry/step1",
                "https://cdn.huodaizi.com/h5/banner-ai.png"));

    List<InquiryH5HomeEntity.QuickNavEntity> quickNavs =
        List.of(
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV001", "AI询价", "3步快速找货", "inquiry", "/h5/inquiry/step1", "HOT"),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV007", "快捷报价", "线索一键报价", "quick-quote", "/h5/quick-quote?merchantId=S001", ""),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV002", "现货大厅", "热门规格现货", "spot", "/", ""),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV003", "报价对比", "多商家智能比较", "compare", "/inquiry/compare", ""),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV004", "消息中心", "通知与待办", "message", "/merchant/message-center", "NEW"),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV005", "提货对账", "履约协同闭环", "reconcile", "/inquiry/reconcile/pass", ""),
            new InquiryH5HomeEntity.QuickNavEntity(
                "NAV006", "信用规则", "分发评分公开", "rule", "/dispatch/score-rules", ""));

    List<InquiryH5HomeEntity.MarketCardEntity> marketCards =
        List.of(
            new InquiryH5HomeEntity.MarketCardEntity("螺纹钢 HRB400E", "Φ20*12m", city, "3520", "+20", "860"),
            new InquiryH5HomeEntity.MarketCardEntity("热轧卷板 Q235B", "3.0*1500*C", city, "3680", "-10", "420"),
            new InquiryH5HomeEntity.MarketCardEntity("中厚板 Q355B", "10*2000*8000", city, "3890", "+15", "220"));

    List<InquiryH5HomeEntity.RecommendationEntity> recommendations =
        List.of(
            new InquiryH5HomeEntity.RecommendationEntity(
                "REC001",
                "唐山弘达钢贸",
                "响应快 · 履约稳",
                "92",
                "7分钟",
                "/merchant/credit/score?merchantId=S001"),
            new InquiryH5HomeEntity.RecommendationEntity(
                "REC002",
                "无锡铭泰供应链",
                "支持月结 · 可回单",
                "88",
                "11分钟",
                "/merchant/credit/score?merchantId=S002"));

    return new InquiryH5HomeEntity(
        city,
        "晴 18-26℃",
        "买钢卖钢 就上货袋子",
        "AI询价 + 报价对比 + 履约协同",
        quickNavs,
        banners,
        marketCards,
        recommendations,
        LocalDateTime.now());
  }

  public InquiryH5InquiryStep1DraftEntity initH5InquiryStep1Draft(String city) {
    LocalDateTime now = LocalDateTime.now();
    String draftId = buildH5InquiryDraftId();
    InquiryH5InquiryStep1DraftEntity entity =
        new InquiryH5InquiryStep1DraftEntity(
            draftId,
            "REBAR",
            "",
            defaultText(city, "全国").trim(),
            "",
            "ANY",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "INIT",
            now,
            now);
    h5InquiryStep1DraftStore.put(draftId, entity);
    return entity;
  }

  public InquiryH5InquiryStep1DraftEntity saveH5InquiryStep1Draft(InquiryH5InquiryStep1SaveRequest request) {
    String draftId = defaultText(request.draftId(), "").trim();
    if (draftId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "draftId 不能为空");
    }
    InquiryH5InquiryStep1DraftEntity existing = h5InquiryStep1DraftStore.get(draftId);
    if (existing == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价草稿不存在");
    }
    LocalDateTime now = LocalDateTime.now();
    InquiryH5InquiryStep1DraftEntity saved =
        new InquiryH5InquiryStep1DraftEntity(
            draftId,
            request.categoryCode().trim().toUpperCase(Locale.ROOT),
            request.specText().trim(),
            request.deliveryCity().trim(),
            request.demandQtyTon().stripTrailingZeros().toPlainString(),
            normalizeInvoiceNeedForH5(request.invoiceNeed()),
            request.contactMobile().trim(),
            defaultText(request.remark(), ""),
            existing.getExpectedDeliveryAt(),
            existing.getDeliveryTimeRange(),
            existing.getUnloadSupport(),
            existing.getNeedInvoice(),
            existing.getStep2Remark(),
            existing.getInquiryId(),
            existing.getInquiryNo(),
            "STEP1_SAVED",
            existing.getCreatedAt(),
            now);
    h5InquiryStep1DraftStore.put(draftId, saved);
    return saved;
  }

  public InquiryH5InquiryStep1DraftEntity getH5InquiryStep1Draft(String draftId) {
    String normalizedDraftId = defaultText(draftId, "").trim();
    if (normalizedDraftId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "draftId 不能为空");
    }
    InquiryH5InquiryStep1DraftEntity entity = h5InquiryStep1DraftStore.get(normalizedDraftId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价草稿不存在");
    }
    return entity;
  }

  public InquiryH5InquiryStep1DraftEntity submitH5InquiryStep2(InquiryH5InquiryStep2SubmitRequest request) {
    InquiryH5InquiryStep1DraftEntity draft = getH5InquiryStep1Draft(request.draftId());
    if (!"STEP1_SAVED".equalsIgnoreCase(draft.getStatus())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先完成Step1");
    }
    String normalizedNeedInvoice = normalizeYesNo(request.needInvoice(), "needInvoice");
    String invoiceNeed = "Y".equals(normalizedNeedInvoice) ? "YES" : "NO";
    InquiryEntity inquiry =
        create(
            new InquiryCreateRequest(
                draft.getCategoryCode(),
                draft.getSpecText(),
                parsePositiveMoney(draft.getDemandQtyTon(), "demandQtyTon"),
                draft.getDeliveryCity(),
                defaultText(request.expectedDeliveryAt(), ""),
                invoiceNeed,
                draft.getContactMobile(),
                mergeRemarks(draft.getRemark(), request.step2Remark(), request.deliveryTimeRange(), request.unloadSupport())));
    InquiryH5InquiryStep1DraftEntity submitted =
        new InquiryH5InquiryStep1DraftEntity(
            draft.getDraftId(),
            draft.getCategoryCode(),
            draft.getSpecText(),
            draft.getDeliveryCity(),
            draft.getDemandQtyTon(),
            draft.getInvoiceNeed(),
            draft.getContactMobile(),
            draft.getRemark(),
            defaultText(request.expectedDeliveryAt(), ""),
            defaultText(request.deliveryTimeRange(), ""),
            normalizeYesNo(request.unloadSupport(), "unloadSupport"),
            normalizedNeedInvoice,
            defaultText(request.step2Remark(), ""),
            inquiry.getId(),
            inquiry.getInquiryNo(),
            "SUBMITTED",
            draft.getCreatedAt(),
            LocalDateTime.now());
    h5InquiryStep1DraftStore.put(submitted.getDraftId(), submitted);
    return submitted;
  }

  private String buildH5InquiryDraftId() {
    return "H5DRAFT" + NO_FMT.format(LocalDateTime.now()) + h5InquiryDraftSeq.incrementAndGet();
  }

  public List<InquiryMerchantLeadEntity> workbenchTasks(InquiryQuoteWorkbenchTaskRequest request) {
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    String keyword = normalize(request.keyword());
    Boolean quoteTimeoutOnly = request.quoteTimeoutOnly();
    InquiryMerchantLeadStatus status = normalizeMerchantLeadStatusOrNull(request.status());
    return merchantLeadStore.values().stream()
        .filter(item -> item.getMerchantId().equalsIgnoreCase(merchantId))
        .filter(item -> status == null || item.getStatus() == status)
        .filter(item -> !Boolean.TRUE.equals(quoteTimeoutOnly) || shouldTreatAsTimeout(item))
        .filter(
            item ->
                keyword == null
                    || normalize(item.getInquiryNo()).contains(keyword)
                    || normalize(item.getSpecText()).contains(keyword)
                    || normalize(item.getBuyerCompany()).contains(keyword))
        .sorted(Comparator.comparing(InquiryMerchantLeadEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<InquiryMerchantLeadEntity> workbenchOverview(
      InquiryQuoteWorkbenchOverviewRequest request) {
    return workbenchTasks(
        new InquiryQuoteWorkbenchTaskRequest(
            request.merchantId(), null, false, null, null, 1, Integer.MAX_VALUE));
  }

  public List<InquiryMerchantLeadEntity> workbenchBatchUpdate(
      InquiryQuoteWorkbenchBatchUpdateRequest request) {
    InquiryMerchantLeadStatus status = normalizeMerchantLeadStatus(request.status());
    String merchantId = defaultText(request.merchantId(), "").trim();
    if (merchantId.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "merchantId 不能为空");
    }
    List<String> ids = request.leadIds() == null ? List.of() : request.leadIds();
    if (ids.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "leadIds 不能为空");
    }
    for (String leadId : ids) {
      InquiryMerchantLeadEntity entity = requireMerchantLead(leadId);
      if (!entity.getMerchantId().equalsIgnoreCase(merchantId)) {
        throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "线索不存在");
      }
      entity.updateStatus(status, defaultText(request.comment(), ""));
    }
    return ids.stream().map(this::requireMerchantLead).toList();
  }

  public InquiryEntity getById(String id) {
    InquiryEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
    }
    return entity;
  }

  public InquiryQuoteCompareEntity getQuoteById(String inquiryId, String quoteId) {
    getById(inquiryId);
    return quoteStore.getOrDefault(inquiryId, List.of()).stream()
        .filter(item -> item.getQuoteId().equals(quoteId))
        .findFirst()
        .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND.getCode(), "报价不存在"));
  }

  public InquiryQuoteCompareEntity confirmDeal(
      String inquiryId,
      String quoteId,
      String contactMobile,
      String buyerCompany,
      String buyerContact,
      String buyerPhone,
      String expectedSignAt,
      String remark) {
    InquiryEntity inquiry = getById(inquiryId);
    String normalizedMobile = normalizePhone(contactMobile);
    if (!normalizePhone(inquiry.getContactMobile()).equals(normalizedMobile)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
    }
    InquiryQuoteCompareEntity quote = getQuoteById(inquiryId, quoteId);
    inquiry.setStatus(InquiryStatus.DEAL_DONE);
    return quote;
  }

  public InquiryPickupOrderEntity createPickupOrder(InquiryPickupOrderCreateRequest request) {
    String inquiryId = request.inquiryId().trim();
    String quoteId = request.quoteId().trim();
    String contactMobile = normalizePhone(request.contactMobile());

    InquiryEntity inquiry = getById(inquiryId);
    if (!normalizePhone(inquiry.getContactMobile()).equals(contactMobile)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
    }
    InquiryQuoteCompareEntity quote = getQuoteById(inquiryId, quoteId);

    String pickupId = "PU" + pickupSeq.incrementAndGet();
    String pickupNo = "PU-" + NO_FMT.format(LocalDateTime.now()) + "-" + pickupId;
    LocalDateTime now = LocalDateTime.now();
    InquiryPickupOrderEntity entity =
        new InquiryPickupOrderEntity(
            pickupId,
            pickupNo,
            inquiry.getId(),
            inquiry.getInquiryNo(),
            quote.getQuoteId(),
            quote.getSupplierId(),
            quote.getSupplierName(),
            defaultText(request.buyerCompany(), "买方公司"),
            defaultText(request.buyerContact(), "采购经理"),
            inquiry.getContactMobile(),
            maskPhone(inquiry.getContactMobile()),
            defaultText(request.pickupSite(), inquiry.getDeliveryCity() + "提货点"),
            request.pickupDate().trim(),
            request.pickupDriverName().trim(),
            maskPhone(request.pickupDriverPhone()),
            request.pickupVehicleNo().trim().toUpperCase(Locale.ROOT),
            inquiry.getDemandQtyTon(),
            inquiry.getSpecText(),
            defaultText(request.remark(), "-"),
            InquiryPickupOrderStatus.CREATED,
            now,
            now);
    pickupOrderStore.put(pickupId, entity);
    return entity;
  }

  public List<InquiryPickupOrderEntity> listPickupOrders(InquiryPickupOrderListRequest request) {
    String phone = normalizePhoneOrNull(request.contactMobile());
    InquiryPickupOrderStatus status = normalizePickupStatusOrNull(request.status());
    String keyword = normalize(request.keyword());
    return pickupOrderStore.values().stream()
        .filter(item -> phone == null || normalizePhone(item.getBuyerPhone()).equals(phone))
        .filter(item -> status == null || item.getStatus() == status)
        .filter(
            item ->
                keyword == null
                    || normalize(item.getPickupNo()).contains(keyword)
                    || normalize(item.getInquiryNo()).contains(keyword)
                    || normalize(item.getSupplierName()).contains(keyword)
                    || normalize(item.getSpecText()).contains(keyword)
                    || normalize(item.getPickupAddress()).contains(keyword))
        .sorted(Comparator.comparing(InquiryPickupOrderEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public InquiryPickupOrderEntity getPickupOrderById(String pickupOrderId, String contactMobile) {
    InquiryPickupOrderEntity entity = requirePickupOrder(pickupOrderId);
    String phone = normalizePhoneOrNull(contactMobile);
    if (phone == null || !normalizePhone(entity.getBuyerPhone()).equals(phone)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "提货单不存在");
    }
    return entity;
  }

  public InquiryPickupOrderEntity updatePickupOrderStatus(
      String pickupOrderId, InquiryPickupOrderStatusUpdateRequest request) {
    InquiryPickupOrderEntity entity = getPickupOrderById(pickupOrderId, request.contactMobile());
    entity.setStatus(normalizePickupStatus(request.status()));
    return entity;
  }

  public InquiryReconcileOrderEntity createReconcileOrder(InquiryReconcileOrderCreateRequest request) {
    InquiryPickupOrderEntity pickup =
        getPickupOrderById(request.pickupOrderId().trim(), request.contactMobile().trim());
    String reconcileId = "RC" + reconcileSeq.incrementAndGet();
    String reconcileNo = "RC-" + NO_FMT.format(LocalDateTime.now()) + "-" + reconcileId;
    BigDecimal invoice = parseMoney("1", "invoiceAmount");
    BigDecimal deduction = BigDecimal.ZERO;
    BigDecimal receivable = invoice.subtract(deduction).max(BigDecimal.ZERO);
    BigDecimal paid = BigDecimal.ZERO;
    BigDecimal outstanding = receivable.subtract(paid).max(BigDecimal.ZERO);
    LocalDateTime now = LocalDateTime.now();
    InquiryReconcileOrderEntity entity =
        new InquiryReconcileOrderEntity(
            reconcileId,
            reconcileNo,
            pickup.getPickupId(),
            pickup.getPickupNo(),
            pickup.getInquiryId(),
            pickup.getInquiryNo(),
            pickup.getSpecText() + " / " + pickup.getQuantityTon() + "吨",
            pickup.getQuoteId(),
            pickup.getSupplierId(),
            pickup.getSupplierName(),
            pickup.getBuyerCompany(),
            pickup.getBuyerPhone(),
            maskPhone(pickup.getBuyerPhone()),
            normalizeMonthOrFallback(request.statementMonth()),
            defaultText(request.dueDate(), LocalDate.now().plusDays(30).toString()),
            toMoney(invoice),
            toMoney(deduction),
            toMoney(receivable),
            toMoney(paid),
            toMoney(outstanding),
            defaultText(request.remark(), "-"),
            InquiryReconcileOrderStatus.CREATED,
            now,
            now);
    reconcileOrderStore.put(reconcileId, entity);
    return entity;
  }

  public List<InquiryReconcileOrderEntity> listReconcileOrders(InquiryReconcileOrderListRequest request) {
    String phone = normalizePhoneOrNull(request.contactMobile());
    InquiryReconcileOrderStatus status = normalizeReconcileStatusOrNull(request.status());
    String keyword = normalize(request.keyword());
    return reconcileOrderStore.values().stream()
        .filter(item -> phone == null || normalizePhone(item.getContactMobile()).equals(phone))
        .filter(item -> status == null || item.getStatus() == status)
        .filter(
            item ->
                keyword == null
                    || normalize(item.getReconcileNo()).contains(keyword)
                    || normalize(item.getPickupOrderNo()).contains(keyword)
                    || normalize(item.getSupplierName()).contains(keyword)
                    || normalize(item.getGoodsSummary()).contains(keyword))
        .sorted(Comparator.comparing(InquiryReconcileOrderEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public InquiryReconcileOrderEntity getReconcileOrderById(
      String reconcileOrderId, String contactMobile) {
    InquiryReconcileOrderEntity entity = requireReconcileOrder(reconcileOrderId);
    String phone = normalizePhoneOrNull(contactMobile);
    if (phone == null || !normalizePhone(entity.getContactMobile()).equals(phone)) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "对账单不存在");
    }
    return entity;
  }

  public InquiryReconcileOrderEntity updateReconcileOrderStatus(
      String reconcileOrderId, InquiryReconcileOrderStatusUpdateRequest request) {
    InquiryReconcileOrderEntity entity =
        getReconcileOrderById(reconcileOrderId, request.contactMobile().trim());
    InquiryReconcileOrderStatus status = normalizeReconcileStatus(request.status());
    BigDecimal receivable = parseMoney(entity.getReceivableAmount(), "receivableAmount");
    BigDecimal paid = parseMoneyOrDefault(entity.getPaidAmount(), BigDecimal.ZERO, "paidAmount");
    if (request.paidAmount() != null && !request.paidAmount().isBlank()) {
      paid = parseMoney(request.paidAmount(), "paidAmount");
    } else if (status == InquiryReconcileOrderStatus.PAID) {
      paid = receivable;
    }
    if (paid.compareTo(receivable) > 0) {
      paid = receivable;
    }
    BigDecimal outstanding = receivable.subtract(paid).max(BigDecimal.ZERO);
    entity.updateAmounts(toMoney(paid), toMoney(outstanding));
    entity.setStatus(status);
    if (request.remark() != null && !request.remark().isBlank()) {
      entity.setLatestRemark(request.remark().trim());
    }
    return entity;
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizePhone(String phone) {
    return phone == null ? "" : phone.replaceAll("\\D", "");
  }

  private String normalizePhoneOrNull(String phone) {
    String normalized = normalizePhone(phone);
    return normalized.isBlank() ? null : normalized;
  }

  private InquiryStatus normalizeStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    try {
      return InquiryStatus.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 OPEN/QUOTING/DEAL_DONE/CLOSED");
    }
  }

  private InquiryMerchantLeadStatus normalizeMerchantLeadStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return normalizeMerchantLeadStatus(status);
  }

  private InquiryMerchantLeadStatus normalizeMerchantLeadStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    try {
      return InquiryMerchantLeadStatus.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 NEW/CONTACTED/QUOTED/WON/LOST/CLOSED");
    }
  }

  private InquiryPickupOrderStatus normalizePickupStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return normalizePickupStatus(status);
  }

  private InquiryPickupOrderStatus normalizePickupStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    try {
      return InquiryPickupOrderStatus.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 CREATED/CONFIRMED/IN_TRANSIT/SIGNED/COMPLETED/CANCELLED");
    }
  }

  private InquiryReconcileOrderStatus normalizeReconcileStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return normalizeReconcileStatus(status);
  }

  private InquiryReconcileOrderStatus normalizeReconcileStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    try {
      return InquiryReconcileOrderStatus.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 CREATED/INVOICE_PENDING/INVOICED/PARTIAL_PAID/PAID/CLOSED/DISPUTED");
    }
  }

  private InquiryMerchantLeadEntity requireMerchantLead(String leadId) {
    InquiryMerchantLeadEntity entity = merchantLeadStore.get(leadId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "商家线索不存在");
    }
    return entity;
  }

  private InquiryPickupOrderEntity requirePickupOrder(String pickupOrderId) {
    InquiryPickupOrderEntity entity = pickupOrderStore.get(pickupOrderId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "提货单不存在");
    }
    return entity;
  }

  private InquiryReconcileOrderEntity requireReconcileOrder(String reconcileOrderId) {
    InquiryReconcileOrderEntity entity = reconcileOrderStore.get(reconcileOrderId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "对账单不存在");
    }
    return entity;
  }

  private InquirySubscriptionPlanEntity requireSubscriptionPlan(String planCode) {
    String normalizedCode = defaultText(planCode, "").trim();
    if (normalizedCode.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "planCode 不能为空");
    }
    InquirySubscriptionPlanEntity entity =
        subscriptionPlanStore.values().stream()
            .filter(item -> item.getPlanCode().equalsIgnoreCase(normalizedCode))
            .findFirst()
            .orElse(null);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "套餐不存在");
    }
    return entity;
  }

  private InquiryBillingOrderEntity requireBillingOrder(String billId) {
    InquiryBillingOrderEntity entity = billingOrderStore.get(defaultText(billId, "").trim());
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "账单不存在");
    }
    return entity;
  }

  private InquiryMessageCenterEntity requireMessage(String messageId) {
    InquiryMessageCenterEntity entity = messageCenterStore.get(defaultText(messageId, "").trim());
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "消息不存在");
    }
    return entity;
  }

  private String normalizeBillingStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return normalizeBillingStatus(status);
  }

  private String normalizeBillingStatus(String status) {
    String normalized = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "UNPAID", "PARTIAL_PAID", "PAID", "OVERDUE" -> normalized;
      default ->
          throw new BaseException(
              ErrorCode.BAD_REQUEST.getCode(),
              "status 仅支持 UNPAID/PARTIAL_PAID/PAID/OVERDUE");
    };
  }

  private String normalizeInvoiceNeedForH5(String invoiceNeed) {
    String normalized = defaultText(invoiceNeed, "ANY").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "ANY", "YES", "NO" -> normalized;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "invoiceNeed 仅支持 ANY/YES/NO");
    };
  }

  private String normalizeYesNo(String value, String fieldName) {
    String normalized = defaultText(value, "").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "Y", "N" -> normalized;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), fieldName + " 仅支持 Y/N");
    };
  }

  private String mergeRemarks(
      String step1Remark, String step2Remark, String deliveryTimeRange, String unloadSupport) {
    List<String> remarks = new java.util.ArrayList<>();
    if (step1Remark != null && !step1Remark.isBlank()) {
      remarks.add("Step1备注:" + step1Remark.trim());
    }
    if (step2Remark != null && !step2Remark.isBlank()) {
      remarks.add("Step2备注:" + step2Remark.trim());
    }
    if (deliveryTimeRange != null && !deliveryTimeRange.isBlank()) {
      remarks.add("收货时段:" + deliveryTimeRange.trim());
    }
    if (unloadSupport != null && !unloadSupport.isBlank()) {
      remarks.add("需要卸货协助:" + unloadSupport.trim());
    }
    return remarks.isEmpty() ? "-" : String.join(" | ", remarks);
  }

  private BigDecimal parsePositiveMoney(String text, String field) {
    BigDecimal value = parseMoney(text, field);
    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), field + " 必须大于0");
    }
    return value;
  }

  private void createBillingOrderForSubscription(
      InquiryMerchantSubscriptionEntity subscription, LocalDateTime now, String operator) {
    String billId = "BL" + billingSeq.incrementAndGet();
    String billNo = "BILL-" + NO_FMT.format(now) + "-" + billId;
    LocalDate issueDate = now.toLocalDate();
    boolean yearly = "YEARLY".equalsIgnoreCase(subscription.getBillingCycle());
    LocalDate periodStart = issueDate.withDayOfMonth(1);
    LocalDate periodEnd = yearly ? periodStart.plusYears(1).minusDays(1) : periodStart.plusMonths(1).minusDays(1);
    BigDecimal amount = parseMoney(subscription.getAmountYuan(), "amountYuan");
    BigDecimal taxAmount = amount.multiply(new BigDecimal("0.13"));
    BigDecimal netAmount = amount.subtract(taxAmount).max(BigDecimal.ZERO);
    InquiryBillingOrderEntity entity =
        new InquiryBillingOrderEntity(
            billId,
            billNo,
            subscription.getMerchantId(),
            subscription.getMerchantName(),
            subscription.getSubscriptionId(),
            subscription.getSubscriptionNo(),
            subscription.getPlanCode(),
            subscription.getPlanName(),
            periodStart.toString(),
            periodEnd.toString(),
            issueDate.toString(),
            toMoney(amount),
            "0",
            toMoney(amount),
            "UNPAID",
            issueDate.plusDays(15).toString(),
            "BANK_TRANSFER",
            "ISSUED",
            "13%",
            toMoney(taxAmount),
            toMoney(netAmount),
            "",
            defaultText(operator, "系统创建"),
            now,
            now);
    billingOrderStore.put(entity.getBillId(), entity);
  }

  private boolean shouldTreatAsTimeout(InquiryMerchantLeadEntity item) {
    return item.getStatus() == InquiryMerchantLeadStatus.NEW
        || item.getStatus() == InquiryMerchantLeadStatus.CONTACTED;
  }

  private void seed() {
    InquiryEntity inquiry1 =
        create(
        new InquiryCreateRequest(
            "REBAR",
            "HRB400E Φ20*12m",
            new BigDecimal("120"),
            "唐山",
            LocalDate.now().plusDays(2).toString(),
            "YES",
            "13800138000",
            "用于工程项目一期"));
    inquiry1.setQuoteSupplierCount(3);

    InquiryEntity inquiry2 =
        create(
        new InquiryCreateRequest(
            "HOT_ROLL",
            "Q235B 3.0*1500*C",
            new BigDecimal("80"),
            "无锡",
            LocalDate.now().plusDays(3).toString(),
            "ANY",
            "13900139000",
            "需要可开票"));
    inquiry2.setQuoteSupplierCount(2);
    seedMerchantLeads(inquiry1, inquiry2);
    seedMerchantCreditScores();
    seedSubscriptionPlans();
    seedMerchantSubscriptions();
    seedBillingOrders();
    seedDispatchScoreRules();
    seedMessageCenter();
    seedH5Home();
    seedH5InquiryStep1Drafts();
  }

  private void seedMerchantLeads(InquiryEntity inquiry1, InquiryEntity inquiry2) {
    InquiryMerchantLeadEntity a =
        new InquiryMerchantLeadEntity(
            "ML-" + inquiry1.getId() + "-S001",
            "MLN-" + inquiry1.getId() + "-001",
            inquiry1.getId(),
            inquiry1.getInquiryNo(),
            "S001",
            "唐山弘达钢贸",
            "唐山弘达钢贸有限公司",
            maskName("王工"),
            maskPhone("13800138000"),
            inquiry1.getSpecText(),
            inquiry1.getDemandQtyTon(),
            inquiry1.getDeliveryCity(),
            inquiry1.getInvoiceNeed(),
            inquiry1.getExpectedDeliveryAt(),
            InquiryMerchantLeadStatus.NEW,
            "",
            "",
            "",
            "",
            "",
            "",
            LocalDateTime.now().minusHours(5),
            LocalDateTime.now().minusHours(5));
    InquiryMerchantLeadEntity b =
        new InquiryMerchantLeadEntity(
            "ML-" + inquiry1.getId() + "-S002",
            "MLN-" + inquiry1.getId() + "-002",
            inquiry1.getId(),
            inquiry1.getInquiryNo(),
            "S002",
            "无锡铭泰供应链",
            "无锡铭泰供应链有限公司",
            maskName("赵总"),
            maskPhone("13900139000"),
            inquiry1.getSpecText(),
            inquiry1.getDemandQtyTon(),
            inquiry1.getDeliveryCity(),
            inquiry1.getInvoiceNeed(),
            inquiry1.getExpectedDeliveryAt(),
            InquiryMerchantLeadStatus.QUOTED,
            "",
            "",
            "",
            "",
            "",
            "",
            LocalDateTime.now().minusHours(4),
            LocalDateTime.now().minusHours(2));
    b.updateQuote("3490", "418800", "2", "月结30天", "按期到厂");
    InquiryMerchantLeadEntity c =
        new InquiryMerchantLeadEntity(
            "ML-" + inquiry2.getId() + "-S001",
            "MLN-" + inquiry2.getId() + "-001",
            inquiry2.getId(),
            inquiry2.getInquiryNo(),
            "S001",
            "唐山弘达钢贸",
            "某工程采购公司",
            maskName("李经理"),
            maskPhone("13700137000"),
            inquiry2.getSpecText(),
            inquiry2.getDemandQtyTon(),
            inquiry2.getDeliveryCity(),
            inquiry2.getInvoiceNeed(),
            inquiry2.getExpectedDeliveryAt(),
            InquiryMerchantLeadStatus.CONTACTED,
            "",
            "",
            "",
            "",
            "",
            "",
            LocalDateTime.now().minusHours(6),
            LocalDateTime.now().minusHours(3));
    merchantLeadStore.put(a.getId(), a);
    merchantLeadStore.put(b.getId(), b);
    merchantLeadStore.put(c.getId(), c);
  }

  private void seedMerchantCreditScores() {
    merchantCreditScoreStore.put(
        "S001",
        new InquiryMerchantCreditScoreEntity(
            "S001",
            "唐山弘达钢贸",
            "92",
            "A",
            "TOP 18%",
            "v2026.04",
            List.of(
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "FULFILLMENT", "履约稳定性", 98, 40, "UP", "逾期率低，交付准时"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "RESPONSE", "响应效率", 95, 25, "FLAT", "平均响应 7 分钟"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "PAYMENT", "回款质量", 89, 25, "UP", "回款周期稳定"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "DATA_QUALITY", "数据完整性", 90, 10, "UP", "电子回单完整率持续提升")),
            List.of(
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-01", "86", "96.2%", "1.8%", "10"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-02", "88", "97.1%", "1.6%", "9"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-03", "90", "97.8%", "1.4%", "8"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-04", "92", "98.1%", "1.2%", "7")),
            List.of("低风险", "履约稳健"),
            List.of("保持回款登记时效在 T+1 内", "将争议工单平均关闭时长压缩到 24h", "提高电子回单上传完整率至 99%"),
            LocalDateTime.now().minusHours(2)));
    merchantCreditScoreStore.put(
        "S002",
        new InquiryMerchantCreditScoreEntity(
            "S002",
            "无锡铭泰供应链",
            "88",
            "A-",
            "TOP 24%",
            "v2026.04",
            List.of(
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "FULFILLMENT", "履约稳定性", 95, 40, "UP", "履约率稳定"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "RESPONSE", "响应效率", 90, 25, "UP", "平均响应 11 分钟"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "PAYMENT", "回款质量", 85, 25, "FLAT", "部分订单回款延后"),
                new InquiryMerchantCreditScoreEntity.DimensionEntity(
                    "DATA_QUALITY", "数据完整性", 86, 10, "UP", "对账凭证完整度较高")),
            List.of(
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-01", "84", "95.5%", "2.4%", "13"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-02", "85", "96.1%", "2.2%", "12"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-03", "87", "96.8%", "2.0%", "11"),
                new InquiryMerchantCreditScoreEntity.TrendPointEntity(
                    "2026-04", "88", "97.3%", "1.9%", "11")),
            List.of("中低风险", "回款有优化空间"),
            List.of("优化对账异常追踪", "增加高峰时段客服值守", "完善回款预警规则"),
            LocalDateTime.now().minusHours(3)));
  }

  private void seedSubscriptionPlans() {
    subscriptionPlanStore.put(
        "PLAN_BASIC",
        new InquirySubscriptionPlanEntity(
            "PLAN_BASIC",
                "PLAN_BASIC",
            "标准版",
                "BASIC",
            "MONTHLY",
            "1999",
                "2499",
                false,
                "线索型商家",
            List.of(
                    new InquirySubscriptionPlanEntity.FeatureEntity("LEAD", "线索管理", "支持线索筛选、状态跟进", "核心"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "QUOTE", "报价工作台", "报价任务视图与批量处理", "核心"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "REPORT", "基础报表", "成交率与响应时效看板", "基础"))));
    subscriptionPlanStore.put(
        "PLAN_PRO",
        new InquirySubscriptionPlanEntity(
            "PLAN_PRO",
                "PLAN_PRO",
            "进阶版",
                "PRO",
            "MONTHLY",
            "3999",
                "4599",
                true,
                "成交增长商家",
            List.of(
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "PICKUP", "提货通", "提货单协同与履约留痕", "增强"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "RECONCILE", "对账通", "对账回款流程管理", "增强"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "CREDIT", "信用评分", "信用分与改进建议", "增强"))));
    subscriptionPlanStore.put(
        "PLAN_ENTERPRISE",
        new InquirySubscriptionPlanEntity(
            "PLAN_ENTERPRISE",
                "PLAN_ENTERPRISE",
            "企业版",
                "ENTERPRISE",
            "YEARLY",
            "69999",
                "79999",
                false,
                "集团与平台型客户",
            List.of(
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "RBAC", "多账号权限", "支持组织架构权限分级", "企业"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "RISK", "风控预警", "回款/争议/履约阈值策略", "企业"),
                    new InquirySubscriptionPlanEntity.FeatureEntity(
                        "API", "数据API", "支持经营数据API对接", "企业"))));
  }

  private void seedMerchantSubscriptions() {
    LocalDateTime now = LocalDateTime.now();
    merchantSubscriptionStore.put(
        "SUB20260418001",
        new InquiryMerchantSubscriptionEntity(
            "SUB20260418001",
            "SUB-20260418-SUB20260418001",
            "S001",
            "唐山弘达钢贸",
            "PLAN_PRO",
            "进阶版",
            "MONTHLY",
            now.toLocalDate().minusMonths(1).toString(),
            now.toLocalDate().plusMonths(11).toString(),
            "ACTIVE",
            "Y",
            "3999",
            List.of("提货通", "对账通", "信用评分"),
            now.minusDays(7),
            now.minusDays(1)));
  }

  private void seedBillingOrders() {
    merchantSubscriptionStore.values().forEach(
        subscription -> {
          boolean exists =
              billingOrderStore.values().stream()
                  .anyMatch(item -> item.getSubscriptionId().equals(subscription.getSubscriptionId()));
          if (!exists) {
            createBillingOrderForSubscription(subscription, subscription.getCreatedAt().plusHours(1), "系统初始化");
          }
        });
  }

  private void seedDispatchScoreRules() {
    dispatchRuleStore.put(
        "MERCHANT_LEAD",
        new InquiryDispatchScoreRuleEntity(
            "MERCHANT_LEAD",
            "线索分发评分规则",
            "v2026.04",
            "对全部商家公开评分维度与增减分标准，付费仅做小权重加成。",
            "每周滚动更新",
            "付费因素仅作为小权重加成",
            List.of(
                new InquiryDispatchScoreRuleEntity.DimensionEntity(
                    "FULFILLMENT", "履约稳定性", 35, "按近90天按时交付率、拒单率综合评估", "正向", "最高权重，体现稳定履约能力"),
                new InquiryDispatchScoreRuleEntity.DimensionEntity(
                    "RESPONSE", "响应时效", 25, "按首次报价响应时长和客服响应时长评估", "正向", "鼓励分钟级响应"),
                new InquiryDispatchScoreRuleEntity.DimensionEntity(
                    "PRICE_COMP", "报价竞争力", 20, "对同规格询价的报价偏离中位值进行评估", "正向", "不过度奖励极端低价"),
                new InquiryDispatchScoreRuleEntity.DimensionEntity(
                    "DISPUTE", "纠纷率", 15, "按争议单占比和结案时长反向计分", "负向", "纠纷越多分数越低"),
                new InquiryDispatchScoreRuleEntity.DimensionEntity(
                    "DATA_QUALITY", "数据完整性", 5, "按提货单、回单、对账凭证完整度计分", "正向", "鼓励数据留痕")),
            List.of(
                new InquiryDispatchScoreRuleEntity.BonusEntity(
                    "B01", "连续30天0纠纷", "+3", "自然月内新增订单无争议且无超时工单", "每月最多加1次"),
                new InquiryDispatchScoreRuleEntity.BonusEntity(
                    "B02", "T+1完成回单上传", "+2", "签收后1个工作日内上传完整回单", "每周最多加2次"),
                new InquiryDispatchScoreRuleEntity.BonusEntity(
                    "B03", "高峰时段极速响应", "+1", "工作日09:00-18:00平均响应≤8分钟", "按周滚动评估")),
            List.of(
                new InquiryDispatchScoreRuleEntity.PenaltyEntity(
                    "P01", "超时未报价", "-5", "有效线索30分钟未响应或未报价", "同一线索仅扣一次"),
                new InquiryDispatchScoreRuleEntity.PenaltyEntity(
                    "P02", "履约违约", "-8", "确认成交后拒发货/无故延迟交付", "严重场景可触发冻结"),
                new InquiryDispatchScoreRuleEntity.PenaltyEntity(
                    "P03", "数据缺失", "-2", "提货单或对账凭证关键字段缺失", "按单扣分")),
            List.of(
                new InquiryDispatchScoreRuleEntity.CaseEntity(
                    "S001",
                    "唐山弘达钢贸",
                    "91",
                    "TOP 18%",
                    "履约与响应领先，获得优先线索分发"),
                new InquiryDispatchScoreRuleEntity.CaseEntity(
                    "S002",
                    "无锡铭泰供应链",
                    "87",
                    "TOP 27%",
                    "纠纷率下降后分发量逐周提升"),
                new InquiryDispatchScoreRuleEntity.CaseEntity(
                    "S003",
                    "郑州鑫诚贸易",
                    "79",
                    "TOP 46%",
                    "因多次超时报价，分发优先级下降")),
            List.of(
                "平台公开维度、权重方向与典型增减分场景，确保分发规则透明。",
                "付费服务不改变核心履约与风控权重，最多提供小幅加权。",
                "若发现数据异常或申诉场景，平台将在核验后回溯修正。"),
            LocalDateTime.now().minusHours(2)));
  }

  private void seedMessageCenter() {
    LocalDateTime now = LocalDateTime.now();
    messageCenterStore.put(
        "MSG20260419001",
        new InquiryMessageCenterEntity(
            "MSG20260419001",
            "S001",
            "报价工作台有新线索待处理",
            "您有 2 条新线索超 10 分钟未响应，建议尽快报价。",
            "SYSTEM",
            "ML-IQ20260418001-S001",
            "HIGH",
            "/merchant/quote/workbench?merchantId=S001",
            now.minusHours(2),
            "UNREAD",
            null,
            now.minusHours(2)));
    messageCenterStore.put(
        "MSG20260419002",
        new InquiryMessageCenterEntity(
            "MSG20260419002",
            "S001",
            "订阅账单即将到期",
            "您当前套餐账单将在 3 天后到期，请及时完成回款登记。",
            "FINANCE",
            "BL20260418001",
            "MEDIUM",
            "/merchant/billing?merchantId=S001",
            now.minusHours(8),
            "UNREAD",
            null,
            now.minusHours(8)));
    messageCenterStore.put(
        "MSG20260419003",
        new InquiryMessageCenterEntity(
            "MSG20260419003",
            "S001",
            "分发评分规则已更新",
            "公开规则版本升级至 v2026.04，建议查看加减分细则。",
            "RULE",
            "DISPATCH-v2026.04",
            "LOW",
            "/dispatch/score-rules?merchantId=S001",
            now.minusDays(1),
            "READ",
            now.minusHours(20),
            now.minusHours(20)));
    messageCenterStore.put(
        "MSG20260419004",
        new InquiryMessageCenterEntity(
            "MSG20260419004",
            "S002",
            "提货单状态变更提醒",
            "提货单 PU-20260419-PU20260419001 已更新为运输中。",
            "TRANSACTION",
            "PU20260419001",
            "MEDIUM",
            "/inquiry/pickup/pass?contactMobile=13900139000",
            now.minusHours(3),
            "UNREAD",
            null,
            now.minusHours(3)));
  }

  private void seedH5Home() {
    h5HomeStore.put(
        "CN",
        new InquiryH5HomeEntity(
            "全国",
            "多云 22°C",
            "货袋子H5首页",
            "钢材交易一站式服务",
            List.of(
                new InquiryH5HomeEntity.QuickNavEntity(
                    "AI_INQUIRY",
                    "AI询价",
                    "3步发布需求",
                    "https://cdn.huodaizi.com/icon/ai-inquiry.png",
                    "/inquiry/create",
                    ""),
                new InquiryH5HomeEntity.QuickNavEntity(
                    "SPOT_MALL",
                    "现货大厅",
                    "实时库存与报价",
                    "https://cdn.huodaizi.com/icon/spot.png",
                    "/",
                    ""),
                new InquiryH5HomeEntity.QuickNavEntity(
                    "MERCHANT_SCORE",
                    "信用评分",
                    "履约与风控评分",
                    "https://cdn.huodaizi.com/icon/score.png",
                    "/merchant/credit/score?merchantId=S001",
                    ""),
                new InquiryH5HomeEntity.QuickNavEntity(
                    "MESSAGE_CENTER",
                    "消息中心",
                    "待办与提醒聚合",
                    "https://cdn.huodaizi.com/icon/message.png",
                    "/merchant/message-center?merchantId=S001",
                    "2")),
            List.of(
                new InquiryH5HomeEntity.BannerEntity(
                    "BNR001",
                    "钢贸商家增长计划",
                    "入驻即送30条高意向线索",
                    "/merchant/subscription?merchantId=S001",
                    "orange"),
                new InquiryH5HomeEntity.BannerEntity(
                    "BNR002",
                    "对账通限时体验",
                    "履约到回款全链路协同",
                    "/inquiry/reconcile/pass?contactMobile=13800138000",
                    "blue")),
            List.of(
                new InquiryH5HomeEntity.MarketCardEntity("螺纹钢 HRB400E", "Φ20*12m", "唐山", "3520", "-20", "860"),
                new InquiryH5HomeEntity.MarketCardEntity("热轧卷板 Q235B", "3.0*1500*C", "无锡", "3680", "+15", "420"),
                new InquiryH5HomeEntity.MarketCardEntity("中厚板 Q355B", "10*2000*8000", "郑州", "3890", "+8", "220")),
            List.of(
                new InquiryH5HomeEntity.RecommendationEntity(
                    "S001",
                    "唐山弘达钢贸",
                    "92",
                    "响应快｜可开票｜当日排货",
                    "7",
                    "/merchant/lead/manage?merchantId=S001"),
                new InquiryH5HomeEntity.RecommendationEntity(
                    "S002",
                    "无锡铭泰供应链",
                    "89",
                    "支持月结｜回单完整",
                    "11",
                    "/merchant/lead/manage?merchantId=S001"),
                new InquiryH5HomeEntity.RecommendationEntity(
                    "S003",
                    "郑州鑫诚贸易",
                    "86",
                    "华中专线｜夜间装车",
                    "14",
                    "/merchant/lead/manage?merchantId=S001")),
            LocalDateTime.now().minusMinutes(20)));
  }

  private void seedH5InquiryStep1Drafts() {
    LocalDateTime now = LocalDateTime.now().minusMinutes(15);
    InquiryH5InquiryStep1DraftEntity draft =
        new InquiryH5InquiryStep1DraftEntity(
            "H5DRAFT20260418001",
            "REBAR",
            "HRB400E Φ20*12m",
            "唐山",
            "120",
            "YES",
            "13800138000",
            "现货优先，当日可装车",
            "2026-04-21",
            "09:00-18:00",
            "N",
            "Y",
            "",
            "",
            "",
            "STEP1_SAVED",
            now.minusMinutes(4),
            now);
    h5InquiryStep1DraftStore.put(draft.getDraftId(), draft);
  }

  private String maskPhone(String phone) {
    String digits = normalizePhone(phone);
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }

  private String normalizeMonthOrFallback(String month) {
    String trimmed = defaultText(month, "");
    if (trimmed.matches("^\\d{4}-\\d{2}$")) {
      return trimmed;
    }
    return LocalDate.now().toString().substring(0, 7);
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

  private BigDecimal parseMoneyOrDefault(String text, BigDecimal fallback, String field) {
    if (text == null || text.isBlank()) {
      return fallback;
    }
    try {
      return new BigDecimal(text.trim());
    } catch (Exception ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), field + " 格式错误");
    }
  }

  private String toMoney(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "*";
    }
    String trimmed = name.trim();
    if (trimmed.length() == 1) {
      return "*";
    }
    return trimmed.substring(0, 1) + "**";
  }

  private List<InquiryQuoteCompareEntity> mockQuoteRows(String inquiryId) {
    return List.of(
        new InquiryQuoteCompareEntity(
            inquiryId,
            inquiryId + "-Q1",
            "S001",
            "唐山弘达钢贸",
            "A",
            "3520",
            "422400",
            "含税到厂",
            "月结15天",
            "1",
            "唐山",
            "7",
            "98",
            "5",
            "YES",
            "YES",
            "当日16点前可装车，支持电子回单",
            LocalDateTime.now().minusHours(1)),
        new InquiryQuoteCompareEntity(
            inquiryId,
            inquiryId + "-Q2",
            "S002",
            "无锡铭泰供应链",
            "A",
            "3490",
            "418800",
            "含税到厂",
            "月结30天",
            "2",
            "无锡",
            "12",
            "94",
            "4",
            "YES",
            "YES",
            "支持月结客户，需提前锁货",
            LocalDateTime.now().minusHours(2)),
        new InquiryQuoteCompareEntity(
            inquiryId,
            inquiryId + "-Q3",
            "S003",
            "郑州鑫诚贸易",
            "B",
            "3470",
            "416400",
            "不含税出库",
            "现款现货",
            "3",
            "郑州",
            "20",
            "90",
            "3",
            "NO",
            "NO",
            "低价方案，不含票据",
            LocalDateTime.now().minusHours(3)));
  }
}
