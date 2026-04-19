package com.huodaizi.backend.service.buy;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.buy.BuyAdminUpdateRequest;
import com.huodaizi.backend.dto.buy.BuyFilterOptions;
import com.huodaizi.backend.dto.buy.BuyFilterRequest;
import com.huodaizi.backend.dto.buy.BuyItemDTO;
import com.huodaizi.backend.dto.buy.BuyListResponse;
import com.huodaizi.backend.dto.buy.BuyPublishRequest;
import com.huodaizi.backend.repository.buy.BuyEntity;
import com.huodaizi.backend.repository.buy.InMemoryBuyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BuyService {

  private final InMemoryBuyRepository repository;

  public BuyService(InMemoryBuyRepository repository) {
    this.repository = repository;
  }

  public BuyFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public BuyListResponse list(BuyFilterRequest request) {
    List<BuyEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<BuyEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<BuyItemDTO> dtoList = paged.stream().map(this::toDTO).toList();
    return new BuyListResponse(dtoList, filtered.size(), page, pageSize);
  }

  public BuyItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public BuyItemDTO publish(BuyPublishRequest request) {
    BuyEntity created = repository.publish(request);
    return toDTO(created);
  }

  public List<BuyItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public BuyItemDTO create(BuyPublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public BuyItemDTO update(String id, BuyAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public BuyItemDTO changeStatus(String id, String status) {
    BuyEntity updated = repository.adminToggleStatus(id, status);
    return toDTO(updated);
  }

  public BuyItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    BuyEntity updated = repository.adminPin(id, pinned);
    return toDTO(updated);
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private BuyItemDTO toDTO(BuyEntity entity) {
    return new BuyItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getBuyer(),
        entity.getCategory(),
        entity.getSpec(),
        entity.getDemand(),
        entity.getCity(),
        entity.getBudget(),
        entity.getArrival(),
        entity.getContactPhone(),
        entity.getUpdatedAt().toString(),
        entity.getStatus(),
        entity.isPinned());
  }
}
