package com.huodaizi.backend.controller.admn06;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityDetailResponse;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityListRequest;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityListResponse;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityReviewRequest;
import com.huodaizi.backend.service.admn06.Admn06LeadQualityAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/lead-quality")
public class Admn06LeadQualityAdminController {
  private final Admn06LeadQualityAdminService service;
  private static final String OPERATOR = "admn06-admin";

  public Admn06LeadQualityAdminController(Admn06LeadQualityAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn06LeadQualityListResponse> list(
      @Valid @ModelAttribute Admn06LeadQualityListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询线索质检 source="
            + (request == null ? "" : request.source())
            + ", status="
            + (request == null ? "" : request.qualityStatus()),
        "TRACE_ADMN06_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{qualityId}")
  public ApiResponse<Admn06LeadQualityDetailResponse> detail(@PathVariable("qualityId") String qualityId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看线索质检详情 qualityId=" + qualityId,
        "TRACE_ADMN06_DETAIL_" + qualityId,
        qualityId);
    return ApiResponse.success(service.detail(qualityId));
  }

  @PutMapping("/{qualityId}/review")
  public ApiResponse<Admn06LeadQualityDetailResponse> review(
      @PathVariable("qualityId") String qualityId,
      @Valid @RequestBody Admn06LeadQualityReviewRequest request) {
    return ApiResponse.success(service.review(qualityId, request));
  }
}
