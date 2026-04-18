package com.huodaizi.backend.controller.logistics;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.logistics.LogisticsAdminCreateRequest;
import com.huodaizi.backend.dto.logistics.LogisticsSectionUpdateRequest;
import com.huodaizi.backend.repository.logistics.LogisticsSectionEntity;
import com.huodaizi.backend.repository.logistics.LogisticsSectionType;
import com.huodaizi.backend.service.logistics.LogisticsService;
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
@RequestMapping("/api/admin/logistics")
public class LogisticsAdminController {

  private final LogisticsService logisticsService;

  public LogisticsAdminController(LogisticsService logisticsService) {
    this.logisticsService = logisticsService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<LogisticsSectionEntity>> list(
      @PathVariable("section") LogisticsSectionType section) {
    return ApiResponse.success(logisticsService.adminList(section));
  }

  @PostMapping("/{section}")
  public ApiResponse<LogisticsSectionEntity> create(
      @PathVariable("section") LogisticsSectionType section,
      @Valid @RequestBody LogisticsAdminCreateRequest request) {
    return ApiResponse.success(logisticsService.adminCreate(section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<LogisticsSectionEntity> update(
      @PathVariable("section") LogisticsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody LogisticsSectionUpdateRequest request) {
    return ApiResponse.success(logisticsService.adminUpdate(section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<LogisticsSectionEntity> changeStatus(
      @PathVariable("section") LogisticsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody LogisticsSectionUpdateRequest request) {
    return ApiResponse.success(logisticsService.adminChangeStatus(section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<LogisticsSectionEntity> pin(
      @PathVariable("section") LogisticsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody LogisticsSectionUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(logisticsService.adminPin(section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("section") LogisticsSectionType section, @PathVariable("id") String id) {
    logisticsService.adminDelete(section, id);
    return ApiResponse.success();
  }
}
