package com.huodaizi.backend.controller.pickupmonitor;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListResponse;
import com.huodaizi.backend.service.pickupmonitor.A04PickupMonitorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/pickup-monitor")
public class A04PickupMonitorAdminController {

  private final A04PickupMonitorService service;

  public A04PickupMonitorAdminController(A04PickupMonitorService service) {
    this.service = service;
  }

  @GetMapping("/tasks")
  public ApiResponse<A04PickupMonitorListResponse> tasks(
      @Valid @ModelAttribute A04PickupMonitorListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/tasks/status")
  public ApiResponse<A04PickupMonitorListResponse> batchStatus(
      @Valid @RequestBody A04PickupMonitorBatchUpdateRequest request) {
    return ApiResponse.success(service.batchUpdate(request));
  }
}
