package com.huodaizi.backend.service.leadops;

import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsAssignRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsFollowRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsItemDTO;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListResponse;
import com.huodaizi.backend.dto.leadops.A02LeadOpsSource;
import com.huodaizi.backend.dto.leadops.A02LeadOpsStatusUpdateRequest;
import com.huodaizi.backend.repository.leadops.A02LeadOpsAggregateRepository;
import com.huodaizi.backend.repository.leadops.A02LeadOpsDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class A02LeadOpsService {

  private final A02LeadOpsAggregateRepository aggregateRepository;
  private final A02LeadOpsDetailRepository detailRepository;

  public A02LeadOpsService(
      A02LeadOpsAggregateRepository aggregateRepository, A02LeadOpsDetailRepository detailRepository) {
    this.aggregateRepository = aggregateRepository;
    this.detailRepository = detailRepository;
  }

  public A02LeadOpsListResponse list(A02LeadOpsListRequest request) {
    return aggregateRepository.list(request);
  }

  public A02LeadOpsItemDTO detail(String source, String leadId, String merchantId) {
    return detailRepository.detail(A02LeadOpsSource.fromOrThrow(source), leadId);
  }

  public A02LeadOpsItemDTO assign(
      String source, String leadId, A02LeadOpsAssignRequest request, String merchantId) {
    return detailRepository.assign(A02LeadOpsSource.fromOrThrow(source), leadId, request);
  }

  public A02LeadOpsItemDTO updateStatus(
      String source, String leadId, A02LeadOpsStatusUpdateRequest request, String merchantId) {
    return detailRepository.updateStatus(A02LeadOpsSource.fromOrThrow(source), leadId, request);
  }

  public A02LeadOpsItemDTO follow(
      String source, String leadId, A02LeadOpsFollowRequest request, String merchantId) {
    return detailRepository.follow(A02LeadOpsSource.fromOrThrow(source), leadId, request);
  }

  public A02LeadOpsItemDTO quickQuote(
      String source, String leadId, InquiryMerchantLeadQuoteRequest request, String merchantId) {
    return detailRepository.quickQuote(source, leadId, request);
  }
}
