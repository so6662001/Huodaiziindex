package com.huodaizi.backend.controller.spot;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.spot.SpotAdminUpdateRequest;
import com.huodaizi.backend.dto.spot.SpotItemDTO;
import com.huodaizi.backend.dto.spot.SpotPublishRequest;
import com.huodaizi.backend.service.spot.SpotService;
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
@RequestMapping("/api/admin/spot")
public class SpotAdminController {

  private final SpotService spotService;

  public SpotAdminController(SpotService spotService) {
    this.spotService = spotService;
  }

  @GetMapping
  public ApiResponse<List<SpotItemDTO>> list() {
    return ApiResponse.success(spotService.listAll());
  }

  @PostMapping
  public ApiResponse<SpotItemDTO> create(@Valid @RequestBody SpotPublishRequest request) {
    return ApiResponse.success(spotService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<SpotItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody SpotAdminUpdateRequest request) {
    return ApiResponse.success(spotService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<SpotItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody SpotAdminUpdateRequest request) {
    return ApiResponse.success(spotService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<SpotItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody SpotAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(spotService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    spotService.delete(id);
    return ApiResponse.success();
  }
}
