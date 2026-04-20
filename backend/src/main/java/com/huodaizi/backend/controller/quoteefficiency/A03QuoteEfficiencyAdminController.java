package com.huodaizi.backend.controller.quoteefficiency;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyBatchUpdateRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListResponse;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyQuickQuoteRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyTaskItemDTO;
import com.huodaizi.backend.service.quoteefficiency.A03QuoteEfficiencyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quote-efficiency")
public class A03QuoteEfficiencyAdminController {

  private final A03QuoteEfficiencyService service;

  public A03QuoteEfficiencyAdminController(A03QuoteEfficiencyService service) {
    this.service = service;
  }

  @GetMapping("/tasks")
  public ApiResponse<A03QuoteEfficiencyListResponse> tasks(
      @Valid @ModelAttribute A03QuoteEfficiencyListRequest request) {
    return ApiResponse.success(service.list(request));
  }

  @PutMapping("/tasks/status")
  public ApiResponse<A03QuoteEfficiencyListResponse> batchStatus(
      @Valid @RequestBody A03QuoteEfficiencyBatchUpdateRequest request) {
    return ApiResponse.success(service.batchUpdate(request));
  }

  @PostMapping("/tasks/{leadId}/quick-quote")
  public ApiResponse<A03QuoteEfficiencyTaskItemDTO> quickQuote(
      @PathVariable("leadId") String leadId,
      @Valid @RequestBody A03QuoteEfficiencyQuickQuoteRequest request) {
    return ApiResponse.success(service.quickQuote(leadId, request));
  }
}
