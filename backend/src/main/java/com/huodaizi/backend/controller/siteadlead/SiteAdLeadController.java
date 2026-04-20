package com.huodaizi.backend.controller.siteadlead;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadMineRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadMineResponse;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadSubmitRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadSubmitResponse;
import com.huodaizi.backend.service.siteadlead.SiteAdLeadService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site-ad-lead")
public class SiteAdLeadController {

  private final SiteAdLeadService siteAdLeadService;

  public SiteAdLeadController(SiteAdLeadService siteAdLeadService) {
    this.siteAdLeadService = siteAdLeadService;
  }

  @PostMapping("/submit")
  public ApiResponse<SiteAdLeadSubmitResponse> submit(@Valid @RequestBody SiteAdLeadSubmitRequest request) {
    return ApiResponse.success(siteAdLeadService.submit(request));
  }

  @GetMapping("/mine")
  public ApiResponse<SiteAdLeadMineResponse> mine(@Valid @ModelAttribute SiteAdLeadMineRequest request) {
    return ApiResponse.success(siteAdLeadService.mine(request));
  }
}
