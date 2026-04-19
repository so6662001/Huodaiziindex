package com.huodaizi.backend.service.warehouse;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.warehouse.WarehouseAdminUpdateRequest;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterOptions;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterRequest;
import com.huodaizi.backend.dto.warehouse.WarehouseItemDTO;
import com.huodaizi.backend.dto.warehouse.WarehouseListResponse;
import com.huodaizi.backend.dto.warehouse.WarehousePublishRequest;
import com.huodaizi.backend.repository.warehouse.InMemoryWarehouseRepository;
import com.huodaizi.backend.repository.warehouse.WarehouseEntity;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

  private final InMemoryWarehouseRepository repository;

  public WarehouseService(InMemoryWarehouseRepository repository) {
    this.repository = repository;
  }

  public WarehouseFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public WarehouseListResponse list(WarehouseFilterRequest request) {
    List<WarehouseEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<WarehouseEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<WarehouseItemDTO> items = paged.stream().map(this::toDTO).toList();
    return new WarehouseListResponse(items, filtered.size(), page, pageSize);
  }

  public WarehouseItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public WarehouseItemDTO publish(WarehousePublishRequest request) {
    return toDTO(repository.publish(request));
  }

  public List<WarehouseItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public WarehouseItemDTO create(WarehousePublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public WarehouseItemDTO update(String id, WarehouseAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public WarehouseItemDTO changeStatus(String id, String status) {
    return toDTO(repository.adminChangeStatus(id, status));
  }

  public WarehouseItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, pinned));
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private WarehouseItemDTO toDTO(WarehouseEntity entity) {
    return new WarehouseItemDTO(
        entity.getId(),
        entity.getName(),
        entity.getCity(),
        entity.getType(),
        entity.getCapacity(),
        entity.getThroughput(),
        entity.getCapability(),
        parseCategories(entity.getCategories()),
        entity.getPrice(),
        entity.getContactPhone(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private List<String> parseCategories(String categories) {
    if (categories == null || categories.isBlank()) {
      return List.of();
    }
    return Pattern.compile("[,，/|]")
        .splitAsStream(categories)
        .map(String::trim)
        .filter(v -> !v.isBlank())
        .toList();
  }
}
