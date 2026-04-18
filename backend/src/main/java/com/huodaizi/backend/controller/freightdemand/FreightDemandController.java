package com.huodaizi.backend.controller.freightdemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterOptionsResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandItemDTO;
import com.huodaizi.backend.dto.freightdemand.FreightDemandListResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandPublishRequest;
import com.huodaizi.backend.service.freightdemand.FreightDemandService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/freight-demand")
public class FreightDemandController {

  private final FreightDemandService freightDemandService;

  public FreightDemandController(FreightDemandService freightDemandService) {
    this.freightDemandService = freightDemandService;
  }

  @GetMapping("/list")
  public ApiResponse<FreightDemandListResponse> list(
      @Valid @ModelAttribute FreightDemandFilterRequest request) {
    return ApiResponse.success(freightDemandService.list(request));
  }

  @GetMapping("/filter-options")
  public ApiResponse<FreightDemandFilterOptionsResponse> filterOptions() {
    return ApiResponse.success(freightDemandService.filterOptions());
  }

  @PostMapping("/publish")
  public ApiResponse<FreightDemandItemDTO> publish(
      @Valid @RequestBody FreightDemandPublishRequest request) {
    return ApiResponse.success(freightDemandService.publish(request));
  }
}
