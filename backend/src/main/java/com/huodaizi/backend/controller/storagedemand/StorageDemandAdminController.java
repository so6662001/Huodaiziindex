package com.huodaizi.backend.controller.storagedemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.storagedemand.StorageDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandItemDTO;
import com.huodaizi.backend.dto.storagedemand.StorageDemandPublishRequest;
import com.huodaizi.backend.service.storagedemand.StorageDemandService;
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
@RequestMapping("/api/admin/storage-demand")
public class StorageDemandAdminController {

  private final StorageDemandService storageDemandService;

  public StorageDemandAdminController(StorageDemandService storageDemandService) {
    this.storageDemandService = storageDemandService;
  }

  @GetMapping
  public ApiResponse<List<StorageDemandItemDTO>> list() {
    return ApiResponse.success(storageDemandService.listAll());
  }

  @PostMapping
  public ApiResponse<StorageDemandItemDTO> create(
      @Valid @RequestBody StorageDemandPublishRequest request) {
    return ApiResponse.success(storageDemandService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<StorageDemandItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody StorageDemandAdminUpdateRequest request) {
    return ApiResponse.success(storageDemandService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<StorageDemandItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody StorageDemandAdminUpdateRequest request) {
    return ApiResponse.success(storageDemandService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<StorageDemandItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody StorageDemandAdminUpdateRequest request) {
    return ApiResponse.success(storageDemandService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    storageDemandService.delete(id);
    return ApiResponse.success();
  }
}
