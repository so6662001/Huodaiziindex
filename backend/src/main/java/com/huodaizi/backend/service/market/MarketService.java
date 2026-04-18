package com.huodaizi.backend.service.market;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.market.MarketAdminCreateRequest;
import com.huodaizi.backend.dto.market.MarketAdminUpdateRequest;
import com.huodaizi.backend.dto.market.MarketCategoryQuoteDTO;
import com.huodaizi.backend.dto.market.MarketOverviewRequest;
import com.huodaizi.backend.dto.market.MarketOverviewResponse;
import com.huodaizi.backend.dto.market.MarketQuotePanelDTO;
import com.huodaizi.backend.dto.market.MarketSectionType;
import com.huodaizi.backend.dto.market.MarketSignalItemDTO;
import com.huodaizi.backend.dto.market.MarketSnapshotItemDTO;
import com.huodaizi.backend.repository.market.InMemoryMarketRepository;
import com.huodaizi.backend.repository.market.MarketEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarketService {

  private final InMemoryMarketRepository repository;

  public MarketService(InMemoryMarketRepository repository) {
    this.repository = repository;
  }

  public MarketOverviewResponse overview(MarketOverviewRequest request) {
    String category = fallback(request.category(), "螺纹钢");
    String city = fallback(request.city(), "唐山");
    String range = fallback(request.range(), "7日");
    return new MarketOverviewResponse(
        category,
        city,
        range,
        categoryQuotes(),
        quotePanels(category, city, range),
        snapshots(),
        signals(),
        insights());
  }

  public List<MarketEntity> adminList(MarketSectionType section) {
    return repository.adminList(section);
  }

  public MarketEntity adminCreate(MarketSectionType section, MarketAdminCreateRequest request) {
    return repository.adminCreate(section, request);
  }

  public MarketEntity adminUpdate(
      MarketSectionType section, String id, MarketAdminUpdateRequest request) {
    return repository.adminUpdate(section, id, request);
  }

  public MarketEntity adminChangeStatus(MarketSectionType section, String id, String status) {
    return repository.adminChangeStatus(section, id, status);
  }

  public MarketEntity adminPin(MarketSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return repository.adminPin(section, id, pinned);
  }

  public void adminDelete(MarketSectionType section, String id) {
    repository.adminDelete(section, id);
  }

  private List<MarketCategoryQuoteDTO> categoryQuotes() {
    return repository.adminList(MarketSectionType.SNAPSHOT).stream()
        .filter(item -> "ONLINE".equals(item.getStatus()))
        .map(
            item ->
                new MarketCategoryQuoteDTO(
                    item.getId(),
                    item.getCategory(),
                    item.getCity(),
                    fallback(item.getPrice(), "0"),
                    fallback(item.getChangeValue(), "0"),
                    item.getUpdatedAt().toString()))
        .toList();
  }

  private List<MarketQuotePanelDTO> quotePanels(String category, String city, String range) {
    return repository.listOverviewItems(category, city, range, MarketSectionType.QUOTE_PANEL)
        .stream()
        .map(
            item ->
                new MarketQuotePanelDTO(
                    item.getId(),
                    item.getTitle(),
                    fallback(item.getPrice(), "-"),
                    fallback(item.getChangeValue(), "0")))
        .toList();
  }

  private List<MarketSnapshotItemDTO> snapshots() {
    return repository.adminList(MarketSectionType.SNAPSHOT).stream()
        .filter(item -> "ONLINE".equals(item.getStatus()))
        .map(
            item ->
                new MarketSnapshotItemDTO(
                    item.getId(),
                    item.getTitle(),
                    item.getCity(),
                    fallback(item.getPrice(), "-"),
                    fallback(item.getHighPrice(), "-"),
                    fallback(item.getLowPrice(), "-"),
                    fallback(item.getChangeValue(), "0")))
        .toList();
  }

  private List<MarketSignalItemDTO> signals() {
    return repository.adminList(MarketSectionType.SIGNAL).stream()
        .filter(item -> "ONLINE".equals(item.getStatus()))
        .map(
            item ->
                new MarketSignalItemDTO(
                    item.getId(),
                    item.getTitle(),
                    fallback(item.getSubtitle(), "-"),
                    fallback(item.getTag(), "-")))
        .toList();
  }

  private List<String> insights() {
    return repository.adminList(MarketSectionType.INSIGHT).stream()
        .filter(item -> "ONLINE".equals(item.getStatus()))
        .map(MarketEntity::getTitle)
        .toList();
  }

  private String fallback(String value, String defaultText) {
    return value == null || value.isBlank() ? defaultText : value;
  }
}
