package com.huodaizi.backend.controller.news;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.news.NewsAdminCreateRequest;
import com.huodaizi.backend.dto.news.NewsAdminUpdateRequest;
import com.huodaizi.backend.dto.news.NewsItemDTO;
import com.huodaizi.backend.dto.news.NewsSectionType;
import com.huodaizi.backend.service.news.NewsService;
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
@RequestMapping("/api/admin/news")
public class NewsAdminController {

  private final NewsService newsService;

  public NewsAdminController(NewsService newsService) {
    this.newsService = newsService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<NewsItemDTO>> list(
      @PathVariable("section") NewsSectionType section) {
    return ApiResponse.success(newsService.adminList(section));
  }

  @PostMapping("/{section}")
  public ApiResponse<NewsItemDTO> create(
      @PathVariable("section") NewsSectionType section,
      @Valid @RequestBody NewsAdminCreateRequest request) {
    return ApiResponse.success(newsService.adminCreate(section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<NewsItemDTO> update(
      @PathVariable("section") NewsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody NewsAdminUpdateRequest request) {
    return ApiResponse.success(newsService.adminUpdate(section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<NewsItemDTO> changeStatus(
      @PathVariable("section") NewsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody NewsAdminUpdateRequest request) {
    return ApiResponse.success(newsService.adminChangeStatus(section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<NewsItemDTO> pin(
      @PathVariable("section") NewsSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody NewsAdminUpdateRequest request) {
    return ApiResponse.success(newsService.adminPin(section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("section") NewsSectionType section, @PathVariable("id") String id) {
    newsService.adminDelete(section, id);
    return ApiResponse.success();
  }
}
