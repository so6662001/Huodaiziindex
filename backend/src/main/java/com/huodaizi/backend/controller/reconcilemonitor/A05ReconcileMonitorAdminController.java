package com.huodaizi.backend.controller.reconcilemonitor;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListResponse;
import com.huodaizi.backend.service.reconcilemonitor.A05ReconcileMonitorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reconcile-monitor")
public class A05ReconcileMonitorAdminController {

  private final A05ReconcileMonitorService service;

  public A05ReconcileMonitorAdminController(A05ReconcileMonitorService service) {
    this.service = service;
  }

  @GetMapping("/tasks")
  public ApiResponse<A05ReconcileMonitorListResponse> tasks(
      @Valid @ModelAttribute A05ReconcileMonitorListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/tasks/status")
  public ApiResponse<A05ReconcileMonitorListResponse> batchStatus(
      @Valid @RequestBody A05ReconcileMonitorBatchUpdateRequest request) {
    return ApiResponse.success(service.batchUpdate(request));
  }
}
