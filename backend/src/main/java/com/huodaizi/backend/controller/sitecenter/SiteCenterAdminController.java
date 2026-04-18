package com.huodaizi.backend.controller.sitecenter;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminCreateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminItemDTO;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterSectionType;
import com.huodaizi.backend.service.sitecenter.SiteCenterService;
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
@RequestMapping("/api/admin/site-center")
public class SiteCenterAdminController {

  private final SiteCenterService siteCenterService;

  public SiteCenterAdminController(SiteCenterService siteCenterService) {
    this.siteCenterService = siteCenterService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<SiteCenterAdminItemDTO>> list(
      @PathVariable("section") SiteCenterSectionType section) {
    return ApiResponse.success(siteCenterService.adminList(section));
  }

  @PostMapping("/{section}")
  public ApiResponse<SiteCenterAdminItemDTO> create(
      @PathVariable("section") SiteCenterSectionType section,
      @Valid @RequestBody SiteCenterAdminCreateRequest request) {
    return ApiResponse.success(siteCenterService.adminCreate(section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<SiteCenterAdminItemDTO> update(
      @PathVariable("section") SiteCenterSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCenterAdminUpdateRequest request) {
    return ApiResponse.success(siteCenterService.adminUpdate(section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<SiteCenterAdminItemDTO> changeStatus(
      @PathVariable("section") SiteCenterSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCenterAdminUpdateRequest request) {
    return ApiResponse.success(siteCenterService.adminChangeStatus(section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<SiteCenterAdminItemDTO> pin(
      @PathVariable("section") SiteCenterSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCenterAdminUpdateRequest request) {
    return ApiResponse.success(siteCenterService.adminPin(section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("section") SiteCenterSectionType section, @PathVariable("id") String id) {
    siteCenterService.adminDelete(section, id);
    return ApiResponse.success();
  }
}
