package com.huodaizi.backend.controller.admn14;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentDetailResponse;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentListRequest;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentListResponse;
import com.huodaizi.backend.dto.admn14.Admn14AbExperimentUpsertRequest;
import com.huodaizi.backend.service.admn14.Admn14AbExperimentAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ab-experiments")
public class Admn14AbExperimentAdminController {
  private static final String OPERATOR = "admn14-admin";
  private final Admn14AbExperimentAdminService service;

  public Admn14AbExperimentAdminController(Admn14AbExperimentAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn14AbExperimentListResponse> list(
      @Valid @ModelAttribute Admn14AbExperimentListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询A/B实验 status="
            + (request == null ? "" : request.experimentStatus())
            + ", scenario="
            + (request == null ? "" : request.scenarioCode()),
        "TRACE_ADMN14_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{experimentId}")
  public ApiResponse<Admn14AbExperimentDetailResponse> detail(
      @PathVariable("experimentId") String experimentId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看A/B实验详情 experimentId=" + experimentId,
        "TRACE_ADMN14_DETAIL_" + experimentId,
        experimentId);
    return ApiResponse.success(service.detail(experimentId));
  }

  @PutMapping
  public ApiResponse<Admn14AbExperimentDetailResponse> upsert(
      @Valid @RequestBody Admn14AbExperimentUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
