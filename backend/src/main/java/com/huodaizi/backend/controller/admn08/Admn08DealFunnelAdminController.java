package com.huodaizi.backend.controller.admn08;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn08.Admn08DealFunnelRequest;
import com.huodaizi.backend.dto.admn08.Admn08DealFunnelResponse;
import com.huodaizi.backend.service.admn08.Admn08DealFunnelAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/deal-funnel")
public class Admn08DealFunnelAdminController {
  private final Admn08DealFunnelAdminService service;
  private static final String OPERATOR = "admn08-admin";

  public Admn08DealFunnelAdminController(Admn08DealFunnelAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn08DealFunnelResponse> funnel(@Valid @ModelAttribute Admn08DealFunnelRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询成交漏斗分析 days="
            + (request == null ? "" : request.safeDays())
            + ", city="
            + (request == null ? "" : request.safeCity()),
        "TRACE_ADMN08_FUNNEL_" + System.currentTimeMillis());
    return ApiResponse.success(service.funnel(request));
  }
}
