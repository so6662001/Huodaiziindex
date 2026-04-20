package com.huodaizi.backend.controller.admn01;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationDetailResponse;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationListRequest;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationListResponse;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationReviewRequest;
import com.huodaizi.backend.service.admn01.Admn01MerchantCertificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/merchant-certifications")
public class Admn01MerchantCertificationAdminController {
  private final Admn01MerchantCertificationService service;

  public Admn01MerchantCertificationAdminController(Admn01MerchantCertificationService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn01MerchantCertificationListResponse> list(
      @Valid @ModelAttribute Admn01MerchantCertificationListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{certificationId}")
  public ApiResponse<Admn01MerchantCertificationDetailResponse> detail(
      @PathVariable("certificationId") String certificationId) {
    return ApiResponse.success(service.detail(certificationId));
  }

  @PutMapping("/{certificationId}/review")
  public ApiResponse<Admn01MerchantCertificationDetailResponse> review(
      @PathVariable("certificationId") String certificationId,
      @Valid @RequestBody Admn01MerchantCertificationReviewRequest request) {
    return ApiResponse.success(service.review(certificationId, request));
  }
}
