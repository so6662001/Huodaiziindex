package com.huodaizi.backend.controller.storagedemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterOptions;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandItemDTO;
import com.huodaizi.backend.dto.storagedemand.StorageDemandListResponse;
import com.huodaizi.backend.dto.storagedemand.StorageDemandPublishRequest;
import com.huodaizi.backend.service.storagedemand.StorageDemandService;
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
@RequestMapping("/api/v1/storage-demand")
public class StorageDemandController {

  private final StorageDemandService storageDemandService;

  public StorageDemandController(StorageDemandService storageDemandService) {
    this.storageDemandService = storageDemandService;
  }

  @GetMapping("/filters")
  public ApiResponse<StorageDemandFilterOptions> filters() {
    return ApiResponse.success(storageDemandService.getFilterOptions());
  }

  @GetMapping
  public ApiResponse<StorageDemandListResponse> list(
      @Valid @ModelAttribute StorageDemandFilterRequest request) {
    return ApiResponse.success(storageDemandService.list(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<StorageDemandItemDTO> detail(@PathVariable("id") String id) {
    return ApiResponse.success(storageDemandService.detail(id));
  }

  @PostMapping
  public ApiResponse<StorageDemandItemDTO> publish(
      @Valid @RequestBody StorageDemandPublishRequest request) {
    return ApiResponse.success(storageDemandService.publish(request));
  }
}
