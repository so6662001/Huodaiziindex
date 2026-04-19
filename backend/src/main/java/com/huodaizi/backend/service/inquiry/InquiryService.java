package com.huodaizi.backend.service.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteSortBy;
import com.huodaizi.backend.dto.inquiry.InquirySuccessRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessResponse;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryEntity;
import com.huodaizi.backend.repository.inquiry.InquiryQuoteCompareEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

  private final InMemoryInquiryRepository repository;

  public InquiryService(InMemoryInquiryRepository repository) {
    this.repository = repository;
  }

  public InquiryCreateResponse create(InquiryCreateRequest request) {
    InquiryEntity entity = repository.create(request);
    return new InquiryCreateResponse(
        entity.getId(),
        entity.getInquiryNo(),
        entity.getStatus().name(),
        "询价已提交，正在为您匹配优质商家");
  }

  public InquiryListResponse list(InquiryListRequest request) {
    List<InquiryEntity> filtered = repository.listMine(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<InquiryEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    return new InquiryListResponse(
        paged.stream().map(this::toItem).toList(),
        filtered.size(),
        page,
        pageSize,
        countByStatus(filtered, InquiryStatus.OPEN),
        countByStatus(filtered, InquiryStatus.QUOTING),
        countByStatus(filtered, InquiryStatus.DEAL_DONE),
        countByStatus(filtered, InquiryStatus.CLOSED));
  }

  public InquiryQuoteCompareResponse compareQuotes(InquiryQuoteCompareRequest request) {
    InquiryEntity inquiry = repository.getById(request.inquiryId());
    List<InquiryQuoteCompareEntity> all = repository.listQuoteCompareItems(request);

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryQuoteCompareEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);

    return new InquiryQuoteCompareResponse(
        inquiry.getId(),
        inquiry.getInquiryNo(),
        inquiry.getSpecText(),
        inquiry.getDemandQtyTon(),
        inquiry.getDeliveryCity(),
        inquiry.getStatus().name(),
        paged.stream().map(this::toCompareItem).toList(),
        all.size(),
        page,
        pageSize);
  }

  public InquirySuccessResponse success(InquirySuccessRequest request) {
    InquiryEntity inquiry = repository.getById(request.inquiryId());
    if (!inquiry.getContactMobile().equals(request.contactMobile().trim())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
    }

    List<InquiryQuoteCompareEntity> rows = repository.listQuoteCompareItems(
        new InquiryQuoteCompareRequest(
            inquiry.getId(),
            request.contactMobile(),
            null,
            null,
            "TOTAL_PRICE",
            1,
            100));

    int quoteCount = rows.size();

    List<String> nextSteps =
        List.of(
            "1) 等待商家报价并关注响应时效",
            "2) 进入报价对比页按总价/时效筛选",
            "3) 优先选择履约评分高且响应快的商家");

    return new InquirySuccessResponse(
        inquiry.getId(),
        inquiry.getInquiryNo(),
        inquiry.getStatus().name(),
        inquiry.getSpecText(),
        inquiry.getDeliveryCity(),
        inquiry.getDemandQtyTon(),
        maskPhone(inquiry.getContactMobile()),
        inquiry.getCreatedAt().toString(),
        quoteCount,
        nextSteps,
        "/inquiry/compare?inquiryId=" + inquiry.getId());
  }

  private InquiryItemDTO toItem(InquiryEntity entity) {
    return new InquiryItemDTO(
        entity.getId(),
        entity.getInquiryNo(),
        entity.getCategoryCode(),
        entity.getSpecText(),
        entity.getDemandQtyTon(),
        entity.getDeliveryCity(),
        entity.getExpectedDeliveryAt(),
        entity.getInvoiceNeed(),
        entity.getStatus().name(),
        entity.getQuoteSupplierCount(),
        entity.getCreatedAt().toString(),
        entity.getUpdatedAt().toString());
  }

  private int countByStatus(List<InquiryEntity> items, InquiryStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private InquiryQuoteCompareItemDTO toCompareItem(InquiryQuoteCompareEntity entity) {
    return new InquiryQuoteCompareItemDTO(
        entity.getQuoteId(),
        entity.getInquiryId(),
        entity.getSupplierId(),
        entity.getSupplierName(),
        entity.getInventoryLocation(),
        entity.getSupplierLevel(),
        entity.getServiceScore(),
        entity.getFulfillmentRate(),
        entity.getPricePerTon(),
        entity.getTotalAmount(),
        entity.getTaxMode(),
        entity.getCanInvoice(),
        entity.getCanFreight(),
        entity.getDeliveryDays(),
        entity.getResponseMinutes(),
        entity.getPaymentTerm(),
        entity.getQuoteRemark(),
        "ACTIVE",
        entity.getQuoteTime().toString());
  }

  private String maskPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "未公开";
    }
    String digits = phone.replaceAll("\\D", "");
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }
}

