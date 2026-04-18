package com.huodaizi.backend.controller.freightdemand;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminCreateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandItemDTO;
import com.huodaizi.backend.service.freightdemand.FreightDemandService;
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
@RequestMapping("/api/admin/freight-demand")
public class FreightDemandAdminController {

  private final FreightDemandService freightDemandService;

  public FreightDemandAdminController(FreightDemandService freightDemandService) {
    this.freightDemandService = freightDemandService;
  }

  @GetMapping
  public ApiResponse<List<FreightDemandItemDTO>> list() {
    return ApiResponse.success(freightDemandService.adminList());
  }

  @PostMapping
  public ApiResponse<FreightDemandItemDTO> create(
      @Valid @RequestBody FreightDemandAdminCreateRequest request) {
    return ApiResponse.success(freightDemandService.adminCreate(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<FreightDemandItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody FreightDemandAdminUpdateRequest request) {
    return ApiResponse.success(freightDemandService.adminUpdate(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<FreightDemandItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody FreightDemandAdminUpdateRequest request) {
    return ApiResponse.success(freightDemandService.adminChangeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<FreightDemandItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody FreightDemandAdminUpdateRequest request) {
    return ApiResponse.success(freightDemandService.adminPin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    freightDemandService.adminDelete(id);
    return ApiResponse.success();
  }
}
