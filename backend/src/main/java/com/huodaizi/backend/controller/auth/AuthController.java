package com.huodaizi.backend.controller.auth;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.dto.auth.N04OnboardingProgressResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.dto.auth.N05NegotiationDetailResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationSendMessageRequest;
import com.huodaizi.backend.dto.auth.N05NegotiationSessionListResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationStatusUpdateRequest;
import com.huodaizi.backend.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/onboarding/progress")
  public ApiResponse<N04OnboardingProgressResponse> onboardingProgress(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    return ApiResponse.success(service.onboardingProgress(token));
  }

  @GetMapping("/negotiations")
  public ApiResponse<N05NegotiationSessionListResponse> negotiationSessions(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
    return ApiResponse.success(service.negotiationSessions(token, status, keyword, pageNo, pageSize));
  }

  @GetMapping("/negotiations/{sessionId}")
  public ApiResponse<N05NegotiationDetailResponse> negotiationDetail(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId) {
    return ApiResponse.success(service.negotiationDetail(token, sessionId));
  }

  @PostMapping("/negotiations/{sessionId}/messages")
  public ApiResponse<N05NegotiationDetailResponse> negotiationMessage(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId,
      @Valid @RequestBody N05NegotiationSendMessageRequest request) {
    return ApiResponse.success(service.sendNegotiationMessage(token, sessionId, request));
  }

  @PostMapping("/negotiations/{sessionId}/status")
  public ApiResponse<N05NegotiationDetailResponse> negotiationStatus(
      @RequestHeader(name = AUTH_HEADER, required = false) String token,
      @PathVariable("sessionId") String sessionId,
      @Valid @RequestBody N05NegotiationStatusUpdateRequest request) {
    return ApiResponse.success(service.updateNegotiationStatus(token, sessionId, request));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(
      @RequestHeader(name = AUTH_HEADER, required = false) String token) {
    service.logout(token);
    return ApiResponse.success();
  }
}
