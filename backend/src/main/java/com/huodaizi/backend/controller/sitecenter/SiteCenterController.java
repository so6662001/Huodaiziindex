package com.huodaizi.backend.controller.sitecenter;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.sitecenter.SiteCenterOverviewResponse;
import com.huodaizi.backend.service.sitecenter.SiteCenterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site-center")
public class SiteCenterController {

  private final SiteCenterService siteCenterService;

  public SiteCenterController(SiteCenterService siteCenterService) {
    this.siteCenterService = siteCenterService;
  }

  @GetMapping("/overview")
  public ApiResponse<SiteCenterOverviewResponse> overview() {
    return ApiResponse.success(siteCenterService.overview());
  }
}
