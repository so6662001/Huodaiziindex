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
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryInquiryRepository {
  private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, InquiryEntity> store = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, List<InquiryQuoteCompareEntity>> quoteStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, InquiryMerchantLeadEntity> merchantLeadStore = new ConcurrentHashMap<>();

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

  public InquiryEntity getById(String id) {
    InquiryEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
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

  private InquiryMerchantLeadEntity requireMerchantLead(String leadId) {
    InquiryMerchantLeadEntity entity = merchantLeadStore.get(leadId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "商家线索不存在");
    }
    return entity;
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

  private String maskPhone(String phone) {
    String digits = normalizePhone(phone);
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
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
