package com.huodaizi.backend.service.riskalert;

import com.huodaizi.backend.dto.riskalert.A08RiskAlertBatchUpdateRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListRequest;
import com.huodaizi.backend.dto.riskalert.A08RiskAlertListResponse;
import com.huodaizi.backend.repository.riskalert.A08RiskAlertRepository;
import org.springframework.stereotype.Service;

@Service
public class A08RiskAlertService {
  private final A08RiskAlertRepository repository;

  public A08RiskAlertService(A08RiskAlertRepository repository) {
    this.repository = repository;
  }

  public A08RiskAlertListResponse list(A08RiskAlertListRequest request) {
    return repository.list(request);
  }

  public A08RiskAlertListResponse batchUpdate(A08RiskAlertBatchUpdateRequest request) {
    return repository.batchUpdate(request);
  }
}
