package com.huodaizi.backend.service.freightdemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminCreateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterOptionsResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandItemDTO;
import com.huodaizi.backend.dto.freightdemand.FreightDemandListResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandPublishRequest;
import com.huodaizi.backend.repository.freightdemand.FreightDemandEntity;
import com.huodaizi.backend.repository.freightdemand.InMemoryFreightDemandRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FreightDemandService {

  private final InMemoryFreightDemandRepository repository;

  public FreightDemandService(InMemoryFreightDemandRepository repository) {
    this.repository = repository;
  }

  public FreightDemandFilterOptionsResponse filterOptions() {
    return repository.filterOptions();
  }

  public FreightDemandListResponse list(FreightDemandFilterRequest request) {
    List<FreightDemandEntity> filtered = repository.listOnline(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<FreightDemandEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<FreightDemandItemDTO> items = paged.stream().map(this::toDTO).toList();
    return new FreightDemandListResponse(items, filtered.size(), page, pageSize);
  }

  public FreightDemandItemDTO publish(FreightDemandPublishRequest request) {
    if (request.originCity() != null
        && request.destinationCity() != null
        && request.originCity().trim().equalsIgnoreCase(request.destinationCity().trim())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "起运地与目的地不能相同");
    }
    if (request.agreed() == null || !request.agreed()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先同意服务协议");
    }
    return toDTO(repository.publish(request));
  }

  public List<FreightDemandItemDTO> adminList() {
    return repository.adminList().stream().map(this::toDTO).toList();
  }

  public FreightDemandItemDTO adminCreate(FreightDemandAdminCreateRequest request) {
    return toDTO(repository.adminCreate(request));
  }

  public FreightDemandItemDTO adminUpdate(String id, FreightDemandAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, request));
  }

  public FreightDemandItemDTO adminChangeStatus(String id, String status) {
    return toDTO(repository.adminChangeStatus(id, status));
  }

  public FreightDemandItemDTO adminPin(String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, pinned));
  }

  public void adminDelete(String id) {
    repository.adminDelete(id);
  }

  private FreightDemandItemDTO toDTO(FreightDemandEntity entity) {
    return new FreightDemandItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getOriginCity(),
        entity.getDestinationCity(),
        entity.getGoodsCategory(),
        entity.getTonnage(),
        entity.getVehicleType(),
        entity.getTimeliness(),
        entity.getLoadDate(),
        invoiceNeedLabel(entity.isNeedInvoice()),
        loadingNeedLabel(entity.isNeedLoading()),
        entity.getCompanyName(),
        maskName(entity.getContactName()),
        maskPhone(entity.getContactPhone()),
        entity.getRemark(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String invoiceNeedLabel(boolean invoiceNeed) {
    return invoiceNeed ? "需开票" : "不开票";
  }

  private String loadingNeedLabel(boolean loadingNeed) {
    return loadingNeed ? "需装卸协同" : "仅运输";
  }

  private String maskPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "-";
    }
    String digits = phone.replaceAll("\\s+", "");
    if (digits.length() < 7) {
      return digits;
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "-";
    }
    String trimmed = name.trim();
    if (trimmed.length() <= 1) {
      return "*";
    }
    return trimmed.substring(0, 1) + "**";
  }
}
