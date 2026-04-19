package com.huodaizi.backend.controller.auth;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final String AUTH_HEADER = "X-Auth-Token";
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  public ApiResponse<AuthRegisterResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
    return ApiResponse.success(service.register(request));
  }

  @PostMapping("/login")
  public ApiResponse<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
    return ApiResponse.success(service.login(request));
  }

  @GetMapping("/session")
  public ApiResponse<AuthSessionResponse> session(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.session(token));
  }

  @GetMapping("/enterprise-certification/detail")
  public ApiResponse<N03EnterpriseCertificationDetailResponse> enterpriseCertificationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.enterpriseCertificationDetail(token));
  }

  @PostMapping("/enterprise-certification/submit")
  public ApiResponse<N03EnterpriseCertificationDetailResponse> submitEnterpriseCertification(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @Valid @RequestBody N03EnterpriseCertificationSubmitRequest request) {
    return ApiResponse.success(service.submitEnterpriseCertification(token, request));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    service.logout(token);
    return ApiResponse.success();
  }
}
