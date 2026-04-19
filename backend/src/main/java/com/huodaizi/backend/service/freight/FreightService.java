package com.huodaizi.backend.service.freight;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freight.FreightAdminUpdateRequest;
import com.huodaizi.backend.dto.freight.FreightFilterOptions;
import com.huodaizi.backend.dto.freight.FreightFilterRequest;
import com.huodaizi.backend.dto.freight.FreightItemDTO;
import com.huodaizi.backend.dto.freight.FreightListResponse;
import com.huodaizi.backend.dto.freight.FreightPublishRequest;
import com.huodaizi.backend.repository.freight.FreightEntity;
import com.huodaizi.backend.repository.freight.InMemoryFreightRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FreightService {

  private final InMemoryFreightRepository repository;

  public FreightService(InMemoryFreightRepository repository) {
    this.repository = repository;
  }

  public FreightFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public FreightListResponse list(FreightFilterRequest request) {
    List<FreightEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<FreightEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<FreightItemDTO> items = paged.stream().map(this::toDTO).toList();
    return new FreightListResponse(items, filtered.size(), page, pageSize);
  }

  public FreightItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public FreightItemDTO publish(FreightPublishRequest request) {
    return toDTO(repository.publish(request));
  }

  public List<FreightItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public FreightItemDTO create(FreightPublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public FreightItemDTO update(String id, FreightAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public FreightItemDTO changeStatus(String id, String status) {
    return toDTO(repository.adminChangeStatus(id, status));
  }

  public FreightItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, pinned));
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private FreightItemDTO toDTO(FreightEntity entity) {
    return new FreightItemDTO(
        entity.getId(),
        entity.getProvider(),
        entity.getRoute(),
        entity.getOrigin(),
        entity.getDestination(),
        entity.getVehicleType(),
        entity.getLoadRange(),
        entity.getFrequency(),
        entity.getTimelinessHours() == null ? "-" : entity.getTimelinessHours() + "小时",
        entity.isReturnTruck() ? "有回程车" : "无回程车",
        entity.getPrice(),
        entity.getContactPhone(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }
}
