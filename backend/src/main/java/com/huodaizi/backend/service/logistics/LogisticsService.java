package com.huodaizi.backend.service.logistics;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.logistics.LogisticsAdminCreateRequest;
import com.huodaizi.backend.dto.logistics.LogisticsDemandCardDTO;
import com.huodaizi.backend.dto.logistics.LogisticsFreightCardDTO;
import com.huodaizi.backend.dto.logistics.LogisticsPageResponse;
import com.huodaizi.backend.dto.logistics.LogisticsQuickEntryDTO;
import com.huodaizi.backend.dto.logistics.LogisticsSearchRequest;
import com.huodaizi.backend.dto.logistics.LogisticsSearchResponse;
import com.huodaizi.backend.dto.logistics.LogisticsSearchResultDTO;
import com.huodaizi.backend.dto.logistics.LogisticsSectionUpdateRequest;
import com.huodaizi.backend.dto.logistics.LogisticsWarehouseCardDTO;
import com.huodaizi.backend.repository.logistics.InMemoryLogisticsRepository;
import com.huodaizi.backend.repository.logistics.LogisticsSectionEntity;
import com.huodaizi.backend.repository.logistics.LogisticsSectionType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LogisticsService {

  private final InMemoryLogisticsRepository repository;

  public LogisticsService(InMemoryLogisticsRepository repository) {
    this.repository = repository;
  }

  public LogisticsPageResponse getOverview() {
    return new LogisticsPageResponse(
        getQuickEntries(),
        getWarehouses(),
        getFreights(),
        getStorageDemands(),
        getTransportDemands(),
        getStations(),
        getAdSlot());
  }

  public LogisticsSearchResponse search(LogisticsSearchRequest request) {
    List<LogisticsSectionEntity> all =
        repository.search(request.city(), request.type(), request.keyword());
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<LogisticsSectionEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    List<LogisticsSearchResultDTO> items = paged.stream().map(this::toSearchResult).toList();
    return new LogisticsSearchResponse(items, all.size());
  }

  public List<LogisticsSectionEntity> adminList(LogisticsSectionType section) {
    return repository.listByType(section, false);
  }

  public LogisticsSectionEntity adminCreate(
      LogisticsSectionType section, LogisticsAdminCreateRequest request) {
    return repository.create(section, request);
  }

  public LogisticsSectionEntity adminUpdate(
      LogisticsSectionType section, String id, LogisticsSectionUpdateRequest request) {
    return repository.update(section, id, request);
  }

  public LogisticsSectionEntity adminChangeStatus(
      LogisticsSectionType section, String id, String status) {
    return repository.changeStatus(section, id, status);
  }

  public LogisticsSectionEntity adminPin(LogisticsSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return repository.pin(section, id, pinned);
  }

  public void adminDelete(LogisticsSectionType section, String id) {
    repository.delete(section, id);
  }

  private List<LogisticsQuickEntryDTO> getQuickEntries() {
    return repository.listByType(LogisticsSectionType.QUICK_ENTRY, true).stream()
        .map(
            e ->
                new LogisticsQuickEntryDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getSubtitle(),
                    valueOrFallback(e.getValue(), "立即发布"),
                    e.getLink()))
        .toList();
  }

  private List<LogisticsWarehouseCardDTO> getWarehouses() {
    return repository.listByType(LogisticsSectionType.WAREHOUSE, true).stream()
        .map(
            e ->
                new LogisticsWarehouseCardDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getCity(),
                    e.getSubtitle(),
                    e.getValue()))
        .toList();
  }

  private List<LogisticsFreightCardDTO> getFreights() {
    return repository.listByType(LogisticsSectionType.FREIGHT, true).stream()
        .map(
            e ->
                new LogisticsFreightCardDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getSubtitle(),
                    valueOrFallback(e.getExtra(), "-"),
                    e.getValue()))
        .toList();
  }

  private List<LogisticsDemandCardDTO> getStorageDemands() {
    return repository.listByType(LogisticsSectionType.STORAGE_DEMAND, true).stream()
        .map(
            e ->
                new LogisticsDemandCardDTO(
                    e.getId(),
                    e.getTitle(),
                    "STORAGE",
                    e.getCity(),
                    e.getLink()))
        .toList();
  }

  private List<LogisticsDemandCardDTO> getTransportDemands() {
    return repository.listByType(LogisticsSectionType.TRANSPORT_DEMAND, true).stream()
        .map(
            e ->
                new LogisticsDemandCardDTO(
                    e.getId(),
                    e.getTitle(),
                    "TRANSPORT",
                    e.getCity(),
                    e.getLink()))
        .toList();
  }

  private List<String> getStations() {
    return repository.listByType(LogisticsSectionType.CITY_STATION, true).stream()
        .map(LogisticsSectionEntity::getTitle)
        .toList();
  }

  private LogisticsWarehouseCardDTO getAdSlot() {
    LogisticsSectionEntity ad =
        repository.listByType(LogisticsSectionType.AD_SLOT, true).stream()
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND.getCode(), "广告位数据为空"));
    return new LogisticsWarehouseCardDTO(
        ad.getId(),
        ad.getTitle(),
        ad.getCity(),
        ad.getSubtitle(),
        ad.getValue());
  }

  private LogisticsSearchResultDTO toSearchResult(LogisticsSectionEntity entity) {
    return new LogisticsSearchResultDTO(
        entity.getId(),
        entity.getType().name(),
        entity.getTitle(),
        entity.getCity(),
        valueOrFallback(entity.getSubtitle(), entity.getContent()),
        valueOrFallback(entity.getValue(), "-"),
        valueOrFallback(entity.getLink(), "#"),
        entity.getStatus(),
        entity.getUpdatedAt().toString());
  }

  private String valueOrFallback(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }
}
