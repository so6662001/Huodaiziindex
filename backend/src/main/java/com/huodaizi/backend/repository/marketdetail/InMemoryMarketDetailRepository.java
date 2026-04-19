package com.huodaizi.backend.repository.marketdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminCreateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.marketdetail.MarketDetailSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryMarketDetailRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, MarketDetailEntity> store = new ConcurrentHashMap<>();

  public InMemoryMarketDetailRepository() {
    seed();
  }

  public List<MarketDetailEntity> listByScopeAndSection(
      String symbol, String city, MarketDetailSectionType section, boolean onlyOnline) {
    String normalizedSymbol = normalize(symbol);
    String normalizedCity = normalize(city);
    return store.values().stream()
        .filter(item -> normalize(item.getSymbol()).equals(normalizedSymbol))
        .filter(item -> normalize(item.getCity()).equals(normalizedCity))
        .filter(item -> item.getSectionType() == section)
        .filter(item -> !onlyOnline || STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(MarketDetailEntity::isPinned).reversed()
                .thenComparing(MarketDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<MarketDetailEntity> adminList(
      String symbol, String city, MarketDetailSectionType section) {
    return listByScopeAndSection(symbol, city, section, false);
  }

  public MarketDetailEntity adminCreate(
      String symbol,
      String city,
      MarketDetailSectionType section,
      MarketDetailAdminCreateRequest request) {
    String id = "MD" + seq.incrementAndGet();
    MarketDetailEntity entity =
        new MarketDetailEntity(
            id,
            symbol,
            city,
            section,
            request.title(),
            defaultText(request.subtitle(), "-"),
            defaultText(request.value(), "-"),
            defaultText(request.trend(), "-"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public MarketDetailEntity adminUpdate(
      String symbol,
      String city,
      MarketDetailSectionType section,
      String id,
      MarketDetailAdminUpdateRequest request) {
    MarketDetailEntity entity = requireByIdAndScope(id, symbol, city, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.symbol(),
        request.city(),
        request.title(),
        request.subtitle(),
        request.value(),
        request.trend(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public MarketDetailEntity adminChangeStatus(
      String symbol, String city, MarketDetailSectionType section, String id, String status) {
    MarketDetailEntity entity = requireByIdAndScope(id, symbol, city, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public MarketDetailEntity adminPin(
      String symbol, String city, MarketDetailSectionType section, String id, boolean pinned) {
    MarketDetailEntity entity = requireByIdAndScope(id, symbol, city, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String symbol, String city, MarketDetailSectionType section, String id) {
    MarketDetailEntity entity = requireByIdAndScope(id, symbol, city, section);
    store.remove(entity.getId());
  }

  private MarketDetailEntity requireByIdAndScope(
      String id, String symbol, String city, MarketDetailSectionType section) {
    MarketDetailEntity entity = store.get(id);
    if (entity == null
        || !normalize(entity.getSymbol()).equals(normalize(symbol))
        || !normalize(entity.getCity()).equals(normalize(city))
        || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "行情详情记录不存在");
    }
    return entity;
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String normalizeStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    if (!STATUS_ONLINE.equals(normalized) && !STATUS_OFFLINE.equals(normalized)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 ONLINE/OFFLINE");
    }
    return normalized;
  }

  private void seed() {
    seedSymbolCity("rebar", "tangshan", "螺纹钢", "唐山");
    seedSymbolCity("hrc", "wuxi", "热卷", "无锡");
    seedSymbolCity("plate", "shanghai", "中厚板", "上海");
  }

  private void seedSymbolCity(String symbol, String city, String symbolName, String cityName) {
    addSeed(
        "MD-" + symbol + "-" + city + "-S1",
        symbol,
        city,
        MarketDetailSectionType.SUMMARY,
        "今日参考价",
        cityName + symbolName + "主流价",
        "3,620 元/吨",
        "+25",
        "/market/" + symbol + "/" + city,
        true,
        5);
    addSeed(
        "MD-" + symbol + "-" + city + "-S2",
        symbol,
        city,
        MarketDetailSectionType.SUMMARY,
        "近7日波动",
        "价格振幅",
        "2.9%",
        "-0.4%",
        "/market/" + symbol + "/" + city,
        false,
        6);
    addSeed(
        "MD-" + symbol + "-" + city + "-N1",
        symbol,
        city,
        MarketDetailSectionType.NEWS,
        cityName + symbolName + "现货价格窄幅上行，终端按需补库。",
        "关联资讯",
        "-",
        "-",
        "/news",
        false,
        8);
    addSeed(
        "MD-" + symbol + "-" + city + "-N2",
        symbol,
        city,
        MarketDetailSectionType.NEWS,
        "原料价格趋稳，" + symbolName + "成本端支撑增强。",
        "关联资讯",
        "-",
        "-",
        "/news",
        false,
        10);
    addSeed(
        "MD-" + symbol + "-" + city + "-R1",
        symbol,
        city,
        MarketDetailSectionType.RELATED,
        cityName + symbolName + "现货供应，库存 500 吨，支持分批提货。",
        "相关供求",
        "-",
        "-",
        "/spot",
        false,
        12);
    addSeed(
        "MD-" + symbol + "-" + city + "-R2",
        symbol,
        city,
        MarketDetailSectionType.RELATED,
        cityName + symbolName + "求购需求，采购量 300 吨，3 天内交付。",
        "相关供求",
        "-",
        "-",
        "/buy",
        false,
        13);
    addSeed(
        "MD-" + symbol + "-" + city + "-E1",
        symbol,
        city,
        MarketDetailSectionType.ACTION,
        "发布相关供应",
        "快捷入口",
        "前往现货大厅",
        "CARD",
        "/spot",
        false,
        14);
    addSeed(
        "MD-" + symbol + "-" + city + "-E2",
        symbol,
        city,
        MarketDetailSectionType.ACTION,
        "发布相关求购",
        "快捷入口",
        "前往求购大厅",
        "CARD",
        "/buy",
        false,
        14);
    addSeed(
        "MD-" + symbol + "-" + city + "-A1",
        symbol,
        city,
        MarketDetailSectionType.AD,
        "行情数据服务",
        "支持多城市、多品种订阅",
        "立即咨询",
        "广告",
        "/market",
        true,
        15);
  }

  private void addSeed(
      String id,
      String symbol,
      String city,
      MarketDetailSectionType section,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    MarketDetailEntity entity =
        new MarketDetailEntity(
            id,
            symbol,
            city,
            section,
            title,
            subtitle,
            value,
            extra,
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.put(id, entity);
  }

  private long parseNumericId(String id) {
    if (id == null) {
      return seq.get();
    }
    String digits = id.replaceAll("[^0-9]", "");
    if (digits.isBlank()) {
      return seq.get();
    }
    try {
      return Long.parseLong(digits);
    } catch (NumberFormatException ex) {
      return seq.get();
    }
  }
}
