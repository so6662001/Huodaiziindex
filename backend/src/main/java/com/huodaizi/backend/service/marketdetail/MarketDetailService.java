package com.huodaizi.backend.service.marketdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminCreateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailItemDTO;
import com.huodaizi.backend.dto.marketdetail.MarketDetailRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailResponse;
import com.huodaizi.backend.dto.marketdetail.MarketDetailSectionType;
import com.huodaizi.backend.dto.marketdetail.MarketDetailSummaryDTO;
import com.huodaizi.backend.repository.marketdetail.InMemoryMarketDetailRepository;
import com.huodaizi.backend.repository.marketdetail.MarketDetailEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarketDetailService {

  private final InMemoryMarketDetailRepository repository;

  public MarketDetailService(InMemoryMarketDetailRepository repository) {
    this.repository = repository;
  }

  public MarketDetailResponse detail(MarketDetailRequest request) {
    String symbol = fallback(request.symbol(), "rebar");
    String city = fallback(request.city(), "tangshan");
    List<MarketDetailEntity> summaryEntities =
        repository.listByScopeAndSection(symbol, city, MarketDetailSectionType.SUMMARY, true);
    if (summaryEntities.isEmpty()) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "行情详情不存在");
    }
    MarketDetailSummaryDTO summary = toSummary(summaryEntities);
    List<String> relatedNews =
        repository.listByScopeAndSection(symbol, city, MarketDetailSectionType.NEWS, true).stream()
            .map(MarketDetailEntity::getTitle)
            .toList();
    List<String> relatedSupply =
        repository.listByScopeAndSection(symbol, city, MarketDetailSectionType.RELATED, true).stream()
            .map(MarketDetailEntity::getTitle)
            .toList();
    List<MarketDetailItemDTO> actions =
        repository.listByScopeAndSection(symbol, city, MarketDetailSectionType.ACTION, true).stream()
            .map(this::toItem)
            .toList();
    List<MarketDetailItemDTO> ads =
        repository.listByScopeAndSection(symbol, city, MarketDetailSectionType.AD, true).stream()
            .map(this::toItem)
            .toList();
    return new MarketDetailResponse(
        symbol,
        city,
        cityName(city) + symbolName(symbol) + "价格走势",
        summaryCards(summary),
        relatedNews,
        relatedSupply,
        actions,
        ads);
  }

  public List<MarketDetailEntity> adminList(
      String symbol, String city, MarketDetailSectionType section) {
    return repository.adminList(symbol, city, section);
  }

  public MarketDetailEntity adminCreate(
      String symbol,
      String city,
      MarketDetailSectionType section,
      MarketDetailAdminCreateRequest request) {
    return repository.adminCreate(symbol, city, section, request);
  }

  public MarketDetailEntity adminUpdate(
      String symbol,
      String city,
      MarketDetailSectionType section,
      String id,
      MarketDetailAdminUpdateRequest request) {
    return repository.adminUpdate(symbol, city, section, id, request);
  }

  public MarketDetailEntity adminChangeStatus(
      String symbol, String city, MarketDetailSectionType section, String id, String status) {
    return repository.adminChangeStatus(symbol, city, section, id, status);
  }

  public MarketDetailEntity adminPin(
      String symbol, String city, MarketDetailSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return repository.adminPin(symbol, city, section, id, pinned);
  }

  public void adminDelete(String symbol, String city, MarketDetailSectionType section, String id) {
    repository.adminDelete(symbol, city, section, id);
  }

  private MarketDetailSummaryDTO toSummary(List<MarketDetailEntity> summaryEntities) {
    MarketDetailEntity chart =
        summaryEntities.stream()
            .filter(entity -> "CHART".equalsIgnoreCase(fallback(entity.getTrend(), "")))
            .findFirst()
            .orElse(summaryEntities.get(0));
    return new MarketDetailSummaryDTO(
        chart.getTitle(),
        fallback(chart.getValue(), "-"),
        fallback(chart.getTrend(), "0"));
  }

  private MarketDetailItemDTO toItem(MarketDetailEntity entity) {
    return new MarketDetailItemDTO(
        entity.getId(),
        entity.getTitle(),
        entity.getSubtitle(),
        entity.getValue(),
        fallback(entity.getTrend(), "-"));
  }

  private List<MarketDetailSummaryDTO> summaryCards(MarketDetailSummaryDTO firstSummary) {
    return List.of(
        firstSummary,
        new MarketDetailSummaryDTO("近7日波动", "2.9%", "-0.4%"),
        new MarketDetailSummaryDTO("近30日区间", "3,510 - 3,780", "+130"));
  }

  private String symbolName(String symbol) {
    return switch (symbol.toLowerCase()) {
      case "rebar" -> "螺纹钢";
      case "hrc" -> "热卷";
      case "plate" -> "中厚板";
      case "section" -> "型钢";
      default -> "钢材品种";
    };
  }

  private String cityName(String city) {
    return switch (city.toLowerCase()) {
      case "tangshan" -> "唐山";
      case "wuxi" -> "无锡";
      case "shanghai" -> "上海";
      case "tianjin" -> "天津";
      case "wuhan" -> "武汉";
      case "foshan" -> "佛山";
      default -> city;
    };
  }

  private String fallback(String value, String defaultText) {
    return value == null || value.isBlank() ? defaultText : value;
  }
}
