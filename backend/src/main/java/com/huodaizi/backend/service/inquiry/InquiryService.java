package com.huodaizi.backend.service.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchBatchUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchOverviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchOverviewResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessResponse;
import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.inquiry.InquiryQuoteCompareEntity;
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

  public InquiryMerchantLeadListResponse merchantLeads(InquiryMerchantLeadListRequest request) {
    List<InquiryMerchantLeadEntity> all = repository.listMerchantLeads(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryMerchantLeadEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    return new InquiryMerchantLeadListResponse(
        paged.stream().map(this::toMerchantLeadItem).toList(),
        all.size(),
        page,
        pageSize,
        countMerchantByStatus(all, InquiryMerchantLeadStatus.NEW),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.QUOTED),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.WON),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.LOST),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.CLOSED));
  }

  public InquiryMerchantLeadDetailResponse merchantLeadDetail(
      String leadId, InquiryMerchantLeadListRequest request) {
    InquiryMerchantLeadEntity lead = repository.merchantLeadDetail(leadId, request.merchantId());
    String nextAction = "优先报价后30分钟内电话回访，提升转化";
    return new InquiryMerchantLeadDetailResponse(
        toMerchantLeadItem(lead),
        lead.getSpecText(),
        lead.getDeliveryCity(),
        lead.getDemandQtyTon(),
        lead.getInvoiceNeed(),
        lead.getExpectedDeliveryAt(),
        nextAction);
  }

  public InquiryMerchantLeadItemDTO merchantQuote(
      String leadId, InquiryMerchantLeadQuoteRequest request) {
    validateQuote(request);
    InquiryMerchantLeadEntity updated = repository.merchantLeadQuote(leadId, request);
    return toMerchantLeadItem(updated);
  }

  public InquiryMerchantLeadItemDTO merchantUpdateStatus(
      String leadId, InquiryMerchantLeadStatusUpdateRequest request) {
    if (request.status() == null || request.status().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    InquiryMerchantLeadEntity updated = repository.merchantLeadUpdateStatus(leadId, request);
    return toMerchantLeadItem(updated);
  }

  public InquiryQuoteWorkbenchOverviewResponse quoteWorkbenchOverview(
      InquiryQuoteWorkbenchOverviewRequest request) {
    List<InquiryMerchantLeadEntity> all = repository.workbenchOverview(request);
    int newCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.NEW);
    int contactedCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.CONTACTED);
    int quotedCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.QUOTED);
    int wonCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.WON);
    int lostCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.LOST);
    int closedCount = countMerchantByStatus(all, InquiryMerchantLeadStatus.CLOSED);
    int pendingQuoteCount = newCount + contactedCount;
    String avgResponseMinutes = averageResponseMinutes(all);
    String quoteRate = calcRate(quotedCount, all.size());
    return new InquiryQuoteWorkbenchOverviewResponse(
        request.merchantId().trim(),
        all.size(),
        newCount,
        contactedCount,
        quotedCount,
        wonCount,
        lostCount,
        closedCount,
        pendingQuoteCount,
        avgResponseMinutes,
        quoteRate);
  }

  public InquiryQuoteWorkbenchTaskListResponse quoteWorkbenchTasks(InquiryQuoteWorkbenchTaskRequest request) {
    List<InquiryMerchantLeadEntity> all = repository.workbenchTasks(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryMerchantLeadEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    return new InquiryQuoteWorkbenchTaskListResponse(
        paged.stream().map(this::toWorkbenchTaskItem).toList(),
        all.size(),
        page,
        pageSize,
        countMerchantByStatus(all, InquiryMerchantLeadStatus.NEW),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.QUOTED),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.CONTACTED),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.WON),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.LOST),
        countMerchantByStatus(all, InquiryMerchantLeadStatus.CLOSED));
  }

  public InquiryQuoteWorkbenchTaskListResponse quoteWorkbenchBatchUpdate(
      InquiryQuoteWorkbenchBatchUpdateRequest request) {
    if (request.leadIds() == null || request.leadIds().isEmpty()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "leadIds 不能为空");
    }
    if (request.status() == null || request.status().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    List<InquiryMerchantLeadEntity> updated = repository.workbenchBatchUpdate(request);
    return new InquiryQuoteWorkbenchTaskListResponse(
        updated.stream().map(this::toWorkbenchTaskItem).toList(),
        updated.size(),
        1,
        updated.size(),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.NEW),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.QUOTED),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.CONTACTED),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.WON),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.LOST),
        countMerchantByStatus(updated, InquiryMerchantLeadStatus.CLOSED));
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

  private InquiryMerchantLeadItemDTO toMerchantLeadItem(InquiryMerchantLeadEntity entity) {
    return new InquiryMerchantLeadItemDTO(
        entity.getId(),
        entity.getInquiryId(),
        entity.getInquiryNo(),
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getSpecText(),
        entity.getDemandQtyTon(),
        entity.getDeliveryCity(),
        entity.getInvoiceNeed(),
        entity.getContactNameMasked(),
        entity.getContactMobileMasked(),
        entity.getExpectedDeliveryAt(),
        entity.getStatus().name(),
        entity.getMerchantName(),
        entity.getQuoteRemark(),
        entity.getUpdatedAt().toString());
  }

  private InquiryQuoteWorkbenchTaskItemDTO toWorkbenchTaskItem(InquiryMerchantLeadEntity entity) {
    return new InquiryQuoteWorkbenchTaskItemDTO(
        entity.getId(),
        entity.getLeadNo(),
        entity.getInquiryId(),
        entity.getInquiryNo(),
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getSpecText(),
        entity.getDemandQtyTon(),
        entity.getDeliveryCity(),
        entity.getInvoiceNeed(),
        entity.getExpectedDeliveryAt(),
        quoteAgeMinutes(entity),
        entity.getUnitPrice() != null && !entity.getUnitPrice().isBlank(),
        entity.getStatus().name(),
        entity.getQuoteRemark(),
        entity.getUpdatedAt().toString());
  }

  private int countMerchantByStatus(List<InquiryMerchantLeadEntity> items, InquiryMerchantLeadStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private String calcRate(int numerator, int denominator) {
    if (denominator <= 0) {
      return "0.0%";
    }
    double pct = numerator * 100.0 / denominator;
    return String.format(java.util.Locale.ROOT, "%.1f%%", pct);
  }

  private String averageResponseMinutes(List<InquiryMerchantLeadEntity> items) {
    List<Integer> values =
        items.stream()
            .map(InquiryMerchantLeadEntity::getResponseMinutes)
            .filter(v -> v != null && !v.isBlank())
            .map(v -> {
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

  private String quoteAgeMinutes(InquiryMerchantLeadEntity entity) {
    long minutes =
        java.time.Duration.between(entity.getCreatedAt(), entity.getUpdatedAt()).toMinutes();
    return String.valueOf(Math.max(minutes, 0));
  }

  private void validateQuote(InquiryMerchantLeadQuoteRequest request) {
    if (request.unitPrice() == null || request.unitPrice().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "unitPrice 不能为空");
    }
    if (request.deliveryDays() == null || request.deliveryDays().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "deliveryDays 不能为空");
    }
    if (request.paymentTerm() == null || request.paymentTerm().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "paymentTerm 不能为空");
    }
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

