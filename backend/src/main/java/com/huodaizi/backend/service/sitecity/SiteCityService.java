package com.huodaizi.backend.service.sitecity;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminCreateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCityMarketItemDTO;
import com.huodaizi.backend.dto.sitecity.SiteCityOverviewResponse;
import com.huodaizi.backend.dto.sitecity.SiteCitySectionType;
import com.huodaizi.backend.dto.sitecity.SiteCitySimpleItemDTO;
import com.huodaizi.backend.repository.sitecity.InMemorySiteCityRepository;
import com.huodaizi.backend.repository.sitecity.SiteCityEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SiteCityService {

  private final InMemorySiteCityRepository repository;

  public SiteCityService(InMemorySiteCityRepository repository) {
    this.repository = repository;
  }

  public SiteCityOverviewResponse overview(String city) {
    return new SiteCityOverviewResponse(
        city,
        cityName(city),
        cityCode(city),
        marketItems(city),
        simpleItems(city, SiteCitySectionType.SPOT),
        simpleItems(city, SiteCitySectionType.BUY),
        simpleItems(city, SiteCitySectionType.LOGISTICS),
        simpleItems(city, SiteCitySectionType.COMPANY),
        simpleItems(city, SiteCitySectionType.AD));
  }

  public List<SiteCityEntity> adminList(String city, SiteCitySectionType section) {
    return repository.adminList(city, section);
  }

  public SiteCityEntity adminCreate(String city, SiteCitySectionType section, SiteCityAdminCreateRequest request) {
    return repository.adminCreate(city, section, request);
  }

  public SiteCityEntity adminUpdate(
      String city, SiteCitySectionType section, String id, SiteCityAdminUpdateRequest request) {
    return repository.adminUpdate(city, section, id, request);
  }

  public SiteCityEntity adminChangeStatus(String city, SiteCitySectionType section, String id, String status) {
    return repository.adminChangeStatus(city, section, id, status);
  }

  public SiteCityEntity adminPin(String city, SiteCitySectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return repository.adminPin(city, section, id, pinned);
  }

  public void adminDelete(String city, SiteCitySectionType section, String id) {
    repository.adminDelete(city, section, id);
  }

  private String cityName(String city) {
    return switch (city.toLowerCase()) {
      case "tangshan", "唐山" -> "唐山";
      case "wuxi", "无锡" -> "无锡";
      case "foshan", "佛山" -> "佛山";
      case "wuhan", "武汉" -> "武汉";
      case "zhengzhou", "郑州" -> "郑州";
      case "chengdu", "成都" -> "成都";
      default -> city;
    };
  }

  private String cityCode(String city) {
    return switch (city.toLowerCase()) {
      case "tangshan", "唐山" -> "TS";
      case "wuxi", "无锡" -> "WX";
      case "foshan", "佛山" -> "FS";
      case "wuhan", "武汉" -> "WH";
      case "zhengzhou", "郑州" -> "ZZ";
      case "chengdu", "成都" -> "CD";
      default -> "CITY";
    };
  }

  private List<SiteCityMarketItemDTO> marketItems(String city) {
    return repository
        .listByCityAndSection(city, SiteCitySectionType.MARKET, true)
        .stream()
        .map(this::toMarket)
        .toList();
  }

  private List<SiteCitySimpleItemDTO> simpleItems(String city, SiteCitySectionType type) {
    return repository
        .listByCityAndSection(city, type, true)
        .stream()
        .map(this::toSimple)
        .toList();
  }

  private SiteCityMarketItemDTO toMarket(SiteCityEntity entity) {
    return new SiteCityMarketItemDTO(
        entity.getId(),
        entity.getTitle(),
        defaultText(entity.getSubtitle(), "-"),
        defaultText(entity.getValue(), "-"),
        defaultText(entity.getExtra(), "0"),
        entity.getUpdatedAt().toString());
  }

  private SiteCitySimpleItemDTO toSimple(SiteCityEntity entity) {
    return new SiteCitySimpleItemDTO(
        entity.getId(),
        entity.getTitle(),
        defaultText(entity.getSubtitle(), defaultText(entity.getValue(), "-")),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }
}
