package com.huodaizi.backend.controller.marketdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.marketdetail.MarketDetailRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailResponse;
import com.huodaizi.backend.service.marketdetail.MarketDetailService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/market-detail")
public class MarketDetailController {

  private final MarketDetailService marketDetailService;

  public MarketDetailController(MarketDetailService marketDetailService) {
    this.marketDetailService = marketDetailService;
  }

  @GetMapping
  public ApiResponse<MarketDetailResponse> detail(@Valid @ModelAttribute MarketDetailRequest request) {
    return ApiResponse.success(marketDetailService.detail(request));
  }
}
