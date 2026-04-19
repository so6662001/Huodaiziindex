package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
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

  private void seed() {
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
  }
}
