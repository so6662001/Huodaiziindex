package com.huodaizi.backend.controller.market;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.market.MarketAdminCreateRequest;
import com.huodaizi.backend.dto.market.MarketAdminUpdateRequest;
import com.huodaizi.backend.dto.market.MarketSectionType;
import com.huodaizi.backend.repository.market.MarketEntity;
import com.huodaizi.backend.service.market.MarketService;
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
@RequestMapping("/api/admin/market")
public class MarketAdminController {

  private final MarketService marketService;

  public MarketAdminController(MarketService marketService) {
    this.marketService = marketService;
  }

  @GetMapping("/{section}")
  public ApiResponse<List<MarketEntity>> list(@PathVariable("section") MarketSectionType section) {
    return ApiResponse.success(marketService.adminList(section));
  }

  @PostMapping("/{section}")
  public ApiResponse<MarketEntity> create(
      @PathVariable("section") MarketSectionType section,
      @Valid @RequestBody MarketAdminCreateRequest request) {
    return ApiResponse.success(marketService.adminCreate(section, request));
  }

  @PutMapping("/{section}/{id}")
  public ApiResponse<MarketEntity> update(
      @PathVariable("section") MarketSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketAdminUpdateRequest request) {
    return ApiResponse.success(marketService.adminUpdate(section, id, request));
  }

  @PutMapping("/{section}/{id}/status")
  public ApiResponse<MarketEntity> changeStatus(
      @PathVariable("section") MarketSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketAdminUpdateRequest request) {
    return ApiResponse.success(marketService.adminChangeStatus(section, id, request.status()));
  }

  @PutMapping("/{section}/{id}/pin")
  public ApiResponse<MarketEntity> pin(
      @PathVariable("section") MarketSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(marketService.adminPin(section, id, request.pinned()));
  }

  @DeleteMapping("/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("section") MarketSectionType section, @PathVariable("id") String id) {
    marketService.adminDelete(section, id);
    return ApiResponse.success();
  }
}
