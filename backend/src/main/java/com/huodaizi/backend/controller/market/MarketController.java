package com.huodaizi.backend.controller.market;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.market.MarketOverviewRequest;
import com.huodaizi.backend.dto.market.MarketOverviewResponse;
import com.huodaizi.backend.service.market.MarketService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/market")
public class MarketController {

  private final MarketService marketService;

  public MarketController(MarketService marketService) {
    this.marketService = marketService;
  }

  @GetMapping("/overview")
  public ApiResponse<MarketOverviewResponse> overview(@Valid @ModelAttribute MarketOverviewRequest request) {
    return ApiResponse.success(marketService.overview(request));
  }
}
