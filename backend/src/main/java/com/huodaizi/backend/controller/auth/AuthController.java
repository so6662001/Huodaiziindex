package com.huodaizi.backend.controller.auth;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.dto.auth.H5N01QuickLoginRequest;
import com.huodaizi.backend.dto.auth.H5N01QuickLoginResponse;
import com.huodaizi.backend.dto.auth.H5N01SendLoginCodeRequest;
import com.huodaizi.backend.dto.auth.H5N01SendLoginCodeResponse;
import com.huodaizi.backend.dto.auth.H5N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.H5N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.dto.auth.N04OnboardingProgressResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.dto.auth.N05NegotiationDetailResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationSendMessageRequest;
import com.huodaizi.backend.dto.auth.N05NegotiationSessionListResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.N06OrderActionRequest;
import com.huodaizi.backend.dto.auth.N06OrderDetailResponse;
import com.huodaizi.backend.dto.auth.N06OrderListResponse;
import com.huodaizi.backend.dto.auth.N07TradeTermsConfirmRequest;
import com.huodaizi.backend.dto.auth.N07TradeTermsDetailResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeCreateRequest;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeDetailResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeListResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressDetailResponse;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressListResponse;
import com.huodaizi.backend.dto.auth.N10CashierOrderDetailResponse;
import com.huodaizi.backend.dto.auth.N10CashierOrderListResponse;
import com.huodaizi.backend.dto.auth.N10CashierPayRequest;
import com.huodaizi.backend.dto.auth.N11PaymentResultResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationCreateRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationDetailResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationListResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleDTO;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleListResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleSetDefaultRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleSetStatusRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleUpsertRequest;
import com.huodaizi.backend.dto.auth.N13CreditScoreDetailResponse;
import com.huodaizi.backend.dto.auth.N13CreditScoreListResponse;
import com.huodaizi.backend.dto.auth.N14DispatchAppealCreateRequest;
import com.huodaizi.backend.dto.auth.N14DispatchAppealDetailResponse;
import com.huodaizi.backend.dto.auth.N14DispatchAppealListResponse;
import com.huodaizi.backend.dto.auth.N14DispatchAppealStatusUpdateRequest;
import com.huodaizi.backend.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final String AUTH_HEADER = "X-Auth-Token";
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  public ApiResponse<AuthRegisterResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
    return ApiResponse.success(service.register(request));
  }

  @PostMapping("/login")
  public ApiResponse<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
    return ApiResponse.success(service.login(request));
  }

  @PostMapping("/h5/send-login-code")
  public ApiResponse<H5N01SendLoginCodeResponse> h5SendLoginCode(
      @Valid @RequestBody H5N01SendLoginCodeRequest request) {
    return ApiResponse.success(service.sendH5LoginCode(request));
  }

  @PostMapping("/h5/quick-login")
  public ApiResponse<H5N01QuickLoginResponse> h5QuickLogin(
      @Valid @RequestBody H5N01QuickLoginRequest request) {
    return ApiResponse.success(service.h5QuickLogin(request));
  }

  @GetMapping("/h5/enterprise-certification/detail")
  public ApiResponse<H5N03EnterpriseCertificationDetailResponse> h5EnterpriseCertificationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.h5EnterpriseCertificationDetail(token));
  }

  @PostMapping("/h5/enterprise-certification/submit")
  public ApiResponse<H5N03EnterpriseCertificationDetailResponse> h5SubmitEnterpriseCertification(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody H5N03EnterpriseCertificationSubmitRequest request) {
    return ApiResponse.success(service.submitH5EnterpriseCertification(token, request));
  }

  @GetMapping("/session")
  public ApiResponse<AuthSessionResponse> session(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.session(token));
  }

  @GetMapping("/enterprise-certification/detail")
  public ApiResponse<N03EnterpriseCertificationDetailResponse> enterpriseCertificationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.enterpriseCertificationDetail(token));
  }

  @PostMapping("/enterprise-certification/submit")
  public ApiResponse<N03EnterpriseCertificationDetailResponse> submitEnterpriseCertification(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N03EnterpriseCertificationSubmitRequest request) {
    return ApiResponse.success(service.submitEnterpriseCertification(token, request));
  }

  @GetMapping("/onboarding/progress")
  public ApiResponse<N04OnboardingProgressResponse> onboardingProgress(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.onboardingProgress(token));
  }

  @GetMapping("/negotiations")
  public ApiResponse<N05NegotiationSessionListResponse> negotiationSessions(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.negotiationSessions(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/negotiations/{sessionId}")
  public ApiResponse<N05NegotiationDetailResponse> negotiationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId) {
    return ApiResponse.success(service.negotiationDetail(token, sessionId));
  }

  @PostMapping("/negotiations/{sessionId}/messages")
  public ApiResponse<N05NegotiationDetailResponse> negotiationMessage(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId,
      @Valid @RequestBody N05NegotiationSendMessageRequest request) {
    return ApiResponse.success(service.sendNegotiationMessage(token, sessionId, request));
  }

  @PostMapping("/negotiations/{sessionId}/status")
  public ApiResponse<N05NegotiationDetailResponse> negotiationStatus(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId,
      @Valid @RequestBody N05NegotiationStatusUpdateRequest request) {
    return ApiResponse.success(service.updateNegotiationStatus(token, sessionId, request));
  }

  @GetMapping("/orders")
  public ApiResponse<N06OrderListResponse> orderList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.orderList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/orders/{orderId}")
  public ApiResponse<N06OrderDetailResponse> orderDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("orderId") String orderId) {
    return ApiResponse.success(service.orderDetail(token, orderId));
  }

  @PostMapping("/orders/{orderId}/action")
  public ApiResponse<N06OrderDetailResponse> orderAction(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("orderId") String orderId,
      @Valid @RequestBody N06OrderActionRequest request) {
    return ApiResponse.success(service.orderAction(token, orderId, request));
  }

  @GetMapping("/orders/{orderId}/trade-terms")
  public ApiResponse<N07TradeTermsDetailResponse> tradeTermsDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("orderId") String orderId) {
    return ApiResponse.success(service.tradeTermsDetail(token, orderId));
  }

  @PostMapping("/orders/{orderId}/trade-terms/confirm")
  public ApiResponse<N07TradeTermsDetailResponse> confirmTradeTerms(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("orderId") String orderId,
      @Valid @RequestBody N07TradeTermsConfirmRequest request) {
    return ApiResponse.success(service.confirmTradeTerms(token, orderId, request));
  }

  @GetMapping("/after-sales/disputes")
  public ApiResponse<N08AfterSaleDisputeListResponse> afterSaleDisputeList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.afterSaleDisputeList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/after-sales/disputes/{disputeId}")
  public ApiResponse<N08AfterSaleDisputeDetailResponse> afterSaleDisputeDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("disputeId") String disputeId) {
    return ApiResponse.success(service.afterSaleDisputeDetail(token, disputeId));
  }

  @PostMapping("/after-sales/disputes")
  public ApiResponse<N08AfterSaleDisputeDetailResponse> createAfterSaleDispute(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N08AfterSaleDisputeCreateRequest request) {
    return ApiResponse.success(service.createAfterSaleDispute(token, request));
  }

  @PostMapping("/after-sales/disputes/{disputeId}/status")
  public ApiResponse<N08AfterSaleDisputeDetailResponse> afterSaleDisputeStatus(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("disputeId") String disputeId,
      @Valid @RequestBody N08AfterSaleDisputeStatusUpdateRequest request) {
    return ApiResponse.success(service.updateAfterSaleDisputeStatus(token, disputeId, request));
  }

  @GetMapping("/after-sales/progress")
  public ApiResponse<N09AfterSaleProgressListResponse> afterSaleProgressList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.afterSaleProgressList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/after-sales/progress/{disputeId}")
  public ApiResponse<N09AfterSaleProgressDetailResponse> afterSaleProgressDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("disputeId") String disputeId) {
    return ApiResponse.success(service.afterSaleProgressDetail(token, disputeId));
  }

  @GetMapping("/cashier/orders")
  public ApiResponse<N10CashierOrderListResponse> cashierOrderList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.cashierOrderList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/cashier/orders/{cashierOrderId}")
  public ApiResponse<N10CashierOrderDetailResponse> cashierOrderDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("cashierOrderId") String cashierOrderId) {
    return ApiResponse.success(service.cashierOrderDetail(token, cashierOrderId));
  }

  @PostMapping("/cashier/orders/{cashierOrderId}/pay")
  public ApiResponse<N10CashierOrderDetailResponse> payCashierOrder(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("cashierOrderId") String cashierOrderId,
      @Valid @RequestBody N10CashierPayRequest request) {
    return ApiResponse.success(service.payCashierOrder(token, cashierOrderId, request));
  }

  @GetMapping("/cashier/orders/{cashierOrderId}/result")
  public ApiResponse<N11PaymentResultResponse> paymentResult(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("cashierOrderId") String cashierOrderId) {
    return ApiResponse.success(service.paymentResult(token, cashierOrderId));
  }

  @GetMapping("/invoices/titles")
  public ApiResponse<N12InvoiceTitleListResponse> invoiceTitleList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.invoiceTitleList(token));
  }

  @PostMapping("/invoices/titles")
  public ApiResponse<N12InvoiceTitleDTO> upsertInvoiceTitle(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N12InvoiceTitleUpsertRequest request) {
    return ApiResponse.success(service.upsertInvoiceTitle(token, request));
  }

  @PostMapping("/invoices/titles/{titleId}/default")
  public ApiResponse<N12InvoiceTitleDTO> setDefaultInvoiceTitle(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("titleId") String titleId,
      @Valid @RequestBody N12InvoiceTitleSetDefaultRequest request) {
    return ApiResponse.success(service.setDefaultInvoiceTitle(token, titleId, request));
  }

  @PostMapping("/invoices/titles/{titleId}/status")
  public ApiResponse<N12InvoiceTitleDTO> setInvoiceTitleStatus(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("titleId") String titleId,
      @Valid @RequestBody N12InvoiceTitleSetStatusRequest request) {
    return ApiResponse.success(service.setInvoiceTitleStatus(token, titleId, request));
  }

  @GetMapping("/invoices/applications")
  public ApiResponse<N12InvoiceApplicationListResponse> invoiceApplicationList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.invoiceApplicationList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/invoices/applications/{applicationId}")
  public ApiResponse<N12InvoiceApplicationDetailResponse> invoiceApplicationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("applicationId") String applicationId) {
    return ApiResponse.success(service.invoiceApplicationDetail(token, applicationId));
  }

  @PostMapping("/invoices/applications")
  public ApiResponse<N12InvoiceApplicationDetailResponse> createInvoiceApplication(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N12InvoiceApplicationCreateRequest request) {
    return ApiResponse.success(service.createInvoiceApplication(token, request));
  }

  @GetMapping("/credit-scores")
  public ApiResponse<N13CreditScoreListResponse> creditScoreList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "grade", required = false) String grade,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.creditScoreList(token, grade, keyword, pageNo, pageSize));
  }

  @GetMapping("/credit-scores/{scoreId}")
  public ApiResponse<N13CreditScoreDetailResponse> creditScoreDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("scoreId") String scoreId) {
    return ApiResponse.success(service.creditScoreDetail(token, scoreId));
  }

  @GetMapping("/dispatch-appeals")
  public ApiResponse<N14DispatchAppealListResponse> dispatchAppealList(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.dispatchAppealList(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/dispatch-appeals/{appealId}")
  public ApiResponse<N14DispatchAppealDetailResponse> dispatchAppealDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("appealId") String appealId) {
    return ApiResponse.success(service.dispatchAppealDetail(token, appealId));
  }

  @PostMapping("/dispatch-appeals")
  public ApiResponse<N14DispatchAppealDetailResponse> createDispatchAppeal(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N14DispatchAppealCreateRequest request) {
    return ApiResponse.success(service.createDispatchAppeal(token, request));
  }

  @PostMapping("/dispatch-appeals/{appealId}/status")
  public ApiResponse<N14DispatchAppealDetailResponse> updateDispatchAppealStatus(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("appealId") String appealId,
      @Valid @RequestBody N14DispatchAppealStatusUpdateRequest request) {
    return ApiResponse.success(service.updateDispatchAppealStatus(token, appealId, request));
  }

  @PostMapping("/dispatch-appeals/{appealId}/action")
  public ApiResponse<N14DispatchAppealDetailResponse> updateDispatchAppealAction(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("appealId") String appealId,
      @Valid @RequestBody N14DispatchAppealStatusUpdateRequest request) {
    return ApiResponse.success(service.updateDispatchAppealStatus(token, appealId, request));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    service.logout(token);
    return ApiResponse.success();
  }
}
