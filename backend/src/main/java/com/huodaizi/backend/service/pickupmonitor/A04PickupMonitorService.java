package com.huodaizi.backend.service.pickupmonitor;

import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorBatchUpdateRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListRequest;
import com.huodaizi.backend.dto.pickupmonitor.A04PickupMonitorListResponse;
import com.huodaizi.backend.repository.pickupmonitor.A04PickupMonitorRepository;
import org.springframework.stereotype.Service;

@Service
public class A04PickupMonitorService {
  private final A04PickupMonitorRepository repository;

  public A04PickupMonitorService(A04PickupMonitorRepository repository) {
    this.repository = repository;
  }

  public A04PickupMonitorListResponse list(A04PickupMonitorListRequest request) {
    return repository.list(request);
  }

  public A04PickupMonitorListResponse batchUpdate(A04PickupMonitorBatchUpdateRequest request) {
    return repository.batchUpdate(request);
  }
}
