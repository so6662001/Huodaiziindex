package com.huodaizi.backend.controller.riskalert;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertBatchUpdateRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListResponse;
import com.huodaizi.backend.service.riskalert.A08RiskAlertService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/risk-alert")
public class A08RiskAlertAdminController {
  private final A08RiskAlertService service;

  public A08RiskAlertAdminController(A08RiskAlertService service) {
    this.service = service;
  }

  @GetMapping("/tasks")
  public ApiResponse<A08RiskAlertListResponse> tasks(
      @Valid @ModelAttribute A08RiskAlertListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/tasks/status")
  public ApiResponse<A08RiskAlertListResponse> batchStatus(
      @Valid @RequestBody A08RiskAlertBatchUpdateRequest request) {
    return ApiResponse.success(service.batchUpdate(request));
  }
}
