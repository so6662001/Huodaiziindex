package com.huodaizi.backend.service.spot;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.spot.SpotAdminUpdateRequest;
import com.huodaizi.backend.dto.spot.SpotFilterRequest;
import com.huodaizi.backend.dto.spot.SpotFilterOptions;
import com.huodaizi.backend.dto.spot.SpotItemDTO;
import com.huodaizi.backend.dto.spot.SpotListResponse;
import com.huodaizi.backend.dto.spot.SpotPublishRequest;
import com.huodaizi.backend.repository.spot.InMemorySpotRepository;
import com.huodaizi.backend.repository.spot.SpotEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpotService {

  private final InMemorySpotRepository repository;

  public SpotService(InMemorySpotRepository repository) {
    this.repository = repository;
  }

  public SpotFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public SpotListResponse list(SpotFilterRequest request) {
    List<SpotEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<SpotEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<SpotItemDTO> dtoList = paged.stream().map(this::toDTO).toList();
    return new SpotListResponse(dtoList, filtered.size(), page, pageSize);
  }

  public SpotItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public SpotItemDTO publish(SpotPublishRequest request) {
    SpotEntity created = repository.publish(request);
    return toDTO(created);
  }

  public List<SpotItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public SpotItemDTO create(SpotPublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public SpotItemDTO update(String id, SpotAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public SpotItemDTO changeStatus(String id, String status) {
    SpotEntity updated = repository.adminToggleStatus(id, status);
    return toDTO(updated);
  }

  public SpotItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    SpotEntity updated = repository.adminPin(id, pinned);
    return toDTO(updated);
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private SpotItemDTO toDTO(SpotEntity entity) {
    return new SpotItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getSeller(),
        entity.getCategory(),
        entity.getSpec(),
        entity.getTonnage(),
        entity.getCity(),
        entity.getPrice(),
        entity.getDelivery(),
        entity.getContactPhone(),
        entity.getUpdatedAt().toString(),
        entity.getStatus(),
        entity.isPinned());
  }
}
