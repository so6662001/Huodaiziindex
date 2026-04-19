package com.huodaizi.backend.controller.warehouse;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterOptions;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterRequest;
import com.huodaizi.backend.dto.warehouse.WarehouseItemDTO;
import com.huodaizi.backend.dto.warehouse.WarehouseListResponse;
import com.huodaizi.backend.dto.warehouse.WarehousePublishRequest;
import com.huodaizi.backend.service.warehouse.WarehouseService;
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
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

  private final WarehouseService warehouseService;

  public WarehouseController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  @GetMapping("/filters")
  public ApiResponse<WarehouseFilterOptions> filters() {
    return ApiResponse.success(warehouseService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<WarehouseListResponse> list(@Valid @ModelAttribute WarehouseFilterRequest request) {
    return ApiResponse.success(warehouseService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<WarehouseItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(warehouseService.detail(id));
  }

  @PostMapping
  public ApiResponse<WarehouseItemDTO> publish(@Valid @RequestBody WarehousePublishRequest request) {
    return ApiResponse.success(warehouseService.publish(request));
  }
}
