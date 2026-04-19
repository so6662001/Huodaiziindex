package com.huodaizi.backend.service.quoteefficiency;

import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyBatchUpdateRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyListResponse;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyQuickQuoteRequest;
import com.huodaizi.backend.dto.quoteefficiency.A03QuoteEfficiencyTaskItemDTO;
import com.huodaizi.backend.repository.quoteefficiency.A03QuoteEfficiencyRepository;
import org.springframework.stereotype.Service;

@Service
public class A03QuoteEfficiencyService {

  private final A03QuoteEfficiencyRepository repository;

  public A03QuoteEfficiencyService(A03QuoteEfficiencyRepository repository) {
    this.repository = repository;
  }

  public A03QuoteEfficiencyListResponse list(A03QuoteEfficiencyListRequest request) {
    return repository.list(request);
  }

  public A03QuoteEfficiencyListResponse batchUpdate(A03QuoteEfficiencyBatchUpdateRequest request) {
    return repository.batchUpdate(request);
  }

  public A03QuoteEfficiencyTaskItemDTO quickQuote(String leadId, A03QuoteEfficiencyQuickQuoteRequest request) {
    return repository.quickQuote(leadId, request);
  }
}
