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
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineItemDTO;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineResponse;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanFeatureDTO;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanItemDTO;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanListRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanListResponse;
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
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderPaymentRequest;
import com.huodaizi.backend.dto.inquiry.InquiryBillingOrderPaymentResponse;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleBonusItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleCaseItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleDimensionDTO;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRulePenaltyItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDispatchScoreRuleResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterReadAllRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterReadRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMessageCenterReadResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeBannerItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeMarketCardDTO;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeQuickNavItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeRecommendationItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5HomeResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1InitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1InitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1OptionDTO;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1SaveRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep1SaveResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep2InitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep2InitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep2SubmitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep2SubmitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep3Request;
import com.huodaizi.backend.dto.inquiry.InquiryH5InquiryStep3Response;
import com.huodaizi.backend.dto.inquiry.InquiryH5QuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5QuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadQuickQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadQuickQuoteResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadQuickStatusRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadQuickStatusResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MerchantLeadResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberMineRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberMineResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberOverviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberOverviewResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberPlanListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5MemberPlanListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderDetailRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderQuickStatusRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5PickupOrderQuickStatusResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5QuickQuoteInitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5QuickQuoteInitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderDetailRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderQuickStatusRequest;
import com.huodaizi.backend.dto.inquiry.InquiryH5ReconcileOrderQuickStatusResponse;
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
import com.huodaizi.backend.repository.inquiry.InquiryMessageCenterEntity;
import com.huodaizi.backend.repository.inquiry.InquiryH5HomeEntity;
import com.huodaizi.backend.repository.inquiry.InquiryH5InquiryStep1DraftEntity;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantSubscriptionEntity;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryQuoteCompareEntity;
import com.huodaizi.backend.repository.inquiry.InquirySubscriptionPlanEntity;
import com.huodaizi.backend.repository.inquiry.InquiryBillingOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryDispatchScoreRuleEntity;
import java.time.LocalDate;
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

  public InquirySubscriptionPlanListResponse subscriptionPlans(InquirySubscriptionPlanListRequest request) {
    List<InquirySubscriptionPlanEntity> all = repository.listSubscriptionPlans(request);
    String recommendPlanCode =
        all.stream()
            .filter(InquirySubscriptionPlanEntity::isRecommended)
            .map(InquirySubscriptionPlanEntity::getPlanCode)
            .findFirst()
            .orElse("");
    return new InquirySubscriptionPlanListResponse(
        request.merchantId().trim(),
        "merchant-saas",
        all.stream().map(this::toSubscriptionPlanItem).toList(),
        recommendPlanCode,
        "CNY");
  }

  public InquirySubscriptionCreateResponse createSubscription(InquirySubscriptionCreateRequest request) {
    InquiryMerchantSubscriptionEntity entity = repository.createSubscription(request);
    return new InquirySubscriptionCreateResponse(
        entity.getSubscriptionId(),
        entity.getSubscriptionNo(),
        entity.getMerchantId(),
        entity.getPlanCode(),
        entity.getPlanName(),
        entity.getStatus(),
        entity.getStartAt(),
        entity.getEndAt(),
        entity.getAmountYuan(),
        "CNY",
        "订阅开通成功，功能权限将在 5 分钟内生效");
  }

  public InquirySubscriptionMineResponse subscriptionMine(InquirySubscriptionMineRequest request) {
    List<InquiryMerchantSubscriptionEntity> all = repository.listSubscriptions(request);
    int page = 1;
    int pageSize = all.size();
    int total = all.size();
    int activeCount = (int) all.stream().filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus())).count();
    int expiringSoonCount =
        (int) all.stream().filter(item -> "EXPIRING_SOON".equalsIgnoreCase(item.getStatus())).count();
    int expiredCount = (int) all.stream().filter(item -> "EXPIRED".equalsIgnoreCase(item.getStatus())).count();
    return new InquirySubscriptionMineResponse(
        request.merchantId().trim(),
        all.stream().map(this::toSubscriptionMineItem).toList(),
        total,
        page,
        pageSize,
        activeCount,
        expiringSoonCount,
        expiredCount);
  }

  public InquiryBillingOrderListResponse billingOrders(InquiryBillingOrderListRequest request) {
    List<InquiryBillingOrderEntity> all = repository.listBillingOrders(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryBillingOrderEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    int unpaidCount = (int) all.stream().filter(item -> "UNPAID".equalsIgnoreCase(item.getStatus())).count();
    int partialPaidCount =
        (int) all.stream().filter(item -> "PARTIAL_PAID".equalsIgnoreCase(item.getStatus())).count();
    int paidCount = (int) all.stream().filter(item -> "PAID".equalsIgnoreCase(item.getStatus())).count();
    java.math.BigDecimal totalReceivable =
        all.stream()
            .map(item -> new java.math.BigDecimal(item.getAmountYuan()))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    java.math.BigDecimal totalPaid =
        all.stream()
            .map(item -> new java.math.BigDecimal(item.getPaidAmountYuan()))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    java.math.BigDecimal totalOutstanding = totalReceivable.subtract(totalPaid).max(java.math.BigDecimal.ZERO);
    return new InquiryBillingOrderListResponse(
        request.merchantId().trim(),
        paged.stream().map(this::toBillingItem).toList(),
        all.size(),
        page,
        pageSize,
        unpaidCount,
        partialPaidCount,
        paidCount,
        toMoney(totalReceivable),
        toMoney(totalPaid),
        toMoney(totalOutstanding));
  }

  public InquiryBillingOrderDetailResponse billingOrderDetail(
      String billId, InquiryBillingOrderListRequest request) {
    InquiryBillingOrderEntity entity = repository.getBillingOrderById(billId, request);
    java.math.BigDecimal amount = new java.math.BigDecimal(entity.getAmountYuan());
    java.math.BigDecimal taxRate = new java.math.BigDecimal("0.13");
    java.math.BigDecimal taxAmount = amount.multiply(taxRate);
    java.math.BigDecimal netAmount = amount.subtract(taxAmount).max(java.math.BigDecimal.ZERO);
    return new InquiryBillingOrderDetailResponse(
        toBillingItem(entity),
        "套餐账单（" + entity.getPlanName() + "）",
        entity.getAmountYuan(),
        "13%",
        toMoney(taxAmount),
        toMoney(netAmount),
        entity.getLatestRemark());
  }

  public InquiryBillingOrderPaymentResponse billingOrderPay(
      String billId, InquiryBillingOrderPaymentRequest request) {
    InquiryBillingOrderEntity entity = repository.payBillingOrder(billId, request);
    return new InquiryBillingOrderPaymentResponse(
        entity.getBillId(),
        entity.getBillNo(),
        entity.getStatus(),
        entity.getPaidAmountYuan(),
        entity.getUpdatedAt().toString(),
        "回款登记成功，当前状态：" + billingStatusText(entity.getStatus()));
  }

  public InquiryDispatchScoreRuleResponse dispatchScoreRules(InquiryDispatchScoreRuleRequest request) {
    InquiryDispatchScoreRuleEntity entity = repository.dispatchScoreRule(request);
    return new InquiryDispatchScoreRuleResponse(
        "-",
        entity.getRuleVersion(),
        entity.getSceneName(),
        "0-100",
        entity.getScoreFormula(),
        "按公开维度计算，平台每周滚动更新，付费因素仅做小幅加成",
        entity.getDimensions().stream()
            .map(
                item ->
                    new InquiryDispatchScoreRuleDimensionDTO(
                        item.code(),
                        item.name(),
                        item.weight(),
                        item.desc(),
                        item.scoreMethod(),
                        item.dataSource()))
            .toList(),
        entity.getBonuses().stream()
            .map(
                item ->
                    new InquiryDispatchScoreRuleBonusItemDTO(
                        item.code(),
                        item.name(),
                        "BONUS",
                        parseScoreImpact(item.scoreChange()),
                        item.trigger(),
                        item.cap()))
            .toList(),
        entity.getPenalties().stream()
            .map(
                item ->
                    new InquiryDispatchScoreRulePenaltyItemDTO(
                        item.code(), item.name(), item.scoreChange(), item.trigger(), item.recovery()))
            .toList(),
        entity.getCases().stream()
            .map(
                item ->
                    new InquiryDispatchScoreRuleCaseItemDTO(
                        item.merchantName(),
                        "线索分发",
                        item.score(),
                        item.score(),
                        item.level(),
                        item.explanation()))
            .toList(),
        entity.getUpdatedAt().toString());
  }

  public InquiryMessageCenterListResponse messageCenterList(InquiryMessageCenterListRequest request) {
    List<InquiryMessageCenterEntity> all = repository.listMessageCenter(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<InquiryMessageCenterEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    int unreadCount = (int) all.stream().filter(item -> "UNREAD".equalsIgnoreCase(item.getReadStatus())).count();
    int readCount = all.size() - unreadCount;
    int systemCount = (int) all.stream().filter(item -> "SYSTEM".equalsIgnoreCase(item.getBizType())).count();
    int transactionCount =
        (int)
            all.stream()
                .filter(item -> "INQUIRY".equalsIgnoreCase(item.getBizType()))
                .count();
    int riskCount = (int) all.stream().filter(item -> "RISK".equalsIgnoreCase(item.getBizType())).count();
    return new InquiryMessageCenterListResponse(
        request.merchantId().trim(),
        paged.stream().map(this::toMessageItem).toList(),
        all.size(),
        page,
        pageSize,
        unreadCount,
        readCount,
        systemCount,
        transactionCount,
        riskCount);
  }

  public InquiryMessageCenterDetailResponse messageCenterDetail(
      String messageId, InquiryMessageCenterListRequest request) {
    InquiryMessageCenterEntity entity = repository.messageCenterDetail(messageId, request);
    return new InquiryMessageCenterDetailResponse(
        toMessageItem(entity),
        entity.getContent(),
        entity.getActionUrl(),
        entity.getBizId(),
        entity.getBizType());
  }

  public InquiryMessageCenterReadResponse messageCenterRead(
      String messageId, InquiryMessageCenterReadRequest request) {
    InquiryMessageCenterEntity entity = repository.messageCenterRead(messageId, request);
    return new InquiryMessageCenterReadResponse(
        request.merchantId().trim(),
        entity.getMessageId(),
        entity.getReadStatus(),
        "READ".equalsIgnoreCase(entity.getReadStatus()) ? "已读" : "未读",
        entity.getReadAt() == null ? "" : entity.getReadAt().toString(),
        "消息已标记为已读");
  }

  public InquiryMessageCenterReadResponse messageCenterReadAll(InquiryMessageCenterReadAllRequest request) {
    repository.messageCenterReadAll(request);
    return new InquiryMessageCenterReadResponse(
        request.merchantId().trim(),
        "",
        "READ",
        "已读",
        java.time.LocalDateTime.now().toString(),
        "已全部标记为已读");
  }

  public InquiryH5HomeResponse h5Home(InquiryH5HomeRequest request) {
    InquiryH5HomeEntity entity = repository.h5Home(request);
    return new InquiryH5HomeResponse(
        entity.getCity(),
        entity.getWeather(),
        entity.getUpdatedAt().toString(),
        entity.getQuickNavs().stream()
            .map(
                item ->
                    new InquiryH5HomeQuickNavItemDTO(
                        item.code(), item.title(), item.icon(), item.actionUrl(), item.badge()))
            .toList(),
        entity.getBanners().stream()
            .map(
                item ->
                    new InquiryH5HomeBannerItemDTO(
                        item.bannerId(),
                        item.title(),
                        item.desc(),
                        item.colorTag(),
                        "查看详情",
                        item.actionUrl()))
            .toList(),
        entity.getMarketCards().stream()
            .map(
                item ->
                    new InquiryH5HomeMarketCardDTO(
                        item.commodity(),
                        item.commodity(),
                        item.spec(),
                        item.city(),
                        item.latestPrice(),
                        item.trend(),
                        item.volume()))
            .toList(),
        entity.getRecommendations().stream()
            .map(
                item ->
                    new InquiryH5HomeRecommendationItemDTO(
                        item.merchantId(),
                        item.merchantName(),
                        item.tags(),
                        item.score(),
                        item.responseMinutes(),
                        "主营钢材",
                        entity.getCity(),
                        item.actionUrl()))
            .toList());
  }

  public InquiryH5InquiryStep1InitResponse h5InquiryStep1Init(InquiryH5InquiryStep1InitRequest request) {
    String city = request.city() == null || request.city().isBlank() ? "全国" : request.city().trim();
    InquiryH5InquiryStep1DraftEntity draft = repository.initH5InquiryStep1Draft(city);
    List<InquiryH5InquiryStep1OptionDTO> categories =
        List.of(
            new InquiryH5InquiryStep1OptionDTO("REBAR", "螺纹钢"),
            new InquiryH5InquiryStep1OptionDTO("HOT_ROLL", "热轧卷板"),
            new InquiryH5InquiryStep1OptionDTO("MEDIUM_PLATE", "中厚板"),
            new InquiryH5InquiryStep1OptionDTO("COLD_ROLL", "冷轧板卷"));
    List<InquiryH5InquiryStep1OptionDTO> deliveryCities =
        List.of(
            new InquiryH5InquiryStep1OptionDTO("唐山", "唐山"),
            new InquiryH5InquiryStep1OptionDTO("无锡", "无锡"),
            new InquiryH5InquiryStep1OptionDTO("郑州", "郑州"),
            new InquiryH5InquiryStep1OptionDTO("佛山", "佛山"),
            new InquiryH5InquiryStep1OptionDTO(city, city));
    return new InquiryH5InquiryStep1InitResponse(
        city,
        draft.getCategoryCode(),
        draft.getDraftId(),
        uniqueOptions(categories),
        uniqueOptions(deliveryCities),
        "已为您保存草稿，可稍后继续填写");
  }

  public InquiryH5InquiryStep1SaveResponse h5InquiryStep1Save(InquiryH5InquiryStep1SaveRequest request) {
    InquiryH5InquiryStep1DraftEntity draft = repository.saveH5InquiryStep1Draft(request);
    String summary =
        draft.getSpecText()
            + " · "
            + draft.getDeliveryCity()
            + " · "
            + draft.getDemandQtyTon()
            + "吨";
    return new InquiryH5InquiryStep1SaveResponse(
        draft.getDraftId(),
        draft.getStatus(),
        "/h5/inquiry/step2?draftId=" + draft.getDraftId(),
        summary,
        draft.getUpdatedAt().toString());
  }

  public InquiryH5InquiryStep2InitResponse h5InquiryStep2Init(InquiryH5InquiryStep2InitRequest request) {
    InquiryH5InquiryStep1DraftEntity draft = repository.getH5InquiryStep1Draft(request.draftId());
    if (!"STEP1_SAVED".equalsIgnoreCase(draft.getStatus()) && !"SUBMITTED".equalsIgnoreCase(draft.getStatus())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "请先完成Step1");
    }
    List<InquiryH5InquiryStep1OptionDTO> expectedDeliveryOptions =
        List.of(
            new InquiryH5InquiryStep1OptionDTO("当天", "当天"),
            new InquiryH5InquiryStep1OptionDTO("3天内", "3天内"),
            new InquiryH5InquiryStep1OptionDTO("7天内", "7天内"),
            new InquiryH5InquiryStep1OptionDTO("15天内", "15天内"));
    List<InquiryH5InquiryStep1OptionDTO> settleTypeOptions =
        List.of(
            new InquiryH5InquiryStep1OptionDTO("货到付款", "货到付款"),
            new InquiryH5InquiryStep1OptionDTO("月结30天", "月结30天"),
            new InquiryH5InquiryStep1OptionDTO("现款现货", "现款现货"));
    return new InquiryH5InquiryStep2InitResponse(
        draft.getDraftId(),
        draft.getCategoryCode(),
        draft.getSpecText(),
        draft.getDeliveryCity(),
        draft.getDemandQtyTon(),
        draft.getInvoiceNeed(),
        maskPhone(draft.getContactMobile()),
        expectedDeliveryOptions,
        settleTypeOptions,
        "补充交期与履约偏好后即可提交询价");
  }

  public InquiryH5InquiryStep2SubmitResponse h5InquiryStep2Submit(InquiryH5InquiryStep2SubmitRequest request) {
    InquiryH5InquiryStep1DraftEntity draft = repository.submitH5InquiryStep2(request);
    return new InquiryH5InquiryStep2SubmitResponse(
        draft.getDraftId(),
        draft.getInquiryId(),
        draft.getInquiryNo(),
        "/h5/inquiry/step3?draftId=" + draft.getDraftId(),
        "询价提交成功，系统正在为您匹配优质商家",
        draft.getUpdatedAt().toString());
  }

  public InquiryH5InquiryStep3Response h5InquiryStep3(InquiryH5InquiryStep3Request request) {
    InquiryH5InquiryStep1DraftEntity draft = repository.getH5InquiryStep1Draft(request.draftId());
    if (!"SUBMITTED".equalsIgnoreCase(draft.getStatus())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "请先完成Step2提交");
    }
    InquirySuccessResponse success =
        success(new InquirySuccessRequest(draft.getInquiryId(), draft.getContactMobile()));
    return new InquiryH5InquiryStep3Response(
        draft.getDraftId(),
        success.inquiryId(),
        success.inquiryNo(),
        success.inquiryStatus(),
        success.specText(),
        success.deliveryCity(),
        success.demandQtyTon(),
        success.contactMobileMasked(),
        success.createdAt(),
        success.quoteCount(),
        success.nextSteps(),
        success.compareUrl(),
        "H5询价已完成，后续可在报价对比页继续跟进");
  }

  public InquiryH5QuoteCompareResponse h5QuoteCompare(InquiryH5QuoteCompareRequest request) {
    InquiryH5InquiryStep1DraftEntity draft = repository.getH5InquiryStep1Draft(request.draftId());
    if (!"SUBMITTED".equalsIgnoreCase(draft.getStatus())) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "请先完成Step2提交");
    }
    InquiryQuoteCompareResponse response =
        compareQuotes(
            new InquiryQuoteCompareRequest(
                draft.getInquiryId(),
                draft.getContactMobile(),
                request.deliveryCycle(),
                request.invoiceType(),
                request.sortBy(),
                request.page(),
                request.pageSize()));
    String selectedSortBy =
        request.sortBy() == null || request.sortBy().isBlank()
            ? "TOTAL_PRICE"
            : request.sortBy().trim().toUpperCase(java.util.Locale.ROOT);
    return new InquiryH5QuoteCompareResponse(
        draft.getDraftId(),
        response.inquiryId(),
        response.inquiryNo(),
        response.inquiryStatus(),
        response.specText(),
        response.demandQtyTon(),
        response.deliveryCity(),
        maskPhone(draft.getContactMobile()),
        selectedSortBy,
        response.quotes().size(),
        response.quotes(),
        response.total(),
        response.page(),
        response.pageSize(),
        "建议优先比较总价、交付天数与履约评分，再进入成交确认",
        "/inquiry/deal/confirm");
  }

  public InquiryH5MerchantLeadResponse h5MerchantLeads(InquiryH5MerchantLeadRequest request) {
    InquiryMerchantLeadListResponse response =
        merchantLeads(
            new InquiryMerchantLeadListRequest(
                request.merchantId(), request.status(), request.keyword(), request.page(), request.pageSize()));
    return new InquiryH5MerchantLeadResponse(
        request.merchantId().trim(),
        request.status() == null ? "" : request.status().trim().toUpperCase(java.util.Locale.ROOT),
        request.keyword() == null ? "" : request.keyword().trim(),
        response.items(),
        response.total(),
        response.page(),
        response.pageSize(),
        response.pendingQuoteCount(),
        response.quotedCount(),
        response.wonCount(),
        response.lostCount(),
        response.closedCount(),
        "建议优先处理 NEW/CONTACTED 状态线索，提升报价时效");
  }

  public InquiryH5MerchantLeadQuickQuoteResponse h5MerchantLeadQuickQuote(
      String leadId, InquiryH5MerchantLeadQuickQuoteRequest request) {
    return quickQuoteByLead(leadId, request, "h5-merchant-lead");
  }

  public InquiryH5MerchantLeadQuickQuoteResponse h5QuickQuoteSubmit(
      String leadId, InquiryH5MerchantLeadQuickQuoteRequest request) {
    return quickQuoteByLead(leadId, request, "h5-quick-quote");
  }

  private InquiryH5MerchantLeadQuickQuoteResponse quickQuoteByLead(
      String leadId, InquiryH5MerchantLeadQuickQuoteRequest request, String source) {
    InquiryMerchantLeadEntity lead = repository.merchantLeadDetail(leadId, request.merchantId());
    java.math.BigDecimal qty = new java.math.BigDecimal(lead.getDemandQtyTon());
    java.math.BigDecimal unitPrice = new java.math.BigDecimal(request.unitPrice().trim());
    java.math.BigDecimal total = unitPrice.multiply(qty);
    String quoteRemark =
        request.quoteRemark() == null || request.quoteRemark().isBlank()
            ? null
            : request.quoteRemark().trim();
    InquiryMerchantLeadItemDTO updated =
        merchantQuote(
            leadId,
            new InquiryMerchantLeadQuoteRequest(
                request.merchantId().trim(),
                lead.getMerchantName(),
                request.unitPrice().trim(),
                toMoney(total),
                "含税到厂",
                request.deliveryDays().trim(),
                request.paymentTerm() == null || request.paymentTerm().isBlank()
                    ? "月结15天"
                    : request.paymentTerm().trim(),
                "YES",
                quoteRemark,
                source));
    return new InquiryH5MerchantLeadQuickQuoteResponse(
        updated.leadId(),
        updated.status(),
        request.unitPrice().trim(),
        toMoney(total),
        request.deliveryDays().trim(),
        "快捷报价已提交");
  }

  public InquiryH5MerchantLeadQuickStatusResponse h5MerchantLeadQuickStatus(
      String leadId, InquiryH5MerchantLeadQuickStatusRequest request) {
    InquiryMerchantLeadItemDTO updated =
        merchantUpdateStatus(
            leadId,
            new InquiryMerchantLeadStatusUpdateRequest(
                request.merchantId().trim(),
                request.status().trim(),
                "h5-merchant-lead",
                request.comment()));
    return new InquiryH5MerchantLeadQuickStatusResponse(
        updated.leadId(),
        updated.status(),
        updated.latestFollow(),
        "线索状态已更新");
  }

  public InquiryH5QuickQuoteInitResponse h5QuickQuoteInit(InquiryH5QuickQuoteInitRequest request) {
    InquiryMerchantLeadEntity lead = repository.merchantLeadDetail(request.leadId(), request.merchantId());
    java.math.BigDecimal suggestedPrice = new java.math.BigDecimal("3500");
    if ("Q235B 3.0*1500*C".equalsIgnoreCase(lead.getSpecText())) {
      suggestedPrice = new java.math.BigDecimal("3490");
    }
    if ("中厚板".equalsIgnoreCase(lead.getSpecText())) {
      suggestedPrice = new java.math.BigDecimal("3890");
    }
    java.math.BigDecimal unitPrice =
        (lead.getUnitPrice() == null || lead.getUnitPrice().isBlank())
            ? suggestedPrice
            : new java.math.BigDecimal(lead.getUnitPrice());
    String suggestedDeliveryDays =
        lead.getDeliveryDays() == null || lead.getDeliveryDays().isBlank()
            ? "1"
            : lead.getDeliveryDays();
    String paymentTerm =
        lead.getPaymentTerm() == null || lead.getPaymentTerm().isBlank() ? "月结15天" : lead.getPaymentTerm();
    return new InquiryH5QuickQuoteInitResponse(
        request.merchantId().trim(),
        lead.getId(),
        lead.getInquiryId(),
        lead.getInquiryNo(),
        lead.getSpecText(),
        lead.getDemandQtyTon(),
        lead.getDeliveryCity(),
        lead.getInvoiceNeed(),
        lead.getStatus().name(),
        unitPrice.stripTrailingZeros().toPlainString(),
        suggestedDeliveryDays,
        paymentTerm,
        "建议在10分钟内完成报价并电话回访，提升线索转化");
  }

  public InquiryH5PickupOrderCreateResponse h5CreatePickupOrder(InquiryH5PickupOrderCreateRequest request) {
    InquiryPickupOrderCreateResponse created =
        createPickupOrder(
            new InquiryPickupOrderCreateRequest(
                request.inquiryId().trim(),
                request.quoteId().trim(),
                request.contactMobile().trim(),
                request.buyerCompany(),
                request.buyerContact(),
                request.pickupSite().trim(),
                request.pickupDate().trim(),
                request.pickupVehicleNo().trim(),
                request.pickupDriverName().trim(),
                request.pickupDriverPhone().trim(),
                request.agreedProtocol(),
                request.remark()));
    return new InquiryH5PickupOrderCreateResponse(
        created.pickupId(),
        created.pickupNo(),
        created.inquiryId(),
        created.quoteId(),
        created.status(),
        pickupStatusText(InquiryPickupOrderStatus.valueOf(created.status())),
        "/h5/pickup-orders?contactMobile=" + request.contactMobile().trim(),
        "H5提货单创建成功，等待卖方确认放货");
  }

  public InquiryH5PickupOrderListResponse h5PickupOrders(InquiryH5PickupOrderListRequest request) {
    InquiryPickupOrderListResponse list =
        listPickupOrders(
            new InquiryPickupOrderListRequest(
                request.contactMobile().trim(),
                request.status(),
                request.keyword(),
                request.page(),
                request.pageSize()));
    String selectedStatus =
        request.status() == null ? "" : request.status().trim().toUpperCase(java.util.Locale.ROOT);
    String selectedKeyword = request.keyword() == null ? "" : request.keyword().trim();
    return new InquiryH5PickupOrderListResponse(
        maskPhone(request.contactMobile().trim()),
        selectedStatus,
        selectedKeyword,
        list.items(),
        list.total(),
        list.page(),
        list.pageSize(),
        list.createdCount(),
        list.confirmedCount(),
        list.inTransitCount(),
        list.signedCount(),
        list.completedCount(),
        list.cancelledCount(),
        "建议优先推进 CONFIRMED/IN_TRANSIT 单据，确保按时签收");
  }

  public InquiryH5PickupOrderDetailResponse h5PickupOrderDetail(
      String pickupOrderId, InquiryH5PickupOrderDetailRequest request) {
    InquiryPickupOrderDetailResponse detail =
        pickupOrderDetail(
            pickupOrderId, new InquiryPickupOrderListRequest(request.contactMobile().trim(), "", "", 1, 10));
    return new InquiryH5PickupOrderDetailResponse(
        detail.order(),
        detail.pickupAddress(),
        detail.contactName(),
        detail.contactPhone(),
        detail.vehicleNo(),
        detail.driverName(),
        detail.driverPhone(),
        detail.latestRemark(),
        "履约建议：提货前复核车牌与司机信息，签收后及时回传回单");
  }

  public InquiryH5PickupOrderQuickStatusResponse h5PickupOrderQuickStatus(
      String pickupOrderId, InquiryH5PickupOrderQuickStatusRequest request) {
    InquiryPickupOrderDetailResponse updated =
        pickupOrderUpdateStatus(
            pickupOrderId,
            new InquiryPickupOrderStatusUpdateRequest(
                request.contactMobile().trim(),
                request.status().trim(),
                "h5-pickup",
                request.remark()));
    return new InquiryH5PickupOrderQuickStatusResponse(
        updated.order().pickupId(),
        updated.order().status(),
        updated.order().statusText(),
        "提货单状态已更新");
  }

  public InquiryH5ReconcileOrderCreateResponse h5CreateReconcileOrder(
      InquiryH5ReconcileOrderCreateRequest request) {
    InquiryReconcileOrderCreateResponse created =
        createReconcileOrder(
            new InquiryReconcileOrderCreateRequest(
                request.pickupOrderId().trim(),
                request.contactMobile().trim(),
                request.statementMonth().trim(),
                "MONTHLY",
                request.dueDate().trim(),
                request.invoiceTitle(),
                request.remark()));
    return new InquiryH5ReconcileOrderCreateResponse(
        created.reconcileId(),
        created.reconcileNo(),
        created.pickupId(),
        created.status(),
        reconcileStatusText(InquiryReconcileOrderStatus.valueOf(created.status())),
        "/h5/reconcile-orders?contactMobile=" + request.contactMobile().trim(),
        "H5对账单创建成功，等待回款跟进");
  }

  public InquiryH5ReconcileOrderListResponse h5ReconcileOrders(InquiryH5ReconcileOrderListRequest request) {
    InquiryReconcileOrderListResponse list =
        listReconcileOrders(
            new InquiryReconcileOrderListRequest(
                request.contactMobile().trim(),
                request.status(),
                request.keyword(),
                request.page(),
                request.pageSize()));
    String selectedStatus =
        request.status() == null ? "" : request.status().trim().toUpperCase(java.util.Locale.ROOT);
    String selectedKeyword = request.keyword() == null ? "" : request.keyword().trim();
    return new InquiryH5ReconcileOrderListResponse(
        maskPhone(request.contactMobile().trim()),
        selectedStatus,
        selectedKeyword,
        list.items(),
        list.total(),
        list.page(),
        list.pageSize(),
        list.createdCount(),
        list.invoicePendingCount(),
        list.invoicedCount(),
        list.confirmedCount(),
        list.partialPaidCount(),
        list.paidCount(),
        list.closedCount(),
        list.disputedCount(),
        "建议每日跟进 PARTIAL_PAID 与 DISPUTED 单据，保障回款效率");
  }

  public InquiryH5ReconcileOrderDetailResponse h5ReconcileOrderDetail(
      String reconcileOrderId, InquiryH5ReconcileOrderDetailRequest request) {
    InquiryReconcileOrderDetailResponse detail =
        reconcileOrderDetail(
            reconcileOrderId,
            new InquiryReconcileOrderListRequest(request.contactMobile().trim(), "", "", 1, 10));
    return new InquiryH5ReconcileOrderDetailResponse(
        detail.order(),
        detail.latestRemark(),
        detail.taxAmount(),
        detail.payableAmount(),
        detail.paidAmount(),
        detail.unpaidAmount(),
        detail.paymentDeadline(),
        detail.voucherStatus(),
        "回款建议：核销后及时更新状态并归档凭证，减少争议单积压");
  }

  public InquiryH5ReconcileOrderQuickStatusResponse h5ReconcileOrderQuickStatus(
      String reconcileOrderId, InquiryH5ReconcileOrderQuickStatusRequest request) {
    InquiryReconcileOrderDetailResponse updated =
        reconcileOrderUpdateStatus(
            reconcileOrderId,
            new InquiryReconcileOrderStatusUpdateRequest(
                request.contactMobile().trim(),
                request.status().trim(),
                request.paidAmount(),
                "h5-reconcile",
                request.remark()));
    return new InquiryH5ReconcileOrderQuickStatusResponse(
        updated.order().reconcileId(),
        updated.order().status(),
        updated.order().statusText(),
        "对账单状态已更新");
  }

  public InquiryH5MemberPlanListResponse h5MemberPlans(InquiryH5MemberPlanListRequest request) {
    InquirySubscriptionPlanListResponse plans =
        subscriptionPlans(new InquirySubscriptionPlanListRequest(request.merchantId().trim()));
    return new InquiryH5MemberPlanListResponse(
        request.merchantId().trim(),
        plans.plans(),
        plans.recommendPlanCode(),
        "优先选择推荐套餐，提升线索处理效率与履约协同能力");
  }

  public InquiryH5MemberCreateResponse h5MemberOpen(InquiryH5MemberCreateRequest request) {
    InquirySubscriptionCreateResponse created =
        createSubscription(
            new InquirySubscriptionCreateRequest(
                request.merchantId().trim(),
                request.planCode().trim(),
                request.billingCycle().trim(),
                request.operator()));
    return new InquiryH5MemberCreateResponse(
        created.subscriptionId(),
        created.subscriptionNo(),
        created.merchantId(),
        created.planCode(),
        created.planName(),
        created.status(),
        subscriptionStatusText(created.status()),
        created.payAmount(),
        "/h5/member?merchantId=" + request.merchantId().trim(),
        "H5会员开通成功，权益已生效");
  }

  public InquiryH5MemberMineResponse h5MemberMine(InquiryH5MemberMineRequest request) {
    InquirySubscriptionMineResponse mine =
        subscriptionMine(new InquirySubscriptionMineRequest(request.merchantId().trim()));
    return new InquiryH5MemberMineResponse(
        request.merchantId().trim(),
        mine.items(),
        mine.total(),
        mine.activeCount(),
        mine.expiringSoonCount(),
        mine.expiredCount(),
        "建议在到期前7天续费，避免功能中断");
  }

  public InquiryH5MemberOverviewResponse h5MemberOverview(InquiryH5MemberOverviewRequest request) {
    InquirySubscriptionMineResponse mine =
        subscriptionMine(new InquirySubscriptionMineRequest(request.merchantId().trim()));
    InquirySubscriptionMineItemDTO current = mine.items().isEmpty() ? null : mine.items().get(0);
    String level = current == null ? "FREE" : planTierByCode(current.planCode());
    String levelText =
        switch (level) {
          case "ENTERPRISE" -> "企业会员";
          case "PRO" -> "专业会员";
          case "BASIC" -> "基础会员";
          default -> "免费用户";
        };
    String renewSuggestion =
        mine.expiringSoonCount() > 0
            ? "存在临近到期会员，建议尽快续费保障权益连续"
            : "会员状态稳定，可持续观察权益使用与回款转化";
    return new InquiryH5MemberOverviewResponse(
        request.merchantId().trim(),
        level,
        levelText,
        current == null ? "" : current.planCode(),
        current == null ? "" : current.planName(),
        current == null ? "NONE" : current.status(),
        current == null ? "未开通" : current.statusText(),
        mine.activeCount(),
        mine.expiringSoonCount(),
        mine.expiredCount(),
        renewSuggestion,
        "会员中心可统一管理套餐权益、续费节奏与经营成本");
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

  private InquirySubscriptionPlanItemDTO toSubscriptionPlanItem(InquirySubscriptionPlanEntity entity) {
    return new InquirySubscriptionPlanItemDTO(
        entity.getPlanId(),
        entity.getPlanCode(),
        entity.getPlanName(),
        entity.getSuitableFor(),
        entity.getBillingCycle(),
        entity.getOriginalPrice(),
        entity.getPrice(),
        entity.isRecommended(),
        entity.getSuitableFor(),
        entity.getFeatures().stream()
            .map(
                feature ->
                    new InquirySubscriptionPlanFeatureDTO(
                        feature.key(),
                        feature.label(),
                        feature.value(),
                        feature.highlight()))
            .toList());
  }

  private InquirySubscriptionMineItemDTO toSubscriptionMineItem(InquiryMerchantSubscriptionEntity entity) {
    String planTier = planTierByCode(entity.getPlanCode());
    String statusText = subscriptionStatusText(entity.getStatus());
    return new InquirySubscriptionMineItemDTO(
        entity.getSubscriptionId(),
        entity.getMerchantId(),
        entity.getMerchantName(),
        entity.getPlanCode(),
        entity.getPlanName(),
        planTier,
        entity.getStatus(),
        statusText,
        entity.getBillingCycle(),
        entity.getStartAt(),
        entity.getEndAt(),
        entity.getAutoRenew(),
        entity.getAmountYuan(),
        "CNY",
        entity.getEntitlements(),
        entity.getCreatedAt().toString(),
        entity.getUpdatedAt().toString());
  }

  private InquiryBillingOrderItemDTO toBillingItem(InquiryBillingOrderEntity entity) {
    return new InquiryBillingOrderItemDTO(
        entity.getBillId(),
        entity.getBillNo(),
        entity.getMerchantId(),
        entity.getSubscriptionId(),
        entity.getSubscriptionNo(),
        entity.getPlanCode(),
        entity.getPlanName(),
        entity.getPeriodStart(),
        entity.getPeriodEnd(),
        entity.getIssueDate(),
        entity.getDueDate(),
        entity.getAmountYuan(),
        entity.getPaidAmountYuan(),
        entity.getUnpaidAmountYuan(),
        entity.getStatus(),
        billingStatusText(entity.getStatus()),
        "BANK_TRANSFER",
        "ISSUED",
        entity.getCreatedAt().toString(),
        entity.getUpdatedAt().toString());
  }

  private InquiryMessageCenterItemDTO toMessageItem(InquiryMessageCenterEntity entity) {
    String messageType = messageTypeFromBizType(entity.getBizType());
    String messageTypeText = messageTypeText(messageType);
    String status = entity.getReadStatus();
    boolean unread = !"READ".equalsIgnoreCase(status);
    List<String> tags = List.of(entity.getPriority(), messageTypeText);
    return new InquiryMessageCenterItemDTO(
        entity.getMessageId(),
        entity.getMerchantId(),
        entity.getTitle(),
        entity.getContent(),
        messageType,
        messageTypeText,
        entity.getBizType(),
        entity.getBizId(),
        status,
        unread ? "未读" : "已读",
        unread,
        "P1".equalsIgnoreCase(entity.getPriority()),
        priorityScore(entity.getPriority()),
        tags,
        "查看详情",
        entity.getActionUrl(),
        entity.getSendAt().toString(),
        entity.getReadAt() == null ? "" : entity.getReadAt().toString());
  }

  private String messageTypeFromBizType(String bizType) {
    if (bizType == null || bizType.isBlank()) {
      return "SYSTEM";
    }
    String normalized = bizType.trim().toUpperCase(java.util.Locale.ROOT);
    return switch (normalized) {
      case "INQUIRY", "RECONCILE", "SUBSCRIPTION" -> "TRANSACTION";
      case "RISK" -> "RISK";
      default -> "SYSTEM";
    };
  }

  private String messageTypeText(String type) {
    if (type == null || type.isBlank()) {
      return "系统";
    }
    return switch (type.trim().toUpperCase(java.util.Locale.ROOT)) {
      case "TRANSACTION" -> "交易";
      case "RISK" -> "风控";
      default -> "系统";
    };
  }

  private int priorityScore(String priority) {
    if (priority == null || priority.isBlank()) {
      return 50;
    }
    String normalized = priority.trim().toUpperCase(java.util.Locale.ROOT);
    return switch (normalized) {
      case "P1" -> 100;
      case "P2" -> 80;
      case "P3" -> 60;
      default -> 50;
    };
  }

  private String subscriptionStatusText(String status) {
    if (status == null || status.isBlank()) {
      return "未知";
    }
    String normalized = status.trim().toUpperCase(java.util.Locale.ROOT);
    return switch (normalized) {
      case "ACTIVE" -> "生效中";
      case "EXPIRING_SOON" -> "即将到期";
      case "EXPIRED" -> "已过期";
      case "CANCELLED" -> "已取消";
      default -> normalized;
    };
  }

  private String planTierByCode(String planCode) {
    if (planCode == null || planCode.isBlank()) {
      return "STANDARD";
    }
    if (planCode.contains("ENTERPRISE")) {
      return "ENTERPRISE";
    }
    if (planCode.contains("PRO")) {
      return "PRO";
    }
    return "STANDARD";
  }

  private String billingStatusText(String status) {
    if (status == null || status.isBlank()) {
      return "未知";
    }
    String normalized = status.trim().toUpperCase(java.util.Locale.ROOT);
    return switch (normalized) {
      case "UNPAID" -> "待支付";
      case "PARTIAL_PAID" -> "部分支付";
      case "PAID" -> "已支付";
      case "OVERDUE" -> "已逾期";
      default -> normalized;
    };
  }

  private int parseScoreImpact(String scoreChange) {
    if (scoreChange == null || scoreChange.isBlank()) {
      return 0;
    }
    String normalized = scoreChange.replace("+", "").trim();
    try {
      return Integer.parseInt(normalized);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  private String toMoney(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
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

  private List<InquiryH5InquiryStep1OptionDTO> uniqueOptions(List<InquiryH5InquiryStep1OptionDTO> options) {
    java.util.LinkedHashMap<String, InquiryH5InquiryStep1OptionDTO> dedup = new java.util.LinkedHashMap<>();
    for (InquiryH5InquiryStep1OptionDTO option : options) {
      if (option == null || option.code() == null || option.code().isBlank()) {
        continue;
      }
      dedup.putIfAbsent(option.code(), option);
    }
    return List.copyOf(dedup.values());
  }
}

