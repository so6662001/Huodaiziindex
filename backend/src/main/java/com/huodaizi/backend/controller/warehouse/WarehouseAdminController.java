package com.huodaizi.backend.controller.warehouse;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.warehouse.WarehouseAdminUpdateRequest;
import com.huodaizi.backend.dto.warehouse.WarehouseItemDTO;
import com.huodaizi.backend.dto.warehouse.WarehousePublishRequest;
import com.huodaizi.backend.service.warehouse.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/warehouse")
public class WarehouseAdminController {

  private final WarehouseService warehouseService;

  public WarehouseAdminController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  @GetMapping
  public ApiResponse<List<WarehouseItemDTO>> list() {
    return ApiResponse.success(warehouseService.listAll());
  }

  @PostMapping
  public ApiResponse<WarehouseItemDTO> create(@Valid @RequestBody WarehousePublishRequest request) {
    return ApiResponse.success(warehouseService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<WarehouseItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody WarehouseAdminUpdateRequest request) {
    return ApiResponse.success(warehouseService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<WarehouseItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody WarehouseAdminUpdateRequest request) {
    return ApiResponse.success(warehouseService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<WarehouseItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody WarehouseAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new com.huodaizi.backend.common.BaseException(
          com.huodaizi.backend.common.ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return ApiResponse.success(warehouseService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    warehouseService.delete(id);
    return ApiResponse.success();
  }
}
