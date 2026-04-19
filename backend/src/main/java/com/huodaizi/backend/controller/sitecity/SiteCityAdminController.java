package com.huodaizi.backend.controller.sitecity;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminCreateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCitySectionType;
import com.huodaizi.backend.repository.sitecity.SiteCityEntity;
import com.huodaizi.backend.service.sitecity.SiteCityService;
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
@RequestMapping("/api/admin/site-city")
public class SiteCityAdminController {

  private final SiteCityService siteCityService;

  public SiteCityAdminController(SiteCityService siteCityService) {
    this.siteCityService = siteCityService;
  }

  @GetMapping("/{city}/{section}")
  public ApiResponse<List<SiteCityEntity>> list(
      @PathVariable("city") String city, @PathVariable("section") SiteCitySectionType section) {
    return ApiResponse.success(siteCityService.adminList(city, section));
  }

  @PostMapping("/{city}/{section}")
  public ApiResponse<SiteCityEntity> create(
      @PathVariable("city") String city,
      @PathVariable("section") SiteCitySectionType section,
      @Valid @RequestBody SiteCityAdminCreateRequest request) {
    return ApiResponse.success(siteCityService.adminCreate(city, section, request));
  }

  @PutMapping("/{city}/{section}/{id}")
  public ApiResponse<SiteCityEntity> update(
      @PathVariable("city") String city,
      @PathVariable("section") SiteCitySectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCityAdminUpdateRequest request) {
    return ApiResponse.success(siteCityService.adminUpdate(city, section, id, request));
  }

  @PutMapping("/{city}/{section}/{id}/status")
  public ApiResponse<SiteCityEntity> changeStatus(
      @PathVariable("city") String city,
      @PathVariable("section") SiteCitySectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCityAdminUpdateRequest request) {
    return ApiResponse.success(siteCityService.adminChangeStatus(city, section, id, request.status()));
  }

  @PutMapping("/{city}/{section}/{id}/pin")
  public ApiResponse<SiteCityEntity> pin(
      @PathVariable("city") String city,
      @PathVariable("section") SiteCitySectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody SiteCityAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return ApiResponse.success(siteCityService.adminPin(city, section, id, request.pinned()));
  }

  @DeleteMapping("/{city}/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("city") String city,
      @PathVariable("section") SiteCitySectionType section,
      @PathVariable("id") String id) {
    siteCityService.adminDelete(city, section, id);
    return ApiResponse.success();
  }
}
