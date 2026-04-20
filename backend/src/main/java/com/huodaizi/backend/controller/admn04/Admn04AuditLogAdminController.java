package com.huodaizi.backend.controller.admn04;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogDetailResponse;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogListRequest;
import com.huodaizi.backend.dto.admn04.Admn04AuditLogListResponse;
import com.huodaizi.backend.service.admn04.Admn04AuditLogAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class Admn04AuditLogAdminController {
  private final Admn04AuditLogAdminService service;
  private static final String OPERATOR = "admn04-admin";

  public Admn04AuditLogAdminController(Admn04AuditLogAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn04AuditLogListResponse> list(@Valid @ModelAttribute Admn04AuditLogListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询审计日志 module="
            + (request == null ? "" : request.moduleCode())
            + ", action="
            + (request == null ? "" : request.actionCode()),
        "TRACE_ADMN04_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{logId}")
  public ApiResponse<Admn04AuditLogDetailResponse> detail(@PathVariable("logId") String logId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR, "查看审计日志详情 logId=" + logId, "TRACE_ADMN04_DETAIL_" + logId, logId);
    return ApiResponse.success(service.detail(logId));
  }
}
