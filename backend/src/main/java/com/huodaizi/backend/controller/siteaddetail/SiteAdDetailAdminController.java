package com.huodaizi.backend.controller.siteaddetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminCreateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminItemDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailSectionType;
import com.huodaizi.backend.service.siteaddetail.SiteAdDetailService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site-ad-detail")
public class SiteAdDetailAdminController {

  private final SiteAdDetailService siteAdDetailService;

  public SiteAdDetailAdminController(SiteAdDetailService siteAdDetailService) {
    this.siteAdDetailService = siteAdDetailService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<SiteAdDetailAdminItemDTO>> list(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section) {
    return ApiResponse.success(siteAdDetailService.adminList(placementId, section));
  }

  @PostMapping("/{section}")
  public ApiResponse<SiteAdDetailAdminItemDTO> create(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section,
      @Valid @RequestBody SiteAdDetailAdminCreateRequest request) {
    return ApiResponse.success(siteAdDetailService.adminCreate(placementId, section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<SiteAdDetailAdminItemDTO> update(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdDetailAdminUpdateRequest request) {
    return ApiResponse.success(siteAdDetailService.adminUpdate(placementId, section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<SiteAdDetailAdminItemDTO> changeStatus(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdDetailAdminUpdateRequest request) {
    return ApiResponse.success(
        siteAdDetailService.adminChangeStatus(placementId, section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<SiteAdDetailAdminItemDTO> pin(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteAdDetailAdminUpdateRequest request) {
    return ApiResponse.success(siteAdDetailService.adminPin(placementId, section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @RequestParam("placementId") String placementId,
      @PathVariable("section") SiteAdDetailSectionType section, @PathVariable("id") String id) {
    siteAdDetailService.adminDelete(placementId, section, id);
    return ApiResponse.success();
  }
}
