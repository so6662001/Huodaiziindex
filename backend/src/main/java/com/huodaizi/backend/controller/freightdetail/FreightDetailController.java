package com.huodaizi.backend.controller.freightdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.freightdetail.FreightDetailRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailResponse;
import com.huodaizi.backend.service.freightdetail.FreightDetailService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/freight-detail")
public class FreightDetailController {

  private final FreightDetailService freightDetailService;

  public FreightDetailController(FreightDetailService freightDetailService) {
    this.freightDetailService = freightDetailService;
  }

  @GetMapping
  public ApiResponse<FreightDetailResponse> detail(@Valid @ModelAttribute FreightDetailRequest request) {
    return ApiResponse.success(freightDetailService.detail(request));
  }
}
