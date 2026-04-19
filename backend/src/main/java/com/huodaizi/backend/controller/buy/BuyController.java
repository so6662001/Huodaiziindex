package com.huodaizi.backend.controller.buy;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.buy.BuyFilterOptions;
import com.huodaizi.backend.dto.buy.BuyFilterRequest;
import com.huodaizi.backend.dto.buy.BuyItemDTO;
import com.huodaizi.backend.dto.buy.BuyListResponse;
import com.huodaizi.backend.dto.buy.BuyPublishRequest;
import com.huodaizi.backend.service.buy.BuyService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/buy")
public class BuyController {

  private final BuyService buyService;

  public BuyController(BuyService buyService) {
    this.buyService = buyService;
  }

  @GetMapping("/filters")
  public ApiResponse<BuyFilterOptions> filters() {
    return ApiResponse.success(buyService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<BuyListResponse> list(@Valid @ModelAttribute BuyFilterRequest request) {
    return ApiResponse.success(buyService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<BuyItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(buyService.detail(id));
  }

  @PostMapping
  public ApiResponse<BuyItemDTO> publish(@Valid @RequestBody BuyPublishRequest request) {
    return ApiResponse.success(buyService.publish(request));
  }
}
