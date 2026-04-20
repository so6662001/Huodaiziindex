package com.huodaizi.backend.controller.planpricing;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingItemDTO;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListRequest;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListResponse;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingUpdateRequest;
import com.huodaizi.backend.service.planpricing.A07PlanPricingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/plan-pricing")
public class A07PlanPricingAdminController {
  private final A07PlanPricingService service;

  public A07PlanPricingAdminController(A07PlanPricingService service) {
    this.service = service;
  }

  @GetMapping("/plans")
  public ApiResponse<A07PlanPricingListResponse> plans(
      @Valid @ModelAttribute A07PlanPricingListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/plans/{planCode}")
  public ApiResponse<A07PlanPricingItemDTO> update(
      @PathVariable("planCode") String planCode, @Valid @RequestBody A07PlanPricingUpdateRequest request) {
    return ApiResponse.success(service.update(planCode, request));
  }
}
