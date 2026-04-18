package com.huodaizi.backend.service.storagedemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.storagedemand.StorageDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterOptions;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandItemDTO;
import com.huodaizi.backend.dto.storagedemand.StorageDemandListResponse;
import com.huodaizi.backend.dto.storagedemand.StorageDemandPublishRequest;
import com.huodaizi.backend.repository.storagedemand.InMemoryStorageDemandRepository;
import com.huodaizi.backend.repository.storagedemand.StorageDemandEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StorageDemandService {

  private final InMemoryStorageDemandRepository repository;

  public StorageDemandService(InMemoryStorageDemandRepository repository) {
    this.repository = repository;
  }

  public StorageDemandFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public StorageDemandListResponse list(StorageDemandFilterRequest request) {
    List<StorageDemandEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<StorageDemandEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<StorageDemandItemDTO> items = paged.stream().map(this::toDTO).toList();
    return new StorageDemandListResponse(items, filtered.size(), page, pageSize);
  }

  public StorageDemandItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public StorageDemandItemDTO publish(StorageDemandPublishRequest request) {
    return toDTO(repository.publish(request));
  }

  public List<StorageDemandItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public StorageDemandItemDTO create(StorageDemandPublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public StorageDemandItemDTO update(String id, StorageDemandAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public StorageDemandItemDTO changeStatus(String id, String status) {
    return toDTO(repository.adminChangeStatus(id, status));
  }

  public StorageDemandItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, pinned));
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private StorageDemandItemDTO toDTO(StorageDemandEntity entity) {
    return new StorageDemandItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getCity(),
        entity.getGoodsCategory(),
        entity.getTonnage(),
        entity.getStorageDays(),
        entity.getInboundDate(),
        serviceNeedLabel(entity.isNeedLoading(), entity.isNeedSorting()),
        entity.getCompanyName(),
        maskContactName(entity.getContactName()),
        entity.getContactPhone(),
        entity.getRemark(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String serviceNeedLabel(boolean needLoading, boolean needSorting) {
    if (needLoading && needSorting) {
      return "装卸+分拣";
    }
    if (needLoading) {
      return "需要装卸";
    }
    if (needSorting) {
      return "需要分拣";
    }
    return "仅仓储";
  }

  private String maskContactName(String name) {
    if (name == null || name.isBlank() || "未公开".equals(name)) {
      return "未公开";
    }
    String trimmed = name.trim();
    if (trimmed.length() == 1) {
      return "*";
    }
    return trimmed.substring(0, 1) + "**";
  }
}
