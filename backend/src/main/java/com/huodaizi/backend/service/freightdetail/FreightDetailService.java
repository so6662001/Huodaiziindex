package com.huodaizi.backend.service.freightdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminCreateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminItemDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailContactDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailMainDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailRelatedDemandDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailRelatedLineDTO;
import com.huodaizi.backend.dto.freightdetail.FreightDetailRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailResponse;
import com.huodaizi.backend.dto.freightdetail.FreightDetailSectionType;
import com.huodaizi.backend.repository.freightdetail.FreightDetailEntity;
import com.huodaizi.backend.repository.freightdetail.InMemoryFreightDetailRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FreightDetailService {

  private static final String DEFAULT_ID = "F20260418001";

  private final InMemoryFreightDetailRepository repository;

  public FreightDetailService(InMemoryFreightDetailRepository repository) {
    this.repository = repository;
  }

  public FreightDetailResponse detail(FreightDetailRequest request) {
    String freightId = sanitizeFreightId(request.id());
    FreightDetailEntity mainEntity =
        repository.onlineByFreightIdAndSection(freightId, FreightDetailSectionType.MAIN);
    if (mainEntity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "专线详情不存在");
    }

    FreightDetailMainDTO main = toMain(mainEntity);
    List<FreightDetailRelatedLineDTO> relatedLines =
        repository.listOnlineByFreightIdAndSection(freightId, FreightDetailSectionType.RELATED_LINE)
            .stream()
            .map(this::toRelatedLine)
            .toList();
    List<FreightDetailRelatedDemandDTO> relatedDemands =
        repository.listOnlineByFreightIdAndSection(
                freightId, FreightDetailSectionType.RELATED_DEMAND)
            .stream()
            .map(this::toRelatedDemand)
            .toList();

    FreightDetailEntity contactEntity =
        repository.onlineByFreightIdAndSection(freightId, FreightDetailSectionType.CONTACT);
    FreightDetailContactDTO contact =
        contactEntity == null ? null : toContact(contactEntity, false);
    FreightDetailEntity adEntity =
        repository.onlineByFreightIdAndSection(freightId, FreightDetailSectionType.AD);
    FreightDetailRelatedLineDTO ad = adEntity == null ? null : toRelatedLine(adEntity);

    return new FreightDetailResponse(main, relatedLines, relatedDemands, contact, ad);
  }

  public List<FreightDetailAdminItemDTO> adminList(String freightId, FreightDetailSectionType section) {
    return repository.adminListByFreightIdAndSection(sanitizeFreightId(freightId), section).stream()
        .map(this::toAdminItem)
        .toList();
  }

  public FreightDetailAdminItemDTO adminCreate(
      String freightId, FreightDetailSectionType section, FreightDetailAdminCreateRequest request) {
    return toAdminItem(repository.adminCreate(sanitizeFreightId(freightId), section, request));
  }

  public FreightDetailAdminItemDTO adminUpdate(
      String freightId,
      FreightDetailSectionType section,
      String recordId,
      FreightDetailAdminUpdateRequest request) {
    return toAdminItem(
        repository.adminUpdate(sanitizeFreightId(freightId), section, recordId, request));
  }

  public FreightDetailAdminItemDTO adminChangeStatus(
      String freightId, FreightDetailSectionType section, String recordId, String status) {
    return toAdminItem(
        repository.adminChangeStatus(sanitizeFreightId(freightId), section, recordId, status));
  }

  public FreightDetailAdminItemDTO adminPin(
      String freightId, FreightDetailSectionType section, String recordId, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toAdminItem(
        repository.adminPin(sanitizeFreightId(freightId), section, recordId, pinned));
  }

  public void adminDelete(String freightId, FreightDetailSectionType section, String recordId) {
    repository.adminDelete(sanitizeFreightId(freightId), section, recordId);
  }

  private FreightDetailMainDTO toMain(FreightDetailEntity entity) {
    return new FreightDetailMainDTO(
        entity.getFreightId(),
        firstMeaningful(entity.getProvider(), entity.getTitle()),
        entity.getRoute(),
        entity.getVehicle(),
        entity.getLoadRange(),
        entity.getFrequency(),
        entity.getTimeliness(),
        entity.getQuote(),
        entity.getDescription(),
        parseTags(entity.getServiceTags()),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private FreightDetailRelatedLineDTO toRelatedLine(FreightDetailEntity entity) {
    return new FreightDetailRelatedLineDTO(
        entity.getRelatedId(),
        entity.getTitle(),
        entity.getRoute(),
        entity.getTimeliness(),
        entity.getQuote(),
        entity.getLink());
  }

  private FreightDetailRelatedDemandDTO toRelatedDemand(FreightDetailEntity entity) {
    return new FreightDetailRelatedDemandDTO(
        entity.getRelatedId(), entity.getTitle(), entity.getLink());
  }

  private FreightDetailContactDTO toContact(FreightDetailEntity entity, boolean showFullPhone) {
    String phone = entity.getContactPhone();
    String contactName = entity.getContactName();
    if (!showFullPhone) {
      phone = maskPhone(phone);
      contactName = maskName(contactName);
    }
    return new FreightDetailContactDTO(
        contactName, phone, entity.getServiceStatus(), entity.getDescription());
  }

  private FreightDetailAdminItemDTO toAdminItem(FreightDetailEntity entity) {
    String subtitle = subtitleOf(entity);
    String value = valueOf(entity);
    String extra = extraOf(entity);
    return new FreightDetailAdminItemDTO(
        entity.getId(),
        entity.getFreightId(),
        entity.getSectionType().name(),
        entity.getTitle(),
        subtitle,
        value,
        extra,
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

  private String sanitizeFreightId(String id) {
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

  private String subtitleOf(FreightDetailEntity entity) {
    return switch (entity.getSectionType()) {
      case MAIN -> firstMeaningful(entity.getRoute(), entity.getProvider());
      case SERVICE_TAG -> "服务标签";
      case RELATED_LINE -> firstMeaningful(entity.getRoute(), entity.getProvider());
      case RELATED_DEMAND -> firstMeaningful(entity.getRelatedId(), "-");
      case CONTACT -> firstMeaningful(entity.getContactName(), "-");
      case AD -> firstMeaningful(entity.getProvider(), "-");
    };
  }

  private String valueOf(FreightDetailEntity entity) {
    return switch (entity.getSectionType()) {
      case MAIN -> firstMeaningful(entity.getQuote(), "-");
      case SERVICE_TAG -> firstMeaningful(entity.getContactPhone(), entity.getTitle());
      case RELATED_LINE -> firstMeaningful(entity.getQuote(), "-");
      case RELATED_DEMAND -> firstMeaningful(entity.getTitle(), "-");
      case CONTACT -> firstMeaningful(entity.getContactPhone(), "-");
      case AD -> firstMeaningful(entity.getDescription(), "-");
    };
  }

  private String extraOf(FreightDetailEntity entity) {
    return switch (entity.getSectionType()) {
      case MAIN ->
          firstMeaningful(
              entity.getVehicle() + " / " + entity.getLoadRange() + " / " + entity.getTimeliness(), "-");
      case SERVICE_TAG -> firstMeaningful(entity.getDescription(), "-");
      case RELATED_LINE -> firstMeaningful(entity.getTimeliness(), "-");
      case RELATED_DEMAND -> firstMeaningful(entity.getLink(), "-");
      case CONTACT -> firstMeaningful(entity.getServiceStatus(), "-");
      case AD -> firstMeaningful(entity.getTitle(), "-");
    };
  }

  private String firstMeaningful(String primary, String fallback) {
    if (primary == null || primary.isBlank()) {
      return fallback;
    }
    return primary;
  }
}
