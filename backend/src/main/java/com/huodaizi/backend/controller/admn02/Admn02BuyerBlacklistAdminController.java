package com.huodaizi.backend.controller.admn02;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn02.Admn02BuyerBlacklistUpdateRequest;
import com.huodaizi.backend.dto.admn02.Admn02BuyerDetailResponse;
import com.huodaizi.backend.dto.admn02.Admn02BuyerListRequest;
import com.huodaizi.backend.dto.admn02.Admn02BuyerListResponse;
import com.huodaizi.backend.service.admn02.Admn02BuyerBlacklistAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/buyers")
public class Admn02BuyerBlacklistAdminController {
  private final Admn02BuyerBlacklistAdminService service;

  public Admn02BuyerBlacklistAdminController(Admn02BuyerBlacklistAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn02BuyerListResponse> list(@Valid @ModelAttribute Admn02BuyerListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{userId}")
  public ApiResponse<Admn02BuyerDetailResponse> detail(@PathVariable("userId") String userId) {
    return ApiResponse.success(service.detail(userId));
  }

  @PutMapping("/{userId}/blacklist")
  public ApiResponse<Admn02BuyerDetailResponse> blacklist(
      @PathVariable("userId") String userId,
      @Valid @RequestBody Admn02BuyerBlacklistUpdateRequest request) {
    return ApiResponse.success(service.blacklist(userId, request));
  }
}
