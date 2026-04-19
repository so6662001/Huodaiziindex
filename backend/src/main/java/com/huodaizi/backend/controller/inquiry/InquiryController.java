package com.huodaizi.backend.controller.inquiry;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmPreviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmPreviewResponse;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmSubmitRequest;
import com.huodaizi.backend.dto.inquiry.InquiryDealConfirmSubmitResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadDetailResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadItemDTO;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantCreditScoreResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchBatchUpdateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchOverviewRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchOverviewResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteWorkbenchTaskRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessResponse;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionMineResponse;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanListRequest;
import com.huodaizi.backend.dto.inquiry.InquirySubscriptionPlanListResponse;
import com.huodaizi.backend.service.inquiry.InquiryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inquiries")
public class InquiryController {

  private final InquiryService service;

  public InquiryController(InquiryService service) {
    this.service = service;
  }

  @PostMapping
  public ApiResponse<InquiryCreateResponse> create(@Valid @RequestBody InquiryCreateRequest request) {
    return ApiResponse.success(service.create(request));
  }

  @GetMapping
  public ApiResponse<InquiryListResponse> list(@Valid @ModelAttribute InquiryListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/compare")
  public ApiResponse<InquiryQuoteCompareResponse> compare(@Valid @ModelAttribute InquiryQuoteCompareRequest request) {
    return ApiResponse.success(service.compareQuotes(request));
  }

  @GetMapping("/deal/preview")
  public ApiResponse<InquiryDealConfirmPreviewResponse> dealPreview(
      @Valid @ModelAttribute InquiryDealConfirmPreviewRequest request) {
    return ApiResponse.success(service.dealConfirmPreview(request));
  }

  @PostMapping("/deal/confirm")
  public ApiResponse<InquiryDealConfirmSubmitResponse> dealConfirm(
      @Valid @RequestBody InquiryDealConfirmSubmitRequest request) {
    return ApiResponse.success(service.dealConfirmSubmit(request));
  }

  @PostMapping("/pickup-orders")
  public ApiResponse<InquiryPickupOrderCreateResponse> createPickupOrder(
      @Valid @RequestBody InquiryPickupOrderCreateRequest request) {
    return ApiResponse.success(service.createPickupOrder(request));
  }

  @GetMapping("/pickup-orders")
  public ApiResponse<InquiryPickupOrderListResponse> listPickupOrders(
      @Valid @ModelAttribute InquiryPickupOrderListRequest request) {
    return ApiResponse.success(service.listPickupOrders(request));
  }

  @GetMapping("/pickup-orders/{pickupOrderId}")
  public ApiResponse<InquiryPickupOrderDetailResponse> pickupOrderDetail(
      @PathVariable("pickupOrderId") String pickupOrderId,
      @Valid @ModelAttribute InquiryPickupOrderListRequest request) {
    return ApiResponse.success(service.pickupOrderDetail(pickupOrderId, request));
  }

  @PutMapping("/pickup-orders/{pickupOrderId}/status")
  public ApiResponse<InquiryPickupOrderDetailResponse> pickupOrderUpdateStatus(
      @PathVariable("pickupOrderId") String pickupOrderId,
      @Valid @RequestBody InquiryPickupOrderStatusUpdateRequest request) {
    return ApiResponse.success(service.pickupOrderUpdateStatus(pickupOrderId, request));
  }

  @PostMapping("/reconcile-orders")
  public ApiResponse<InquiryReconcileOrderCreateResponse> createReconcileOrder(
      @Valid @RequestBody InquiryReconcileOrderCreateRequest request) {
    return ApiResponse.success(service.createReconcileOrder(request));
  }

  @GetMapping("/reconcile-orders")
  public ApiResponse<InquiryReconcileOrderListResponse> listReconcileOrders(
      @Valid @ModelAttribute InquiryReconcileOrderListRequest request) {
    return ApiResponse.success(service.listReconcileOrders(request));
  }

  @GetMapping("/reconcile-orders/{reconcileOrderId}")
  public ApiResponse<InquiryReconcileOrderDetailResponse> reconcileOrderDetail(
      @PathVariable("reconcileOrderId") String reconcileOrderId,
      @Valid @ModelAttribute InquiryReconcileOrderListRequest request) {
    return ApiResponse.success(service.reconcileOrderDetail(reconcileOrderId, request));
  }

  @PutMapping("/reconcile-orders/{reconcileOrderId}/status")
  public ApiResponse<InquiryReconcileOrderDetailResponse> reconcileOrderUpdateStatus(
      @PathVariable("reconcileOrderId") String reconcileOrderId,
      @Valid @RequestBody InquiryReconcileOrderStatusUpdateRequest request) {
    return ApiResponse.success(service.reconcileOrderUpdateStatus(reconcileOrderId, request));
  }

  @GetMapping("/success")
  public ApiResponse<InquirySuccessResponse> success(@Valid @ModelAttribute InquirySuccessRequest request) {
    return ApiResponse.success(service.success(request));
  }

  @GetMapping("/merchant/leads")
  public ApiResponse<InquiryMerchantLeadListResponse> merchantLeads(
      @Valid @ModelAttribute InquiryMerchantLeadListRequest request) {
    return ApiResponse.success(service.merchantLeads(request));
  }

  @GetMapping("/merchant/credit-score")
  public ApiResponse<InquiryMerchantCreditScoreResponse> merchantCreditScore(
      @Valid @ModelAttribute InquiryMerchantCreditScoreRequest request) {
    return ApiResponse.success(service.merchantCreditScore(request));
  }

  @GetMapping("/merchant/subscription/plans")
  public ApiResponse<InquirySubscriptionPlanListResponse> subscriptionPlans(
      @Valid @ModelAttribute InquirySubscriptionPlanListRequest request) {
    return ApiResponse.success(service.subscriptionPlans(request));
  }

  @PostMapping("/merchant/subscription")
  public ApiResponse<InquirySubscriptionCreateResponse> createSubscription(
      @Valid @RequestBody InquirySubscriptionCreateRequest request) {
    return ApiResponse.success(service.createSubscription(request));
  }

  @GetMapping("/merchant/subscription/mine")
  public ApiResponse<InquirySubscriptionMineResponse> mySubscriptions(
      @Valid @ModelAttribute InquirySubscriptionMineRequest request) {
    return ApiResponse.success(service.subscriptionMine(request));
  }

  @GetMapping("/merchant/leads/{leadId}")
  public ApiResponse<InquiryMerchantLeadDetailResponse> merchantLeadDetail(
      @PathVariable("leadId") String leadId, @Valid @ModelAttribute InquiryMerchantLeadListRequest request) {
    return ApiResponse.success(service.merchantLeadDetail(leadId, request));
  }

  @PostMapping("/merchant/leads/{leadId}/quote")
  public ApiResponse<InquiryMerchantLeadItemDTO> merchantQuote(
      @PathVariable("leadId") String leadId, @Valid @RequestBody InquiryMerchantLeadQuoteRequest request) {
    return ApiResponse.success(service.merchantQuote(leadId, request));
  }

  @PutMapping("/merchant/leads/{leadId}/status")
  public ApiResponse<InquiryMerchantLeadItemDTO> merchantUpdateStatus(
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody InquiryMerchantLeadStatusUpdateRequest request) {
    return ApiResponse.success(service.merchantUpdateStatus(leadId, request));
  }

  @GetMapping("/merchant/workbench/overview")
  public ApiResponse<InquiryQuoteWorkbenchOverviewResponse> quoteWorkbenchOverview(
      @Valid @ModelAttribute InquiryQuoteWorkbenchOverviewRequest request) {
    return ApiResponse.success(service.quoteWorkbenchOverview(request));
  }

  @GetMapping("/merchant/workbench/tasks")
  public ApiResponse<InquiryQuoteWorkbenchTaskListResponse> quoteWorkbenchTasks(
      @Valid @ModelAttribute InquiryQuoteWorkbenchTaskRequest request) {
    return ApiResponse.success(service.quoteWorkbenchTasks(request));
  }

  @PutMapping("/merchant/workbench/tasks/status")
  public ApiResponse<InquiryQuoteWorkbenchTaskListResponse> quoteWorkbenchBatchUpdateStatus(
      @Valid @RequestBody InquiryQuoteWorkbenchBatchUpdateRequest request) {
    return ApiResponse.success(service.quoteWorkbenchBatchUpdate(request));
  }
}
