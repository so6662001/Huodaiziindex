package com.huodaizi.backend.controller.buy;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.buy.BuyAdminUpdateRequest;
import com.huodaizi.backend.dto.buy.BuyItemDTO;
import com.huodaizi.backend.dto.buy.BuyPublishRequest;
import com.huodaizi.backend.service.buy.BuyService;
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
@RequestMapping("/api/admin/buy")
public class BuyAdminController {

  private final BuyService buyService;

  public BuyAdminController(BuyService buyService) {
    this.buyService = buyService;
  }

  @GetMapping
  public ApiResponse<List<BuyItemDTO>> list() {
    return ApiResponse.success(buyService.listAll());
  }

  @PostMapping
  public ApiResponse<BuyItemDTO> create(@Valid @RequestBody BuyPublishRequest request) {
    return ApiResponse.success(buyService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<BuyItemDTO> update(
      @PathVariable("id") String id, @Valid @RequestBody BuyAdminUpdateRequest request) {
    return ApiResponse.success(buyService.update(id, request));
  }

  @PutMapping("/{id}/status")
  public ApiResponse<BuyItemDTO> changeStatus(
      @PathVariable("id") String id, @Valid @RequestBody BuyAdminUpdateRequest request) {
    return ApiResponse.success(buyService.changeStatus(id, request.status()));
  }

  @PutMapping("/{id}/pin")
  public ApiResponse<BuyItemDTO> pin(
      @PathVariable("id") String id, @Valid @RequestBody BuyAdminUpdateRequest request) {
    if (request.pinned() == null) {
      throw new IllegalArgumentException("pinned 不能为空");
    }
    return ApiResponse.success(buyService.pin(id, request.pinned()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") String id) {
    buyService.delete(id);
    return ApiResponse.success();
  }
}
