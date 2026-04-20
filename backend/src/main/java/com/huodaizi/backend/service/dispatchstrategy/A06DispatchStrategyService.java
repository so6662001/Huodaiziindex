package com.huodaizi.backend.service.dispatchstrategy;

import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyItemDTO;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListRequest;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyListResponse;
import com.huodaizi.backend.dto.dispatchstrategy.A06DispatchStrategyUpdateRequest;
import com.huodaizi.backend.repository.dispatchstrategy.A06DispatchStrategyRepository;
import org.springframework.stereotype.Service;

@Service
public class A06DispatchStrategyService {
  private final A06DispatchStrategyRepository repository;

  public A06DispatchStrategyService(A06DispatchStrategyRepository repository) {
    this.repository = repository;
  }

  public A06DispatchStrategyListResponse list(A06DispatchStrategyListRequest request) {
    return repository.list(request);
  }

  public A06DispatchStrategyItemDTO detail(String sceneCode) {
    return repository.detail(sceneCode);
  }

  public A06DispatchStrategyItemDTO update(String sceneCode, A06DispatchStrategyUpdateRequest request) {
    return repository.update(sceneCode, request);
  }
}
