package com.huodaizi.backend.controller.admn10;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleDetailResponse;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleListRequest;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleListResponse;
import com.huodaizi.backend.dto.admn10.Admn10BillingRuleUpsertRequest;
import com.huodaizi.backend.service.admn10.Admn10BillingRuleAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/billing-rules")
public class Admn10BillingRuleAdminController {
  private static final String OPERATOR = "admn10-admin";
  private final Admn10BillingRuleAdminService service;

  public Admn10BillingRuleAdminController(Admn10BillingRuleAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn10BillingRuleListResponse> list(
      @Valid @ModelAttribute Admn10BillingRuleListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询计费规则 status="
            + (request == null ? "" : request.ruleStatus())
            + ", scene="
            + (request == null ? "" : request.sceneCode()),
        "TRACE_ADMN10_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{ruleId}")
  public ApiResponse<Admn10BillingRuleDetailResponse> detail(@PathVariable("ruleId") String ruleId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看计费规则详情 ruleId=" + ruleId,
        "TRACE_ADMN10_DETAIL_" + ruleId,
        ruleId);
    return ApiResponse.success(service.detail(ruleId));
  }

  @PutMapping
  public ApiResponse<Admn10BillingRuleDetailResponse> upsert(
      @Valid @RequestBody Admn10BillingRuleUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
