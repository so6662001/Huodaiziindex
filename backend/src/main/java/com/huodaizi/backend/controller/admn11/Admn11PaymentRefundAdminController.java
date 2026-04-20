package com.huodaizi.backend.controller.admn11;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundDetailResponse;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundListRequest;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundListResponse;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundReviewRequest;
import com.huodaizi.backend.service.admn11.Admn11PaymentRefundAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/payment-refunds")
public class Admn11PaymentRefundAdminController {
  private static final String OPERATOR = "admn11-admin";
  private final Admn11PaymentRefundAdminService service;

  public Admn11PaymentRefundAdminController(Admn11PaymentRefundAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn11PaymentRefundListResponse> list(
      @Valid @ModelAttribute Admn11PaymentRefundListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询支付退款 refundStatus="
            + (request == null ? "" : request.refundStatus())
            + ", refundReason="
            + (request == null ? "" : request.refundReasonCode()),
        "TRACE_ADMN11_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{refundId}")
  public ApiResponse<Admn11PaymentRefundDetailResponse> detail(@PathVariable("refundId") String refundId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看支付退款详情 refundId=" + refundId,
        "TRACE_ADMN11_DETAIL_" + refundId,
        refundId);
    return ApiResponse.success(service.detail(refundId));
  }

  @PutMapping("/{refundId}/review")
  public ApiResponse<Admn11PaymentRefundDetailResponse> review(
      @PathVariable("refundId") String refundId,
      @Valid @RequestBody Admn11PaymentRefundReviewRequest request) {
    return ApiResponse.success(service.review(refundId, request));
  }
}
