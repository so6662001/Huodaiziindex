package com.huodaizi.backend.controller.admn15;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketDetailResponse;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketHandleRequest;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketListRequest;
import com.huodaizi.backend.dto.admn15.Admn15RiskAlertTicketListResponse;
import com.huodaizi.backend.service.admn15.Admn15RiskAlertTicketAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/risk-alert-tickets")
public class Admn15RiskAlertTicketAdminController {
  private static final String OPERATOR = "admn15-admin";
  private final Admn15RiskAlertTicketAdminService service;

  public Admn15RiskAlertTicketAdminController(Admn15RiskAlertTicketAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn15RiskAlertTicketListResponse> list(
      @Valid @ModelAttribute Admn15RiskAlertTicketListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询风险预警工单 status="
            + (request == null ? "" : request.ticketStatus())
            + ", riskLevel="
            + (request == null ? "" : request.riskLevel()),
        "TRACE_ADMN15_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<Admn15RiskAlertTicketDetailResponse> detail(
      @PathVariable("ticketId") String ticketId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看风险预警工单详情 ticketId=" + ticketId,
        "TRACE_ADMN15_DETAIL_" + ticketId,
        ticketId);
    return ApiResponse.success(service.detail(ticketId));
  }

  @PutMapping("/{ticketId}/handle")
  public ApiResponse<Admn15RiskAlertTicketDetailResponse> handle(
      @PathVariable("ticketId") String ticketId,
      @Valid @RequestBody Admn15RiskAlertTicketHandleRequest request) {
    return ApiResponse.success(service.handle(ticketId, request));
  }
}
