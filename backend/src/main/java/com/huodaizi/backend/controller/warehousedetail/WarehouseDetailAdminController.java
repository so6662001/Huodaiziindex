package com.huodaizi.backend.controller.warehousedetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminCreateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminItemDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailSectionType;
import com.huodaizi.backend.service.warehousedetail.WarehouseDetailService;
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
@RequestMapping("/api/admin/warehouse-detail")
public class WarehouseDetailAdminController {

  private final WarehouseDetailService warehouseDetailService;

  public WarehouseDetailAdminController(WarehouseDetailService warehouseDetailService) {
    this.warehouseDetailService = warehouseDetailService;
  }

  @GetMapping("/{id}/{section}")
  public ApiResponse<List<WarehouseDetailAdminItemDTO>> list(
      @PathVariable("id") String id, @PathVariable("section") WarehouseDetailSectionType section) {
    return ApiResponse.success(warehouseDetailService.adminList(id, section));
  }

  @PostMapping("/{id}/{section}")
  public ApiResponse<WarehouseDetailAdminItemDTO> create(
      @PathVariable("id") String id,
      @PathVariable("section") WarehouseDetailSectionType section,
      @Valid @RequestBody WarehouseDetailAdminCreateRequest request) {
    return ApiResponse.success(warehouseDetailService.adminCreate(id, section, request));
  }

  @PutMapping("/{id}/{section}/{recordId}")
  public ApiResponse<WarehouseDetailAdminItemDTO> update(
      @PathVariable("id") String id,
      @PathVariable("section") WarehouseDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody WarehouseDetailAdminUpdateRequest request) {
    return ApiResponse.success(warehouseDetailService.adminUpdate(id, section, recordId, request));
  }

  @PutMapping("/{id}/{section}/{recordId}/status")
  public ApiResponse<WarehouseDetailAdminItemDTO> changeStatus(
      @PathVariable("id") String id,
      @PathVariable("section") WarehouseDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody WarehouseDetailAdminUpdateRequest request) {
    return ApiResponse.success(
        warehouseDetailService.adminChangeStatus(id, section, recordId, request.status()));
  }

  @PutMapping("/{id}/{section}/{recordId}/pin")
  public ApiResponse<WarehouseDetailAdminItemDTO> pin(
      @PathVariable("id") String id,
      @PathVariable("section") WarehouseDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody WarehouseDetailAdminUpdateRequest request) {
    return ApiResponse.success(warehouseDetailService.adminPin(id, section, recordId, request.pinned()));
  }

  @DeleteMapping("/{id}/{section}/{recordId}")
  public ApiResponse<Void> delete(
      @PathVariable("id") String id,
      @PathVariable("section") WarehouseDetailSectionType section,
      @PathVariable("recordId") String recordId) {
    warehouseDetailService.adminDelete(id, section, recordId);
    return ApiResponse.success();
  }
}
