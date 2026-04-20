package com.huodaizi.backend.service.planpricing;

import com.huodaizi.backend.dto.planpricing.A07PlanPricingItemDTO;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListRequest;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingListResponse;
import com.huodaizi.backend.dto.planpricing.A07PlanPricingUpdateRequest;
import com.huodaizi.backend.repository.planpricing.A07PlanPricingRepository;
import org.springframework.stereotype.Service;

@Service
public class A07PlanPricingService {
  private final A07PlanPricingRepository repository;

  public A07PlanPricingService(A07PlanPricingRepository repository) {
    this.repository = repository;
  }

  public A07PlanPricingListResponse list(A07PlanPricingListRequest request) {
    return repository.list(request);
  }

  public A07PlanPricingItemDTO update(String planCode, A07PlanPricingUpdateRequest request) {
    return repository.update(planCode, request);
  }
}
