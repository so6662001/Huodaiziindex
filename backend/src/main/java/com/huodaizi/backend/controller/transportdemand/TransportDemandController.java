package com.huodaizi.backend.controller.transportdemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterOptions;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandItemDTO;
import com.huodaizi.backend.dto.transportdemand.TransportDemandListResponse;
import com.huodaizi.backend.dto.transportdemand.TransportDemandPublishRequest;
import com.huodaizi.backend.service.transportdemand.TransportDemandService;
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
@RequestMapping("/api/v1/transport-demand")
public class TransportDemandController {

  private final TransportDemandService transportDemandService;

  public TransportDemandController(TransportDemandService transportDemandService) {
    this.transportDemandService = transportDemandService;
  }

  @GetMapping("/filters")
  public ApiResponse<TransportDemandFilterOptions> filters() {
    return ApiResponse.success(transportDemandService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<TransportDemandListResponse> list(
      @Valid @ModelAttribute TransportDemandFilterRequest request) {
    return ApiResponse.success(transportDemandService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<TransportDemandItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(transportDemandService.detail(id));
  }

  @PostMapping
  public ApiResponse<TransportDemandItemDTO> publish(
      @Valid @RequestBody TransportDemandPublishRequest request) {
    return ApiResponse.success(transportDemandService.publish(request));
  }
}
