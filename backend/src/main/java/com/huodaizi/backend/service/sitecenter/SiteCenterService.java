package com.huodaizi.backend.service.sitecenter;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminCreateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminItemDTO;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdProductDTO;
import com.huodaizi.backend.dto.sitecenter.SiteCenterCityItemDTO;
import com.huodaizi.backend.dto.sitecenter.SiteCenterOverviewResponse;
import com.huodaizi.backend.dto.sitecenter.SiteCenterRegionGroupDTO;
import com.huodaizi.backend.dto.sitecenter.SiteCenterSectionType;
import com.huodaizi.backend.dto.sitecenter.SiteCenterStatDTO;
import com.huodaizi.backend.repository.sitecenter.InMemorySiteCenterRepository;
import com.huodaizi.backend.repository.sitecenter.SiteCenterEntity;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SiteCenterService {

  private final InMemorySiteCenterRepository repository;

  public SiteCenterService(InMemorySiteCenterRepository repository) {
    this.repository = repository;
  }

  public SiteCenterOverviewResponse overview() {
    List<SiteCenterStatDTO> stats =
        repository.listOnlineBySection(SiteCenterSectionType.STAT).stream()
            .map(this::toStat)
            .toList();

    Map<String, List<SiteCenterEntity>> cityByRegion =
        repository.listOnlineBySection(SiteCenterSectionType.CITY).stream()
            .collect(
                Collectors.groupingBy(
                    SiteCenterEntity::getRegion,
                    LinkedHashMap::new,
                    Collectors.toList()));
    List<SiteCenterRegionGroupDTO> regions =
        cityByRegion.entrySet().stream()
            .sorted(Comparator.comparingInt(entry -> regionOrder(entry.getKey())))
            .map(
                entry ->
                    new SiteCenterRegionGroupDTO(
                        entry.getKey(),
                        entry.getValue().stream().findFirst().map(SiteCenterEntity::getExtra).orElse("-"),
                        entry.getValue().stream().map(this::toCity).toList()))
            .toList();

    List<SiteCenterAdProductDTO> adProducts =
        repository.listOnlineBySection(SiteCenterSectionType.AD_PRODUCT).stream()
            .map(this::toAdProduct)
            .toList();

    return new SiteCenterOverviewResponse(stats, regions, adProducts);
  }

  public List<SiteCenterAdminItemDTO> adminList(SiteCenterSectionType section) {
    return repository.adminListBySection(section).stream().map(this::toAdmin).toList();
  }

  public SiteCenterAdminItemDTO adminCreate(
      SiteCenterSectionType section, SiteCenterAdminCreateRequest request) {
    return toAdmin(repository.adminCreate(section, request));
  }

  public SiteCenterAdminItemDTO adminUpdate(
      SiteCenterSectionType section, String id, SiteCenterAdminUpdateRequest request) {
    return toAdmin(repository.adminUpdate(section, id, request));
  }

  public SiteCenterAdminItemDTO adminChangeStatus(
      SiteCenterSectionType section, String id, String status) {
    return toAdmin(repository.adminChangeStatus(section, id, status));
  }

  public SiteCenterAdminItemDTO adminPin(
      SiteCenterSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toAdmin(repository.adminPin(section, id, pinned));
  }

  public void adminDelete(SiteCenterSectionType section, String id) {
    repository.adminDelete(section, id);
  }

  private SiteCenterStatDTO toStat(SiteCenterEntity entity) {
    return new SiteCenterStatDTO(entity.getTitle(), entity.getValue());
  }

  private SiteCenterCityItemDTO toCity(SiteCenterEntity entity) {
    return new SiteCenterCityItemDTO(
        entity.getTitle(),
        entity.getCitySlug(),
        entity.getSubtitle(),
        entity.getValue(),
        entity.getLink());
  }

  private SiteCenterAdProductDTO toAdProduct(SiteCenterEntity entity) {
    return new SiteCenterAdProductDTO(
        entity.getTitle(), entity.getSubtitle(), entity.getValue(), entity.getLink());
  }

  private SiteCenterAdminItemDTO toAdmin(SiteCenterEntity entity) {
    return new SiteCenterAdminItemDTO(
        entity.getId(),
        entity.getSectionType().name(),
        entity.getRegion(),
        entity.getTitle(),
        entity.getCitySlug(),
        entity.getTitle(),
        entity.getSubtitle(),
        entity.getValue(),
        entity.getExtra(),
        entity.getLink(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private int regionOrder(String region) {
    if ("华北".equals(region)) {
      return 1;
    }
    if ("华东".equals(region)) {
      return 2;
    }
    if ("华中华南".equals(region)) {
      return 3;
    }
    return 99;
  }
}
