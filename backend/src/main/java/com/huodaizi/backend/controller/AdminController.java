package com.huodaizi.backend.controller;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admin.AdminCreateRequest;
import com.huodaizi.backend.dto.admin.AdminUpdateRequest;
import com.huodaizi.backend.repository.InMemoryHomeRepository;
import com.huodaizi.backend.repository.model.AdminEntityType;
import com.huodaizi.backend.repository.model.BaseAdminEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final InMemoryHomeRepository repository;

  public AdminController(InMemoryHomeRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/config")
  public ApiResponse<BaseAdminEntity> getConfig() {
    return ApiResponse.success(repository.getConfig());
  }

  @PutMapping("/config")
  public ApiResponse<BaseAdminEntity> updateConfig(@Valid @RequestBody AdminUpdateRequest request) {
    return ApiResponse.success(repository.updateConfig(request.title(), request.content()));
  }

  @GetMapping("/{type}")
  public ApiResponse<List<BaseAdminEntity>> listByType(@PathVariable("type") AdminEntityType type) {
    return ApiResponse.success(repository.listByType(type));
  }

  @PutMapping("/{type}/{id}")
  public ApiResponse<BaseAdminEntity> updateByType(
      @PathVariable("type") AdminEntityType type,
      @PathVariable("id") String id,
      @Valid @RequestBody AdminUpdateRequest request) {
    return ApiResponse.success(repository.updateByType(type, id, request));
  }

  @PostMapping("/{type}")
  public ApiResponse<BaseAdminEntity> createByType(
      @PathVariable("type") AdminEntityType type, @Valid @RequestBody AdminCreateRequest request) {
    return ApiResponse.success(repository.createByType(type, request));
  }

  @DeleteMapping("/{type}/{id}")
  public ApiResponse<Void> deleteByType(
      @PathVariable("type") AdminEntityType type, @PathVariable("id") String id) {
    repository.deleteByTypeAndId(type, id);
    return ApiResponse.success();
  }
}
