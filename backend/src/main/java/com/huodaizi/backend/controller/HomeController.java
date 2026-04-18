package com.huodaizi.backend.controller;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.HomeOverviewResponse;
import com.huodaizi.backend.dto.HomeSearchRequest;
import com.huodaizi.backend.service.HomeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/home")
public class HomeController {

  private final HomeService homeService;

  public HomeController(HomeService homeService) {
    this.homeService = homeService;
  }

  @GetMapping("/overview")
  public ApiResponse<HomeOverviewResponse> overview() {
    return ApiResponse.success(homeService.getOverview());
  }

  @GetMapping("/search")
  public ApiResponse<List<Object>> search(@Valid @ModelAttribute HomeSearchRequest request) {
    return ApiResponse.success(homeService.search(request));
  }
}
