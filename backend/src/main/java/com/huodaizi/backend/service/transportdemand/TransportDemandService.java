package com.huodaizi.backend.service.transportdemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.transportdemand.TransportDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterOptions;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandItemDTO;
import com.huodaizi.backend.dto.transportdemand.TransportDemandListResponse;
import com.huodaizi.backend.dto.transportdemand.TransportDemandPublishRequest;
import com.huodaizi.backend.repository.transportdemand.InMemoryTransportDemandRepository;
import com.huodaizi.backend.repository.transportdemand.TransportDemandEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransportDemandService {

  private final InMemoryTransportDemandRepository repository;

  public TransportDemandService(InMemoryTransportDemandRepository repository) {
    this.repository = repository;
  }

  public TransportDemandFilterOptions getFilterOptions() {
    return repository.getFilterOptions();
  }

  public TransportDemandListResponse list(TransportDemandFilterRequest request) {
    List<TransportDemandEntity> filtered = repository.query(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<TransportDemandEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<TransportDemandItemDTO> items = paged.stream().map(this::toDTO).toList();
    return new TransportDemandListResponse(items, filtered.size(), page, pageSize);
  }

  public TransportDemandItemDTO detail(String id) {
    return toDTO(repository.getById(id));
  }

  public TransportDemandItemDTO publish(TransportDemandPublishRequest request) {
    return toDTO(repository.publish(request));
  }

  public List<TransportDemandItemDTO> listAll() {
    return repository.listAll().stream().map(this::toDTO).toList();
  }

  public TransportDemandItemDTO create(TransportDemandPublishRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public TransportDemandItemDTO update(String id, TransportDemandAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public TransportDemandItemDTO changeStatus(String id, String status) {
    return toDTO(repository.adminChangeStatus(id, status));
  }

  public TransportDemandItemDTO pin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, pinned));
  }

  public void delete(String id) {
    repository.adminDelete(id);
  }

  private TransportDemandItemDTO toDTO(TransportDemandEntity entity) {
    return new TransportDemandItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getOriginCity(),
        entity.getDestinationCity(),
        entity.getOriginCity() + " -> " + entity.getDestinationCity(),
        entity.getGoodsCategory(),
        entity.getTonnage(),
        entity.getVehicleType(),
        entity.getTimeliness(),
        entity.getLoadDate(),
        invoiceNeedLabel(entity.isNeedInvoice()),
        loadingNeedLabel(entity.isNeedLoading()),
        entity.getCompanyName(),
        maskContactName(entity.getContactName()),
        entity.getContactPhone(),
        entity.getRemark(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String invoiceNeedLabel(boolean needInvoice) {
    return needInvoice ? "需要开票" : "无需开票";
  }

  private String loadingNeedLabel(boolean needLoading) {
    return needLoading ? "需要装卸协同" : "无需装卸协同";
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
