package com.huodaizi.backend.controller.siteadlead;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminAssignRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminFollowRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadFollowLogDTO;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminUpdateRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadItemDTO;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadListRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadListResponse;
import com.huodaizi.backend.service.siteadlead.SiteAdLeadService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site-ad-lead")
public class SiteAdLeadAdminController {

  private final SiteAdLeadService service;

  public SiteAdLeadAdminController(SiteAdLeadService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<SiteAdLeadListResponse> list(@Valid SiteAdLeadListRequest request) {
    return ApiResponse.success(service.adminList(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<SiteAdLeadItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(service.adminDetail(id));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<SiteAdLeadItemDTO> updateStatus(
      @PathVariable("id") String id, @Valid @RequestBody SiteAdLeadAdminUpdateRequest request) {
    return ApiResponse.success(service.adminUpdateStatus(id, request));
  }

  @PutMapping("/{id}/assign")
  public ApiResponse<SiteAdLeadItemDTO> assign(
      @PathVariable("id") String id, @Valid @RequestBody SiteAdLeadAdminAssignRequest request) {
    return ApiResponse.success(service.adminAssign(id, request));
  }

  @PostMapping("/{id}/follow")
  public ApiResponse<SiteAdLeadItemDTO> addFollow(
      @PathVariable("id") String id, @Valid @RequestBody SiteAdLeadAdminFollowRequest request) {
    return ApiResponse.success(service.adminAddFollow(id, request));
  }

  @GetMapping("/{id}/follow")
  public ApiResponse<List<SiteAdLeadFollowLogDTO>> followLogs(@PathVariable("id") String id) {
    return ApiResponse.success(service.adminFollowLogs(id));
  }
}
