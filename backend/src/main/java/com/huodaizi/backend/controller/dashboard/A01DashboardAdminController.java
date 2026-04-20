package com.huodaizi.backend.controller.dashboard;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.dashboard.A01DashboardRequest;
import com.huodaizi.backend.dto.dashboard.A01DashboardResponse;
import com.huodaizi.backend.service.dashboard.A01DashboardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class A01DashboardAdminController {

  private final A01DashboardService service;

  public A01DashboardAdminController(A01DashboardService service) {
    this.service = service;
  }

  @GetMapping("/a01")
  public ApiResponse<A01DashboardResponse> a01(@Valid @ModelAttribute A01DashboardRequest request) {
    return ApiResponse.success(service.dashboard(request));
  }
}
