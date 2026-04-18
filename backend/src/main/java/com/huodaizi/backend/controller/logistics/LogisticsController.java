package com.huodaizi.backend.controller.logistics;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.logistics.LogisticsPageResponse;
import com.huodaizi.backend.dto.logistics.LogisticsSearchRequest;
import com.huodaizi.backend.dto.logistics.LogisticsSearchResponse;
import com.huodaizi.backend.service.logistics.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/logistics")
public class LogisticsController {

  private final LogisticsService logisticsService;

  public LogisticsController(LogisticsService logisticsService) {
    this.logisticsService = logisticsService;
  }

  @GetMapping("/overview")
  public ApiResponse<LogisticsPageResponse> overview() {
    return ApiResponse.success(logisticsService.getOverview());
  }

  @GetMapping("/search")
  public ApiResponse<LogisticsSearchResponse> search(
      @Valid @ModelAttribute LogisticsSearchRequest request) {
    return ApiResponse.success(logisticsService.search(request));
  }
}
