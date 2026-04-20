package com.huodaizi.backend.service.reconcilemonitor;

import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListRequest;
import com.huodaizi.backend.dto.reconcilemonitor.A05ReconcileMonitorListResponse;
import com.huodaizi.backend.repository.reconcilemonitor.A05ReconcileMonitorRepository;
import org.springframework.stereotype.Service;

@Service
public class A05ReconcileMonitorService {
  private final A05ReconcileMonitorRepository repository;

  public A05ReconcileMonitorService(A05ReconcileMonitorRepository repository) {
    this.repository = repository;
  }

  public A05ReconcileMonitorListResponse list(A05ReconcileMonitorListRequest request) {
    return repository.list(request);
  }

  public A05ReconcileMonitorListResponse batchUpdate(A05ReconcileMonitorBatchUpdateRequest request) {
    return repository.batchUpdate(request);
  }
}
