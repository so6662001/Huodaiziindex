package com.huodaizi.backend.controller.sitead;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.sitead.SiteAdOptionsResponse;
import com.huodaizi.backend.dto.sitead.SiteAdProductDTO;
import com.huodaizi.backend.dto.sitead.SiteAdPublishRequest;
import com.huodaizi.backend.dto.sitead.SiteAdSubmitResponse;
import com.huodaizi.backend.service.sitead.SiteAdService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site-ad")
public class SiteAdController {

  private final SiteAdService siteAdService;

  public SiteAdController(SiteAdService siteAdService) {
    this.siteAdService = siteAdService;
  }

  @GetMapping("/options")
  public ApiResponse<SiteAdOptionsResponse> options() {
    return ApiResponse.success(siteAdService.options());
  }

  @GetMapping("/products")
  public ApiResponse<List<SiteAdProductDTO>> products() {
    return ApiResponse.success(siteAdService.products());
  }

  @PostMapping("/publish")
  public ApiResponse<SiteAdSubmitResponse> publish(@Valid @RequestBody SiteAdPublishRequest request) {
    return ApiResponse.success(siteAdService.publish(request));
  }
}
