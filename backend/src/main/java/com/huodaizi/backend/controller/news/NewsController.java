package com.huodaizi.backend.controller.news;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.news.NewsListRequest;
import com.huodaizi.backend.dto.news.NewsListResponse;
import com.huodaizi.backend.dto.news.NewsOverviewResponse;
import com.huodaizi.backend.service.news.NewsService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/news")
public class NewsController {

  private final NewsService newsService;

  public NewsController(NewsService newsService) {
    this.newsService = newsService;
  }

  @GetMapping("/overview")
  public ApiResponse<NewsOverviewResponse> overview(@Valid @ModelAttribute NewsListRequest request) {
    return ApiResponse.success(newsService.overview(request));
  }

  @GetMapping
  public ApiResponse<NewsListResponse> list(@Valid @ModelAttribute NewsListRequest request) {
    return ApiResponse.success(newsService.list(request));
  }
}
