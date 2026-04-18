package com.huodaizi.backend.controller.spot;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.spot.SpotFilterRequest;
import com.huodaizi.backend.dto.spot.SpotFilterOptions;
import com.huodaizi.backend.dto.spot.SpotItemDTO;
import com.huodaizi.backend.dto.spot.SpotListResponse;
import com.huodaizi.backend.dto.spot.SpotPublishRequest;
import com.huodaizi.backend.service.spot.SpotService;
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
@RequestMapping("/api/v1/spot")
public class SpotController {

  private final SpotService spotService;

  public SpotController(SpotService spotService) {
    this.spotService = spotService;
  }

  @GetMapping("/filters")
  public ApiResponse<SpotFilterOptions> filters() {
    return ApiResponse.success(spotService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<SpotListResponse> list(@Valid @ModelAttribute SpotFilterRequest request) {
    return ApiResponse.success(spotService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<SpotItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(spotService.detail(id));
  }

  @PostMapping
  public ApiResponse<SpotItemDTO> publish(@Valid @RequestBody SpotPublishRequest request) {
    return ApiResponse.success(spotService.publish(request));
  }
}
