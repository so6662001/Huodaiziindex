package com.huodaizi.backend.controller.admn12;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleDetailResponse;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleListRequest;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleListResponse;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleUpsertRequest;
import com.huodaizi.backend.service.admn12.Admn12AdSlotScheduleAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ad-slot-schedules")
public class Admn12AdSlotScheduleAdminController {
  private static final String OPERATOR = "admn12-admin";
  private final Admn12AdSlotScheduleAdminService service;

  public Admn12AdSlotScheduleAdminController(Admn12AdSlotScheduleAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn12AdSlotScheduleListResponse> list(
      @Valid @ModelAttribute Admn12AdSlotScheduleListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询广告位排期 status="
            + (request == null ? "" : request.scheduleStatus())
            + ", slotType="
            + (request == null ? "" : request.slotType()),
        "TRACE_ADMN12_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{scheduleId}")
  public ApiResponse<Admn12AdSlotScheduleDetailResponse> detail(
      @PathVariable("scheduleId") String scheduleId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查看广告位排期详情 scheduleId=" + scheduleId,
        "TRACE_ADMN12_DETAIL_" + scheduleId,
        scheduleId);
    return ApiResponse.success(service.detail(scheduleId));
  }

  @PutMapping
  public ApiResponse<Admn12AdSlotScheduleDetailResponse> upsert(
      @Valid @RequestBody Admn12AdSlotScheduleUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
