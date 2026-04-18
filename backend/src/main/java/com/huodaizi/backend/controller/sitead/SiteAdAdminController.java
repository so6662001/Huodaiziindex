package com.huodaizi.backend.controller.sitead;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.sitead.SiteAdAdminCreateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdAdminItemDTO;
import com.huodaizi.backend.dto.sitead.SiteAdAdminUpdateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdSectionType;
import com.huodaizi.backend.service.sitead.SiteAdService;
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
@RequestMapping("/api/admin/site-ad")
public class SiteAdAdminController {

  private final SiteAdService siteAdService;

  public SiteAdAdminController(SiteAdService siteAdService) {
    this.siteAdService = siteAdService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<SiteAdAdminItemDTO>> list(@PathVariable("section") SiteAdSectionType section) {
    return ApiResponse.success(siteAdService.adminList(section));
  }

  @PostMapping("/{section}")
  public ApiResponse<SiteAdAdminItemDTO> create(
      @PathVariable("section") SiteAdSectionType section,
      @Valid @RequestBody SiteAdAdminCreateRequest request) {
    return ApiResponse.success(siteAdService.adminCreate(section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<SiteAdAdminItemDTO> update(
      @PathVariable("section") SiteAdSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdAdminUpdateRequest request) {
    return ApiResponse.success(siteAdService.adminUpdate(section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<SiteAdAdminItemDTO> status(
      @PathVariable("section") SiteAdSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdAdminUpdateRequest request) {
    return ApiResponse.success(siteAdService.adminChangeStatus(section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<SiteAdAdminItemDTO> pin(
      @PathVariable("section") SiteAdSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdAdminUpdateRequest request) {
    return ApiResponse.success(siteAdService.adminPin(section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("section") SiteAdSectionType section, @PathVariable("id") String id) {
    siteAdService.adminDelete(section, id);
    return ApiResponse.success();
  }
}
