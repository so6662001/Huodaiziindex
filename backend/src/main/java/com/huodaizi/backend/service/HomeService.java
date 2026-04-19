package com.huodaizi.backend.service;

import com.huodaizi.backend.dto.HomeOverviewResponse;
import com.huodaizi.backend.dto.HomeSearchRequest;
import com.huodaizi.backend.repository.model.BaseAdminEntity;
import com.huodaizi.backend.repository.InMemoryHomeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

  private final InMemoryHomeRepository repository;

  public HomeService(InMemoryHomeRepository repository) {
    this.repository = repository;
  }

  public HomeOverviewResponse getOverview() {
    return new HomeOverviewResponse(
        repository.getHomeConfig(),
        repository.getMarketQuotes(),
        repository.getLatestSupplies(),
        repository.getLatestDemands(),
        repository.getWarehouses(),
        repository.getFreights(),
        repository.getStations(),
        repository.getNews(),
        repository.getAdSlots());
  }

  public List<BaseAdminEntity> search(HomeSearchRequest request) {
    String keyword = normalize(request.keyword());
    String city = normalize(request.city());
    String type = normalize(request.type());
    return repository.searchEntities(city, type, keyword);
  }

  private String normalize(String source) {
    return source == null ? "" : source.trim();
  }
}
