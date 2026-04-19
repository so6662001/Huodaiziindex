package com.huodaizi.backend.controller.freightdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminCreateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminItemDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailSectionType;
import com.huodaizi.backend.service.freightdetail.FreightDetailService;
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
@RequestMapping("/api/admin/freight-detail")
public class FreightDetailAdminController {

  private final FreightDetailService freightDetailService;

  public FreightDetailAdminController(FreightDetailService freightDetailService) {
    this.freightDetailService = freightDetailService;
  }

  @GetMapping("/{id}/{section}")
  public ApiResponse<List<FreightDetailAdminItemDTO>> list(
      @PathVariable("id") String id, @PathVariable("section") FreightDetailSectionType section) {
    return ApiResponse.success(freightDetailService.adminList(id, section));
  }

  @PostMapping("/{id}/{section}")
  public ApiResponse<FreightDetailAdminItemDTO> create(
      @PathVariable("id") String id,
      @PathVariable("section") FreightDetailSectionType section,
      @Valid @RequestBody FreightDetailAdminCreateRequest request) {
    return ApiResponse.success(freightDetailService.adminCreate(id, section, request));
  }

  @PutMapping("/{id}/{section}/{recordId}")
  public ApiResponse<FreightDetailAdminItemDTO> update(
      @PathVariable("id") String id,
      @PathVariable("section") FreightDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody FreightDetailAdminUpdateRequest request) {
    return ApiResponse.success(freightDetailService.adminUpdate(id, section, recordId, request));
  }

  @PutMapping("/{id}/{section}/{recordId}/status")
  public ApiResponse<FreightDetailAdminItemDTO> changeStatus(
      @PathVariable("id") String id,
      @PathVariable("section") FreightDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody FreightDetailAdminUpdateRequest request) {
    return ApiResponse.success(
        freightDetailService.adminChangeStatus(id, section, recordId, request.status()));
  }

  @PutMapping("/{id}/{section}/{recordId}/pin")
  public ApiResponse<FreightDetailAdminItemDTO> pin(
      @PathVariable("id") String id,
      @PathVariable("section") FreightDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody FreightDetailAdminUpdateRequest request) {
    return ApiResponse.success(freightDetailService.adminPin(id, section, recordId, request.pinned()));
  }

  @DeleteMapping("/{id}/{section}/{recordId}")
  public ApiResponse<Void> delete(
      @PathVariable("id") String id,
      @PathVariable("section") FreightDetailSectionType section,
      @PathVariable("recordId") String recordId) {
    freightDetailService.adminDelete(id, section, recordId);
    return ApiResponse.success();
  }
}
