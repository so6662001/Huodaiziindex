package com.huodaizi.backend.service.warehousedetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminCreateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminItemDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailContactDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailMainDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailRelatedDemandDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailRelatedWarehouseDTO;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailResponse;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailSectionType;
import com.huodaizi.backend.repository.warehousedetail.InMemoryWarehouseDetailRepository;
import com.huodaizi.backend.repository.warehousedetail.WarehouseDetailEntity;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WarehouseDetailService {

  private static final String DEFAULT_ID = "W20260418001";

  private final InMemoryWarehouseDetailRepository repository;

  public WarehouseDetailService(InMemoryWarehouseDetailRepository repository) {
    this.repository = repository;
  }

  public WarehouseDetailResponse detail(WarehouseDetailRequest request) {
    String warehouseId = sanitizeWarehouseId(request.id());
    WarehouseDetailEntity mainEntity =
        repository.onlineByWarehouseIdAndSection(warehouseId, WarehouseDetailSectionType.MAIN);
    if (mainEntity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓库详情不存在");
    }

    WarehouseDetailMainDTO main = toMain(mainEntity);
    List<WarehouseDetailRelatedWarehouseDTO> relatedWarehouses =
        repository.listOnlineByWarehouseIdAndSection(warehouseId, WarehouseDetailSectionType.RELATED_WAREHOUSE)
            .stream()
            .map(this::toRelatedWarehouse)
            .toList();
    List<WarehouseDetailRelatedDemandDTO> relatedDemands =
        repository.listOnlineByWarehouseIdAndSection(warehouseId, WarehouseDetailSectionType.RELATED_DEMAND)
            .stream()
            .map(this::toRelatedDemand)
            .toList();

    WarehouseDetailEntity contactEntity =
        repository.onlineByWarehouseIdAndSection(warehouseId, WarehouseDetailSectionType.CONTACT);
    WarehouseDetailContactDTO contact = contactEntity == null ? null : toContact(contactEntity, false);

    WarehouseDetailEntity adEntity =
        repository.onlineByWarehouseIdAndSection(warehouseId, WarehouseDetailSectionType.AD);
    WarehouseDetailRelatedWarehouseDTO ad = adEntity == null ? null : toRelatedWarehouse(adEntity);

    return new WarehouseDetailResponse(main, relatedWarehouses, relatedDemands, contact, ad);
  }

  public List<WarehouseDetailAdminItemDTO> adminList(String warehouseId, WarehouseDetailSectionType section) {
    return repository.adminListByWarehouseIdAndSection(sanitizeWarehouseId(warehouseId), section).stream()
        .map(this::toAdminItem)
        .toList();
  }

  public WarehouseDetailAdminItemDTO adminCreate(
      String warehouseId, WarehouseDetailSectionType section, WarehouseDetailAdminCreateRequest request) {
    return toAdminItem(repository.adminCreate(sanitizeWarehouseId(warehouseId), section, request));
  }

  public WarehouseDetailAdminItemDTO adminUpdate(
      String warehouseId,
      WarehouseDetailSectionType section,
      String recordId,
      WarehouseDetailAdminUpdateRequest request) {
    return toAdminItem(
        repository.adminUpdate(sanitizeWarehouseId(warehouseId), section, recordId, request));
  }

  public WarehouseDetailAdminItemDTO adminChangeStatus(
      String warehouseId, WarehouseDetailSectionType section, String recordId, String status) {
    return toAdminItem(
        repository.adminChangeStatus(sanitizeWarehouseId(warehouseId), section, recordId, status));
  }

  public WarehouseDetailAdminItemDTO adminPin(
      String warehouseId, WarehouseDetailSectionType section, String recordId, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toAdminItem(
        repository.adminPin(sanitizeWarehouseId(warehouseId), section, recordId, pinned));
  }

  public void adminDelete(String warehouseId, WarehouseDetailSectionType section, String recordId) {
    repository.adminDelete(sanitizeWarehouseId(warehouseId), section, recordId);
  }

  private WarehouseDetailMainDTO toMain(WarehouseDetailEntity entity) {
    return new WarehouseDetailMainDTO(
        entity.getWarehouseId(),
        entity.getTitle(),
        entity.getCity(),
        entity.getWarehouseType(),
        entity.getCapacity(),
        entity.getThroughput(),
        entity.getCapability(),
        entity.getQuote(),
        entity.getAddress(),
        entity.getWorkTime(),
        parseTags(entity.getServiceTags()),
        entity.getDescription(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private WarehouseDetailRelatedWarehouseDTO toRelatedWarehouse(WarehouseDetailEntity entity) {
    return new WarehouseDetailRelatedWarehouseDTO(
        entity.getRelatedId(),
        entity.getTitle(),
        entity.getCity(),
        entity.getQuote(),
        entity.getWarehouseType(),
        entity.getLink());
  }

  private WarehouseDetailRelatedDemandDTO toRelatedDemand(WarehouseDetailEntity entity) {
    return new WarehouseDetailRelatedDemandDTO(
        entity.getRelatedId(), entity.getTitle(), entity.getLink());
  }

  private WarehouseDetailContactDTO toContact(WarehouseDetailEntity entity, boolean showFullPhone) {
    String phone = entity.getContactPhone();
    String contactName = entity.getContactName();
    if (!showFullPhone) {
      phone = maskPhone(phone);
      contactName = maskName(contactName);
    }
    return new WarehouseDetailContactDTO(
        contactName, phone, entity.getServiceStatus(), entity.getDescription());
  }

  private WarehouseDetailAdminItemDTO toAdminItem(WarehouseDetailEntity entity) {
    return new WarehouseDetailAdminItemDTO(
        entity.getId(),
        entity.getWarehouseId(),
        entity.getSectionType().name(),
        entity.getTitle(),
        entity.getCity(),
        entity.getWarehouseType(),
        entity.getCapacity(),
        entity.getThroughput(),
        entity.getCapability(),
        entity.getQuote(),
        entity.getAddress(),
        entity.getWorkTime(),
        entity.getServiceTags(),
        entity.getDescription(),
        entity.getRelatedId(),
        entity.getContactName(),
        entity.getContactPhone(),
        entity.getServiceStatus(),
        entity.getLink(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private List<String> parseTags(String tags) {
    if (tags == null || tags.isBlank()) {
      return List.of();
    }
    return Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
  }

  private String sanitizeWarehouseId(String id) {
    if (id == null || id.isBlank()) {
      return DEFAULT_ID;
    }
    return id.trim().toUpperCase();
  }

  private String maskPhone(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone == null ? "-" : phone;
    }
    return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "-";
    }
    if (name.length() <= 1) {
      return "*";
    }
    if (name.length() == 2) {
      return name.substring(0, 1) + "*";
    }
    return name.substring(0, 1) + "**";
  }
}
