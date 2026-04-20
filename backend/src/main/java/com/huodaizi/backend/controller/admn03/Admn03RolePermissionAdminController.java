package com.huodaizi.backend.controller.admn03;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn03.Admn03RoleDetailResponse;
import com.huodaizi.backend.dto.admn03.Admn03RoleListRequest;
import com.huodaizi.backend.dto.admn03.Admn03RoleListResponse;
import com.huodaizi.backend.dto.admn03.Admn03RolePermissionUpdateRequest;
import com.huodaizi.backend.dto.admn03.Admn03RoleUpsertRequest;
import com.huodaizi.backend.service.admn03.Admn03RolePermissionAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rbac/roles")
public class Admn03RolePermissionAdminController {
  private final Admn03RolePermissionAdminService service;

  public Admn03RolePermissionAdminController(Admn03RolePermissionAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn03RoleListResponse> list(@Valid @ModelAttribute Admn03RoleListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{roleId}")
  public ApiResponse<Admn03RoleDetailResponse> detail(@PathVariable("roleId") String roleId) {
    return ApiResponse.success(service.detail(roleId));
  }

  @PostMapping
  public ApiResponse<Admn03RoleDetailResponse> upsert(@Valid @RequestBody Admn03RoleUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }

  @PutMapping("/{roleId}/permissions")
  public ApiResponse<Admn03RoleDetailResponse> updatePermissions(
      @PathVariable("roleId") String roleId,
      @Valid @RequestBody Admn03RolePermissionUpdateRequest request) {
    return ApiResponse.success(service.updatePermissions(roleId, request));
  }
}
