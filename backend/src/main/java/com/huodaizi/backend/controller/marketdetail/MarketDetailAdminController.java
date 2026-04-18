package com.huodaizi.backend.controller.marketdetail;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminCreateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailSectionType;
import com.huodaizi.backend.repository.marketdetail.MarketDetailEntity;
import com.huodaizi.backend.service.marketdetail.MarketDetailService;
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
@RequestMapping("/api/admin/market-detail")
public class MarketDetailAdminController {

  private final MarketDetailService marketDetailService;

  public MarketDetailAdminController(MarketDetailService marketDetailService) {
    this.marketDetailService = marketDetailService;
  }

  @GetMapping("/{symbol}/{city}/{section}")
  public ApiResponse<List<MarketDetailEntity>> list(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section) {
    return ApiResponse.success(marketDetailService.adminList(symbol, city, section));
  }

  @PostMapping("/{symbol}/{city}/{section}")
  public ApiResponse<MarketDetailEntity> create(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section,
      @Valid @RequestBody MarketDetailAdminCreateRequest request) {
    return ApiResponse.success(marketDetailService.adminCreate(symbol, city, section, request));
  }

  @PutMapping("/{symbol}/{city}/{section}/{id}")
  public ApiResponse<MarketDetailEntity> update(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketDetailAdminUpdateRequest request) {
    return ApiResponse.success(marketDetailService.adminUpdate(symbol, city, section, id, request));
  }

  @PutMapping("/{symbol}/{city}/{section}/{id}/status")
  public ApiResponse<MarketDetailEntity> changeStatus(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketDetailAdminUpdateRequest request) {
    return ApiResponse.success(
        marketDetailService.adminChangeStatus(symbol, city, section, id, request.status()));
  }

  @PutMapping("/{symbol}/{city}/{section}/{id}/pin")
  public ApiResponse<MarketDetailEntity> pin(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section,
      @PathVariable("id") String id,
      @Valid @RequestBody MarketDetailAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(
        marketDetailService.adminPin(symbol, city, section, id, request.pinned()));
  }

  @DeleteMapping("/{symbol}/{city}/{section}/{id}")
  public ApiResponse<Void> delete(
      @PathVariable("symbol") String symbol,
      @PathVariable("city") String city,
      @PathVariable("section") MarketDetailSectionType section,
      @PathVariable("id") String id) {
    marketDetailService.adminDelete(symbol, city, section, id);
    return ApiResponse.success();
  }
}
