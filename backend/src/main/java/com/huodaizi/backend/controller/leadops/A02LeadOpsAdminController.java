package com.huodaizi.backend.controller.leadops;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.leadops.A02LeadOpsAssignRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsFollowRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsItemDTO;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsStatusUpdateRequest;
import com.huodaizi.backend.service.leadops.A02LeadOpsService;
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
@RequestMapping("/api/admin/lead-ops")
public class A02LeadOpsAdminController {
  private final A02LeadOpsService service;

  public A02LeadOpsAdminController(A02LeadOpsService service) {
    this.service = service;
  }

  @GetMapping("/leads")
  public ApiResponse<A02LeadOpsListResponse> list(@Valid @ModelAttribute A02LeadOpsListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/leads/{source}/{leadId}")
  public ApiResponse<A02LeadOpsItemDTO> detail(
      @PathVariable("source") String source,
      @PathVariable("leadId") String leadId) {
    return ApiResponse.success(service.detail(source, leadId, "S001"));
  }

  @PutMapping("/leads/{source}/{leadId}/assign")
  public ApiResponse<A02LeadOpsItemDTO> assign(
      @PathVariable("source") String source,
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody A02LeadOpsAssignRequest request) {
    return ApiResponse.success(service.assign(source, leadId, request, "S001"));
  }

  @PutMapping("/leads/{source}/{leadId}/status")
  public ApiResponse<A02LeadOpsItemDTO> status(
      @PathVariable("source") String source,
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody A02LeadOpsStatusUpdateRequest request) {
    return ApiResponse.success(service.updateStatus(source, leadId, request, "S001"));
  }

  @PostMapping("/leads/{source}/{leadId}/follow")
  public ApiResponse<A02LeadOpsItemDTO> follow(
      @PathVariable("source") String source,
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody A02LeadOpsFollowRequest request) {
    return ApiResponse.success(service.follow(source, leadId, request, "S001"));
  }

  @PostMapping("/leads/{source}/{leadId}/quick-quote")
  public ApiResponse<A02LeadOpsItemDTO> quickQuote(
      @PathVariable("source") String source,
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody InquiryMerchantLeadQuoteRequest request) {
    return ApiResponse.success(service.quickQuote(source, leadId, request, "S001"));
  }
}
