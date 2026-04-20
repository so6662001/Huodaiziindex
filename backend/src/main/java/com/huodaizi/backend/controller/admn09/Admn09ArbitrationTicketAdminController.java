package com.huodaizi.backend.controller.admn09;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationAssignRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationReviewRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketDetailResponse;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketListRequest;
import com.huodaizi.backend.dto.admn09.Admn09ArbitrationTicketListResponse;
import com.huodaizi.backend.service.admn09.Admn09ArbitrationTicketAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/arbitration-tickets")
public class Admn09ArbitrationTicketAdminController {
  private final Admn09ArbitrationTicketAdminService service;
  private static final String OPERATOR = "admn09-admin";

  public Admn09ArbitrationTicketAdminController(Admn09ArbitrationTicketAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn09ArbitrationTicketListResponse> list(
      @Valid @ModelAttribute Admn09ArbitrationTicketListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询仲裁工单 status="
            + (request == null ? "" : request.arbitrationStatus())
            + ", priority="
            + (request == null ? "" : request.priorityLevel()),
        "TRACE_ADMN09_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<Admn09ArbitrationTicketDetailResponse> detail(@PathVariable("ticketId") String ticketId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看仲裁工单详情 ticketId=" + ticketId,
        "TRACE_ADMN09_DETAIL_" + ticketId,
        ticketId);
    return ApiResponse.success(service.detail(ticketId));
  }

  @PutMapping("/{ticketId}/assign")
  public ApiResponse<Admn09ArbitrationTicketDetailResponse> assign(
      @PathVariable("ticketId") String ticketId,
      @Valid @RequestBody Admn09ArbitrationAssignRequest request) {
    return ApiResponse.success(service.assign(ticketId, request));
  }

  @PostMapping("/{ticketId}/review")
  public ApiResponse<Admn09ArbitrationTicketDetailResponse> review(
      @PathVariable("ticketId") String ticketId,
      @Valid @RequestBody Admn09ArbitrationReviewRequest request) {
    return ApiResponse.success(service.review(ticketId, request));
  }
}
