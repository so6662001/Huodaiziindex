package com.huodaizi.backend.controller.dispatchstrategy;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyItemDTO;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListResponse;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest;
import com.huodaizi.backend.service.dispatchstrategy.A06DispatchStrategyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dispatch-strategy")
public class A06DispatchStrategyAdminController {
  private final A06DispatchStrategyService service;

  public A06DispatchStrategyAdminController(A06DispatchStrategyService service) {
    this.service = service;
  }

  @GetMapping("/rules")
  public ApiResponse<A06DispatchStrategyListResponse> rules(
      @Valid @ModelAttribute A06DispatchStrategyListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/rules/{sceneCode}")
  public ApiResponse<A06DispatchStrategyItemDTO> update(
      @PathVariable("sceneCode") String sceneCode,
      @Valid @RequestBody A06DispatchStrategyUpdateRequest request) {
    return ApiResponse.success(service.update(sceneCode, request));
  }
}
