package com.huodaizi.backend.controller.transportdemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.transportdemand.TransportDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandItemDTO;
import com.huodaizi.backend.dto.transportdemand.TransportDemandPublishRequest;
import com.huodaizi.backend.service.transportdemand.TransportDemandService;
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
@RequestMapping("/api/admin/transport-demand")
public class TransportDemandAdminController {

  private final TransportDemandService transportDemandService;

  public TransportDemandAdminController(TransportDemandService transportDemandService) {
    this.transportDemandService = transportDemandService;
  }

  @GetMapping
  public ApiResponse<List<TransportDemandItemDTO>> list() {
    return ApiResponse.success(transportDemandService.listAll());
  }

  @PostMapping
  public ApiResponse<TransportDemandItemDTO> create(
      @Valid @RequestBody TransportDemandPublishRequest request) {
    return ApiResponse.success(transportDemandService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<TransportDemandItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody TransportDemandAdminUpdateRequest request) {
    return ApiResponse.success(transportDemandService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<TransportDemandItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody TransportDemandAdminUpdateRequest request) {
    return ApiResponse.success(transportDemandService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<TransportDemandItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody TransportDemandAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(transportDemandService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    transportDemandService.delete(id);
    return ApiResponse.success();
  }
}
