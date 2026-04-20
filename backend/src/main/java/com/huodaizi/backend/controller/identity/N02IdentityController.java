package com.huodaizi.backend.controller.identity;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.identity.H5N02IdentityListResponse;
import com.huodaizi.backend.dto.identity.H5N02IdentitySwitchRequest;
import com.huodaizi.backend.dto.identity.H5N02IdentitySwitchResponse;
import com.huodaizi.backend.dto.identity.N02IdentityListResponse;
import com.huodaizi.backend.dto.identity.N02IdentitySwitchRequest;
import com.huodaizi.backend.dto.identity.N02IdentitySwitchResponse;
import com.huodaizi.backend.service.identity.N02IdentityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity")
public class N02IdentityController {
  private static final String AUTH_HEADER = "X-Auth-Token";
  private final N02IdentityService service;

  public N02IdentityController(N02IdentityService service) {
    this.service = service;
  }

  @GetMapping("/options")
  public ApiResponse<N02IdentityListResponse> options(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.list(token));
  }

  @PostMapping("/switch")
  public ApiResponse<N02IdentitySwitchResponse> switchIdentity(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N02IdentitySwitchRequest request) {
    return ApiResponse.success(service.switchIdentity(token, request));
  }

  @GetMapping("/h5/options")
  public ApiResponse<H5N02IdentityListResponse> h5Options(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.h5List(token));
  }

  @PostMapping("/h5/switch")
  public ApiResponse<H5N02IdentitySwitchResponse> h5SwitchIdentity(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody H5N02IdentitySwitchRequest request) {
    return ApiResponse.success(service.h5SwitchIdentity(token, request));
  }
}
