package com.huodaizi.backend.controller.sitecity;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.sitecity.SiteCityOverviewResponse;
import com.huodaizi.backend.service.sitecity.SiteCityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site-city")
public class SiteCityController {

  private final SiteCityService siteCityService;

  public SiteCityController(SiteCityService siteCityService) {
    this.siteCityService = siteCityService;
  }

  @GetMapping("/{city}/overview")
  public ApiResponse<SiteCityOverviewResponse> overview(@PathVariable("city") String city) {
    return ApiResponse.success(siteCityService.overview(city));
  }
}
