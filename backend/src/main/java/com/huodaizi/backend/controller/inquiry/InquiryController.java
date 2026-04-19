package com.huodaizi.backend.controller.inquiry;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.inquiry.InquiryCreateRequest;
import com.huodaizi.backend.dto.inquiry.InquiryCreateResponse;
import com.huodaizi.backend.dto.inquiry.InquiryListRequest;
import com.huodaizi.backend.dto.inquiry.InquiryListResponse;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareRequest;
import com.huodaizi.backend.dto.inquiry.InquiryQuoteCompareResponse;
import com.huodaizi.backend.dto.inquiry.InquirySuccessRequest;
import com.huodaizi.backend.dto.inquiry.InquirySuccessResponse;
import com.huodaizi.backend.service.inquiry.InquiryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inquiries")
public class InquiryController {

  private final InquiryService service;

  public InquiryController(InquiryService service) {
    this.service = service;
  }

  @PostMapping
  public ApiResponse<InquiryCreateResponse> create(@Valid @RequestBody InquiryCreateRequest request) {
    return ApiResponse.success(service.create(request));
  }

  @GetMapping
  public ApiResponse<InquiryListResponse> list(@Valid @ModelAttribute InquiryListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/compare")
  public ApiResponse<InquiryQuoteCompareResponse> compare(@Valid @ModelAttribute InquiryQuoteCompareRequest request) {
    return ApiResponse.success(service.compareQuotes(request));
  }

  @GetMapping("/success")
  public ApiResponse<InquirySuccessResponse> success(@Valid @ModelAttribute InquirySuccessRequest request) {
    return ApiResponse.success(service.success(request));
  }
}
