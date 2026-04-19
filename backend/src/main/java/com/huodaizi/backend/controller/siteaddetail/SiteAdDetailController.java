package com.huodaizi.backend.controller.siteaddetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailLeadSubmitRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailLeadSubmitResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailPlacementDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailOverviewResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailRequest;
import com.huodaizi.backend.service.siteaddetail.SiteAdDetailService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site-ad-detail")
public class SiteAdDetailController {

  private final SiteAdDetailService siteAdDetailService;

  public SiteAdDetailController(SiteAdDetailService siteAdDetailService) {
    this.siteAdDetailService = siteAdDetailService;
  }

  @GetMapping
  public ApiResponse<SiteAdDetailOverviewResponse> detail(@Valid SiteAdDetailRequest request) {
    return ApiResponse.success(siteAdDetailService.detail(request.id()));
  }

  @GetMapping("/products")
  public ApiResponse<List<SiteAdDetailPlacementDTO>> products() {
    return ApiResponse.success(siteAdDetailService.listProducts());
  }

  @PostMapping("/lead")
  public ApiResponse<SiteAdDetailLeadSubmitResponse> submitLead(
      @Valid @RequestBody SiteAdDetailLeadSubmitRequest request) {
    return ApiResponse.success(siteAdDetailService.submitLead(request));
  }
}
