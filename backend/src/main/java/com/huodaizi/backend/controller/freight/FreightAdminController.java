package com.huodaizi.backend.controller.freight;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freight.FreightAdminUpdateRequest;
import com.huodaizi.backend.dto.freight.FreightItemDTO;
import com.huodaizi.backend.dto.freight.FreightPublishRequest;
import com.huodaizi.backend.service.freight.FreightService;
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
@RequestMapping("/api/admin/freight")
public class FreightAdminController {

  private final FreightService freightService;

  public FreightAdminController(FreightService freightService) {
    this.freightService = freightService;
  }

  @GetMapping
  public ApiResponse<List<FreightItemDTO>> list() {
    return ApiResponse.success(freightService.listAll());
  }

  @PostMapping
  public ApiResponse<FreightItemDTO> create(@Valid @RequestBody FreightPublishRequest request) {
    return ApiResponse.success(freightService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<FreightItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody FreightAdminUpdateRequest request) {
    return ApiResponse.success(freightService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<FreightItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody FreightAdminUpdateRequest request) {
    return ApiResponse.success(freightService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<FreightItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody FreightAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return ApiResponse.success(freightService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    freightService.delete(id);
    return ApiResponse.success();
  }
}
