package com.huodaizi.backend.controller.newsdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminCreateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminItemDTO;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailSectionType;
import com.huodaizi.backend.service.newsdetail.NewsDetailService;
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
@RequestMapping("/api/admin/news-detail")
public class NewsDetailAdminController {

  private final NewsDetailService newsDetailService;

  public NewsDetailAdminController(NewsDetailService newsDetailService) {
    this.newsDetailService = newsDetailService;
  }

  @GetMapping("/{id}/{section}")
  public ApiResponse<List<NewsDetailAdminItemDTO>> list(
      @PathVariable("id") String id, @PathVariable("section") NewsDetailSectionType section) {
    return ApiResponse.success(newsDetailService.adminList(id, section));
  }

  @PostMapping("/{id}/{section}")
  public ApiResponse<NewsDetailAdminItemDTO> create(
      @PathVariable("id") String id,
      @PathVariable("section") NewsDetailSectionType section,
      @Valid @RequestBody NewsDetailAdminCreateRequest request) {
    return ApiResponse.success(newsDetailService.adminCreate(id, section, request));
  }

  @PutMapping("/{id}/{section}/{recordId}")
  public ApiResponse<NewsDetailAdminItemDTO> update(
      @PathVariable("id") String id,
      @PathVariable("section") NewsDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody NewsDetailAdminUpdateRequest request) {
    return ApiResponse.success(newsDetailService.adminUpdate(id, section, recordId, request));
  }

  @PutMapping("/{id}/{section}/{recordId}/status")
  public ApiResponse<NewsDetailAdminItemDTO> changeStatus(
      @PathVariable("id") String id,
      @PathVariable("section") NewsDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody NewsDetailAdminUpdateRequest request) {
    return ApiResponse.success(newsDetailService.adminChangeStatus(id, section, recordId, request.status()));
  }

  @PutMapping("/{id}/{section}/{recordId}/pin")
  public ApiResponse<NewsDetailAdminItemDTO> pin(
      @PathVariable("id") String id,
      @PathVariable("section") NewsDetailSectionType section,
      @PathVariable("recordId") String recordId,
      @Valid @RequestBody NewsDetailAdminUpdateRequest request) {
    return ApiResponse.success(newsDetailService.adminPin(id, section, recordId, request.pinned()));
  }

  @DeleteMapping("/{id}/{section}/{recordId}")
  public ApiResponse<Void> delete(
      @PathVariable("id") String id,
      @PathVariable("section") NewsDetailSectionType section,
      @PathVariable("recordId") String recordId) {
    newsDetailService.adminDelete(id, section, recordId);
    return ApiResponse.success();
  }
}
