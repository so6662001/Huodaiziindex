package com.huodaizi.backend.controller.newsdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.newsdetail.NewsDetailRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailResponse;
import com.huodaizi.backend.service.newsdetail.NewsDetailService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/news-detail")
public class NewsDetailController {

  private final NewsDetailService newsDetailService;

  public NewsDetailController(NewsDetailService newsDetailService) {
    this.newsDetailService = newsDetailService;
  }

  @GetMapping
  public ApiResponse<NewsDetailResponse> detail(@Valid @ModelAttribute NewsDetailRequest request) {
    return ApiResponse.success(newsDetailService.detail(request));
  }
}
