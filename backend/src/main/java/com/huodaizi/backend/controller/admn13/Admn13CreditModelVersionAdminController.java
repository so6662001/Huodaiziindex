package com.huodaizi.backend.controller.admn13;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionDetailResponse;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionListRequest;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionListResponse;
import com.huodaizi.backend.dto.admn13.Admn13CreditModelVersionUpsertRequest;
import com.huodaizi.backend.service.admn13.Admn13CreditModelVersionAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/credit-model-versions")
public class Admn13CreditModelVersionAdminController {
  private static final String OPERATOR = "admn13-admin";
  private final Admn13CreditModelVersionAdminService service;

  public Admn13CreditModelVersionAdminController(Admn13CreditModelVersionAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn13CreditModelVersionListResponse> list(
      @Valid @ModelAttribute Admn13CreditModelVersionListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询信用模型版本 status="
            + (request == null ? "" : request.versionStatus())
            + ", riskLevel="
            + (request == null ? "" : request.riskLevel()),
        "TRACE_ADMN13_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{versionId}")
  public ApiResponse<Admn13CreditModelVersionDetailResponse> detail(
      @PathVariable("versionId") String versionId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看信用模型版本详情 versionId=" + versionId,
        "TRACE_ADMN13_DETAIL_" + versionId,
        versionId);
    return ApiResponse.success(service.detail(versionId));
  }

  @PutMapping
  public ApiResponse<Admn13CreditModelVersionDetailResponse> upsert(
      @Valid @RequestBody Admn13CreditModelVersionUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
