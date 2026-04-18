package com.huodaizi.backend.controller.freight;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.freight.FreightFilterOptions;
import com.huodaizi.backend.dto.freight.FreightFilterRequest;
import com.huodaizi.backend.dto.freight.FreightItemDTO;
import com.huodaizi.backend.dto.freight.FreightListResponse;
import com.huodaizi.backend.dto.freight.FreightPublishRequest;
import com.huodaizi.backend.service.freight.FreightService;
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
@RequestMapping("/api/v1/freight")
public class FreightController {

  private final FreightService freightService;

  public FreightController(FreightService freightService) {
    this.freightService = freightService;
  }

  @GetMapping("/filters")
  public ApiResponse<FreightFilterOptions> filters() {
    return ApiResponse.success(freightService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<FreightListResponse> list(@Valid @ModelAttribute FreightFilterRequest request) {
    return ApiResponse.success(freightService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<FreightItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(freightService.detail(id));
  }

  @PostMapping
  public ApiResponse<FreightItemDTO> publish(@Valid @RequestBody FreightPublishRequest request) {
    return ApiResponse.success(freightService.publish(request));
  }
}
