package com.huodaizi.backend.controller.admn16;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionDetailResponse;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionListRequest;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionListResponse;
import com.huodaizi.backend.dto.admn16.Admn16DataApiSubscriptionUpsertRequest;
import com.huodaizi.backend.service.admn16.Admn16DataApiSubscriptionAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/data-api-subscriptions")
public class Admn16DataApiSubscriptionAdminController {
  private static final String OPERATOR = "admn16-admin";
  private final Admn16DataApiSubscriptionAdminService service;

  public Admn16DataApiSubscriptionAdminController(Admn16DataApiSubscriptionAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn16DataApiSubscriptionListResponse> list(
      @Valid @ModelAttribute Admn16DataApiSubscriptionListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询数据API订阅 status="
            + (request == null ? "" : request.subscriptionStatus())
            + ", apiProduct="
            + (request == null ? "" : request.apiProductCode())
            + ", cycle="
            + (request == null ? "" : request.billingCycle()),
        "TRACE_ADMN16_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{subscriptionId}")
  public ApiResponse<Admn16DataApiSubscriptionDetailResponse> detail(
      @PathVariable("subscriptionId") String subscriptionId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看数据API订阅详情 subscriptionId=" + subscriptionId,
        "TRACE_ADMN16_DETAIL_" + subscriptionId,
        subscriptionId);
    return ApiResponse.success(service.detail(subscriptionId));
  }

  @PutMapping
  public ApiResponse<Admn16DataApiSubscriptionDetailResponse> upsert(
      @Valid @RequestBody Admn16DataApiSubscriptionUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
