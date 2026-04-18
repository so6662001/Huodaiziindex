package com.huodaizi.backend.controller.warehousedetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailResponse;
import com.huodaizi.backend.service.warehousedetail.WarehouseDetailService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/warehouse-detail")
public class WarehouseDetailController {

  private final WarehouseDetailService warehouseDetailService;

  public WarehouseDetailController(WarehouseDetailService warehouseDetailService) {
    this.warehouseDetailService = warehouseDetailService;
  }

  @GetMapping
  public ApiResponse<WarehouseDetailResponse> detail(
      @Valid @ModelAttribute WarehouseDetailRequest request) {
    return ApiResponse.success(warehouseDetailService.detail(request));
  }
}
