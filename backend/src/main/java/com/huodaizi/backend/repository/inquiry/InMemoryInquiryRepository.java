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
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
  private final ConcurrentMap<String, InquiryEntity> store = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, List<InquiryQuoteCompareEntity>> quoteStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantLeadEntity> merchantLeadStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantCreditScoreEntity> merchantCreditScoreStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquirySubscriptionPlanEntity> subscriptionPlanStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantSubscriptionEntity> merchantSubscriptionStore =
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
    entity.setStatus(status);
    if (request.comment() != null && !request.comment().isBlank()) {
      entity.updateQuote(
          entity.getUnitPrice(),
          entity.getTotalAmount(),
          entity.getDeliveryDays(),
          entity.getPaymentTerm(),
          request.comment().trim());
    }
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
