package com.huodaizi.backend.service.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmPreviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmPreviewResponse;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmSubmitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmSubmitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreDimensionDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreTrendPointDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatusUpdateRequest;
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
import com.huodaizi.backend.repository.inquiry.InquiryMerchantCreditScoreEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
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

  public InquiryDealConfirmPreviewResponse dealConfirmPreview(InquiryDealConfirmPreviewRequest request) {
    InquiryEntity inquiry = repository.getById(request.inquiryId().trim());
    if (!inquiry.getContactMobile().equals(request.contactMobile().trim())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.NOT_FOUND.getCode(), "询价单不存在");
    }
    InquiryQuoteCompareEntity quote =
        repository.getQuoteById(request.inquiryId().trim(), request.quoteId().trim());
    return new InquiryDealConfirmPreviewResponse(
        inquiry.getId(),
        inquiry.getInquiryNo(),
        quote.getQuoteId(),
        quote.getSupplierId(),
        quote.getSupplierName(),
        inquiry.getSpecText(),
        inquiry.getDemandQtyTon(),
        inquiry.getDeliveryCity(),
        quote.getPricePerTon(),
        quote.getTotalAmount(),
        quote.getPaymentTerm(),
        quote.getDeliveryDays(),
        inquiry.getExpectedDeliveryAt(),
        inquiry.getInvoiceNeed(),
        "确认后不可撤销，请核对价格、票据与交期");
  }

  public InquiryDealConfirmSubmitResponse dealConfirmSubmit(InquiryDealConfirmSubmitRequest request) {
    InquiryQuoteCompareEntity quote =
        repository.confirmDeal(
            request.inquiryId().trim(),
            request.quoteId().trim(),
            request.contactMobile().trim(),
            request.buyerCompany() == null ? "" : request.buyerCompany().trim(),
            request.buyerContact() == null ? "" : request.buyerContact().trim(),
            request.buyerPhone() == null ? "" : request.buyerPhone().trim(),
            request.expectedSignDate() == null ? "" : request.expectedSignDate().trim(),
            request.remark() == null ? "" : request.remark().trim());
    InquiryEntity inquiry = repository.getById(request.inquiryId().trim());
    return new InquiryDealConfirmSubmitResponse(
        inquiry.getId(),
        inquiry.getInquiryNo(),
        quote.getQuoteId(),
        inquiry.getStatus().name(),
        quote.getSupplierId(),
        quote.getSupplierName(),
        quote.getTotalAmount(),
        "成交确认成功，平台将推进履约交付");
  }

  public InquiryPickupOrderCreateResponse createPickupOrder(InquiryPickupOrderCreateRequest request) {
    if (!request.agreedProtocol()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "请先同意提货服务协议");
    }
    InquiryPickupOrderEntity entity = repository.createPickupOrder(request);
    return new InquiryPickupOrderCreateResponse(
        entity.getPickupId(),
        entity.getPickupNo(),
        entity.getInquiryId(),
        entity.getQuoteId(),
        entity.getStatus().name(),
        "提货单已创建，等待卖方确认放货");
  }

  public InquiryPickupOrderListResponse listPickupOrders(InquiryPickupOrderListRequest request) {
    List<InquiryPickupOrderEntity> all = repository.listPickupOrders(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryPickupOrderEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    return new InquiryPickupOrderListResponse(
        paged.stream().map(this::toPickupItem).toList(),
        all.size(),
        page,
        pageSize,
        countPickupByStatus(all, InquiryPickupOrderStatus.CREATED),
        countPickupByStatus(all, InquiryPickupOrderStatus.CONFIRMED),
        countPickupByStatus(all, InquiryPickupOrderStatus.IN_TRANSIT),
        countPickupByStatus(all, InquiryPickupOrderStatus.SIGNED),
        countPickupByStatus(all, InquiryPickupOrderStatus.COMPLETED),
        countPickupByStatus(all, InquiryPickupOrderStatus.CANCELLED));
  }

  public InquiryPickupOrderDetailResponse pickupOrderDetail(
      String pickupId, InquiryPickupOrderListRequest request) {
    InquiryPickupOrderEntity entity = repository.getPickupOrderById(pickupId, request.contactMobile());
    return new InquiryPickupOrderDetailResponse(
        toPickupItem(entity),
        entity.getPickupAddress(),
        entity.getBuyerContact(),
        entity.getBuyerPhoneMasked(),
        entity.getTruckNo(),
        entity.getDriverName(),
        entity.getDriverPhoneMasked(),
        entity.getRemark());
  }

  public InquiryPickupOrderDetailResponse pickupOrderUpdateStatus(
      String pickupId, InquiryPickupOrderStatusUpdateRequest request) {
    if (request.status() == null || request.status().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    InquiryPickupOrderEntity entity = repository.updatePickupOrderStatus(pickupId, request);
    return new InquiryPickupOrderDetailResponse(
        toPickupItem(entity),
        entity.getPickupAddress(),
        entity.getBuyerContact(),
        entity.getBuyerPhoneMasked(),
        entity.getTruckNo(),
        entity.getDriverName(),
        entity.getDriverPhoneMasked(),
        request.remark() == null || request.remark().isBlank() ? entity.getRemark() : request.remark());
  }

  public InquiryReconcileOrderCreateResponse createReconcileOrder(InquiryReconcileOrderCreateRequest request) {
    if (!"MONTHLY".equalsIgnoreCase(request.settleType())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "settleType 目前仅支持 MONTHLY");
    }
    InquiryReconcileOrderEntity entity = repository.createReconcileOrder(request);
    return new InquiryReconcileOrderCreateResponse(
        entity.getReconcileId(),
        entity.getReconcileNo(),
        entity.getPickupOrderId(),
        entity.getStatus().name(),
        "对账单已创建，等待开票与回款登记");
  }

  public InquiryReconcileOrderListResponse listReconcileOrders(InquiryReconcileOrderListRequest request) {
    List<InquiryReconcileOrderEntity> all = repository.listReconcileOrders(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryReconcileOrderEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    return new InquiryReconcileOrderListResponse(
        paged.stream().map(this::toReconcileItem).toList(),
        all.size(),
        page,
        pageSize,
        countReconcileByStatus(all, InquiryReconcileOrderStatus.CREATED),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.INVOICE_PENDING),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.INVOICED),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.CONFIRMED),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.PARTIAL_PAID),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.PAID),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.CLOSED),
        countReconcileByStatus(all, InquiryReconcileOrderStatus.DISPUTED));
  }

  public InquiryReconcileOrderDetailResponse reconcileOrderDetail(
      String reconcileOrderId, InquiryReconcileOrderListRequest request) {
    InquiryReconcileOrderEntity entity =
        repository.getReconcileOrderById(reconcileOrderId, request.contactMobile());
    return new InquiryReconcileOrderDetailResponse(
        toReconcileItem(entity),
        entity.getLatestRemark(),
        entity.getInvoiceAmount(),
        entity.getReceivableAmount(),
        entity.getPaidAmount(),
        entity.getOutstandingAmount(),
        entity.getDueDate(),
        statusToInvoiceStatus(entity.getStatus()));
  }

  public InquiryReconcileOrderDetailResponse reconcileOrderUpdateStatus(
      String reconcileOrderId, InquiryReconcileOrderStatusUpdateRequest request) {
    if (request.status() == null || request.status().isBlank()) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    InquiryReconcileOrderEntity entity = repository.updateReconcileOrderStatus(reconcileOrderId, request);
    return new InquiryReconcileOrderDetailResponse(
        toReconcileItem(entity),
        request.remark() == null || request.remark().isBlank() ? entity.getLatestRemark() : request.remark(),
        entity.getInvoiceAmount(),
        entity.getReceivableAmount(),
        entity.getPaidAmount(),
        entity.getOutstandingAmount(),
        entity.getDueDate(),
        statusToInvoiceStatus(entity.getStatus()));
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

  public InquiryMerchantCreditScoreResponse merchantCreditScore(InquiryMerchantCreditScoreRequest request) {
    InquiryMerchantCreditScoreEntity entity = repository.merchantCreditScore(request);
    List<InquiryMerchantCreditScoreDimensionDTO> dimensions =
        entity.getDimensions().stream()
            .map(
                item ->
                    new InquiryMerchantCreditScoreDimensionDTO(
                        item.code(), item.name(), item.score(), item.weight(), item.trend(), item.summary()))
            .toList();
    List<InquiryMerchantCreditScoreTrendPointDTO> trend =
        entity.getTrendPoints().stream()
            .map(
                point ->
                    new InquiryMerchantCreditScoreTrendPointDTO(
                        point.month(),
                        point.creditScore(),
                        point.fulfillmentRate(),
                        point.disputeRate(),
                        point.responseMinutes()))
            .toList();
    return new InquiryMerchantCreditScoreResponse(
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getScore(),
        entity.getGrade(),
        entity.getRankPercent(),
        entity.getScoreVersion(),
        entity.getUpdatedAt().toString(),
        dimensions,
        trend,
        entity.getRisks(),
        entity.getSuggestions());
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

  private InquiryPickupOrderItemDTO toPickupItem(InquiryPickupOrderEntity entity) {
    return new InquiryPickupOrderItemDTO(
        entity.getPickupId(),
        entity.getPickupNo(),
        entity.getInquiryId(),
        entity.getInquiryNo(),
        entity.getQuoteId(),
        entity.getSupplierId(),
        entity.getSupplierName(),
        entity.getBuyerCompany(),
        entity.getPickupAddress(),
        entity.getBuyerContact(),
        entity.getBuyerPhoneMasked(),
        entity.getPickupDate(),
        "09:00-18:00",
        entity.getTruckNo(),
        entity.getDriverName(),
        entity.getDriverPhoneMasked(),
        entity.getSpecText() + " / " + entity.getQuantityTon() + "吨",
        entity.getStatus().name(),
        pickupStatusText(entity.getStatus()),
        entity.getCreatedAt().toString(),
        entity.getUpdatedAt().toString());
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

  private InquiryReconcileOrderItemDTO toReconcileItem(InquiryReconcileOrderEntity entity) {
    return new InquiryReconcileOrderItemDTO(
        entity.getReconcileId(),
        entity.getReconcileNo(),
        entity.getPickupOrderId(),
        entity.getPickupOrderNo(),
        entity.getInquiryId(),
        entity.getInquiryNo(),
        entity.getQuoteId(),
        entity.getSupplierId(),
        entity.getSupplierName(),
        entity.getBuyerCompany(),
        entity.getContactMobileMasked(),
        entity.getGoodsSummary(),
        entity.getReceivableAmount(),
        entity.getPaidAmount(),
        entity.getOutstandingAmount(),
        statusToInvoiceStatus(entity.getStatus()),
        entity.getDueDate(),
        entity.getStatus().name(),
        reconcileStatusText(entity.getStatus()),
        entity.getCreatedAt().toString(),
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

  private int countPickupByStatus(List<InquiryPickupOrderEntity> items, InquiryPickupOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private int countReconcileByStatus(
      List<InquiryReconcileOrderEntity> items, InquiryReconcileOrderStatus status) {
    return (int) items.stream().filter(item -> item.getStatus() == status).count();
  }

  private String pickupStatusText(InquiryPickupOrderStatus status) {
    return switch (status) {
      case CREATED -> "待确认";
      case CONFIRMED -> "已确认";
      case IN_TRANSIT -> "运输中";
      case SIGNED -> "已签收";
      case COMPLETED -> "已完成";
      case CANCELLED -> "已取消";
    };
  }

  private String reconcileStatusText(InquiryReconcileOrderStatus status) {
    return switch (status) {
      case CREATED -> "已创建";
      case INVOICE_PENDING -> "待开票";
      case INVOICED -> "已开票";
      case CONFIRMED -> "已确认";
      case PARTIAL_PAID -> "部分回款";
      case PAID -> "已回款";
      case CLOSED -> "已关闭";
      case DISPUTED -> "争议中";
    };
  }

  private String statusToInvoiceStatus(InquiryReconcileOrderStatus status) {
    return switch (status) {
      case CREATED, INVOICE_PENDING -> "UNISSUED";
      case INVOICED, CONFIRMED, PARTIAL_PAID, PAID, CLOSED -> "ISSUED";
      case DISPUTED -> "DISPUTED";
    };
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

