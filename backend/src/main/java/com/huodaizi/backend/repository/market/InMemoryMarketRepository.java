package com.huodaizi.backend.repository.market;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.market.MarketAdminCreateRequest;
import com.huodaizi.backend.dto.market.MarketAdminUpdateRequest;
import com.huodaizi.backend.dto.market.MarketOverviewRequest;
import com.huodaizi.backend.dto.market.MarketSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryMarketRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, MarketEntity> store = new ConcurrentHashMap<>();

  public InMemoryMarketRepository() {
    seed();
  }

  public List<MarketEntity> listOverviewItems(
      String categoryFilter, String cityFilter, String rangeFilter, MarketSectionType section) {
    String category = normalize(categoryFilter);
    String city = normalize(cityFilter);
    String range = normalize(rangeFilter);
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                category.isBlank()
                    || "全部品类".equals(category)
                    || normalize(item.getCategory()).contains(category))
        .filter(
            item ->
                city.isBlank() || "全部城市".equals(city) || normalize(item.getCity()).contains(city))
        .filter(item -> matchRange(item, range))
        .sorted(
            Comparator.comparing(MarketEntity::isPinned).reversed()
                .thenComparing(MarketEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<MarketEntity> listOverviewItems(MarketOverviewRequest request, MarketSectionType section) {
    return listOverviewItems(request.category(), request.city(), request.range(), section);
  }

  public List<MarketEntity> adminList(MarketSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(MarketEntity::isPinned).reversed()
                .thenComparing(MarketEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public MarketEntity adminCreate(MarketSectionType section, MarketAdminCreateRequest request) {
    String id = "MK" + seq.incrementAndGet();
    MarketEntity entity =
        new MarketEntity(
            id,
            section,
            defaultText(request.category(), "螺纹钢"),
            defaultText(request.city(), "唐山"),
            defaultText(request.rangeTag(), "7日"),
            request.title(),
            defaultText(request.subtitle(), "-"),
            defaultText(request.price(), "-"),
            defaultText(request.highPrice(), "-"),
            defaultText(request.lowPrice(), "-"),
            defaultText(request.changeValue(), "0"),
            defaultText(request.tag(), "-"),
            defaultText(request.link(), "#"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public MarketEntity adminUpdate(MarketSectionType section, String id, MarketAdminUpdateRequest request) {
    MarketEntity entity = requireByIdAndSection(id, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.category(),
        request.city(),
        request.rangeTag(),
        request.title(),
        request.subtitle(),
        request.price(),
        request.highPrice(),
        request.lowPrice(),
        request.changeValue(),
        request.tag(),
        request.link(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public MarketEntity adminChangeStatus(MarketSectionType section, String id, String status) {
    MarketEntity entity = requireByIdAndSection(id, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public MarketEntity adminPin(MarketSectionType section, String id, boolean pinned) {
    MarketEntity entity = requireByIdAndSection(id, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(MarketSectionType section, String id) {
    MarketEntity entity = requireByIdAndSection(id, section);
    store.remove(entity.getId());
  }

  private MarketEntity requireByIdAndSection(String id, MarketSectionType section) {
    MarketEntity entity = store.get(id);
    if (entity == null || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "行情记录不存在");
    }
    return entity;
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String defaultTextOrDash(String text) {
    return text == null || text.isBlank() ? "-" : text;
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

  private boolean matchRange(MarketEntity entity, String range) {
    if (range.isBlank() || "7日".equals(range)) {
      return true;
    }
    return normalize(entity.getRangeTag()).contains(range);
  }

  private void seed() {
    addSeed(
        "MK20260418001",
        MarketSectionType.SNAPSHOT,
        "螺纹钢",
        "唐山",
        "螺纹钢 HRB400E",
        "主流成交价",
        "3620",
        "3678",
        "3592",
        "+25",
        "快照",
        true,
        8);
    addSeed(
        "MK20260418002",
        MarketSectionType.SNAPSHOT,
        "热卷",
        "无锡",
        "热卷 Q235B 4.75",
        "主流成交价",
        "3780",
        "3826",
        "3752",
        "-15",
        "快照",
        false,
        10);
    addSeed(
        "MK20260418003",
        MarketSectionType.SNAPSHOT,
        "中厚板",
        "上海",
        "中厚板 Q355B 20mm",
        "主流成交价",
        "3950",
        "3980",
        "3916",
        "+10",
        "快照",
        false,
        12);
    addSeed(
        "MK20260418004",
        MarketSectionType.SNAPSHOT,
        "型钢",
        "天津",
        "H型钢 200*200",
        "主流成交价",
        "3730",
        "3748",
        "3705",
        "+8",
        "快照",
        false,
        13);
    addSeed(
        "MK20260418005",
        MarketSectionType.QUOTE_PANEL,
        "螺纹钢",
        "唐山",
        "今日参考价",
        "市场主流价",
        "3620 元/吨",
        "-",
        "-",
        "+25",
        "看涨",
        true,
        6);
    addSeed(
        "MK20260418006",
        MarketSectionType.QUOTE_PANEL,
        "螺纹钢",
        "唐山",
        "近7日波动",
        "价格振幅",
        "2.9%",
        "-",
        "-",
        "-0.4%",
        "震荡",
        false,
        7);
    addSeed(
        "MK20260418007",
        MarketSectionType.QUOTE_PANEL,
        "螺纹钢",
        "唐山",
        "近30日区间",
        "价格区间",
        "3510 - 3780",
        "-",
        "-",
        "+130",
        "区间",
        false,
        9);
    addSeed(
        "MK20260418008",
        MarketSectionType.INSIGHT,
        "螺纹钢",
        "唐山",
        "市场解读",
        "观察",
        "北方建材成交回暖，螺纹现货价格小幅走强。",
        "-",
        "-",
        "+",
        "解读",
        false,
        12);
    addSeed(
        "MK20260418009",
        MarketSectionType.INSIGHT,
        "热卷",
        "无锡",
        "华东热卷维持窄幅震荡，终端按需采购为主。",
        "市场解读",
        "-",
        "-",
        "-",
        "-",
        "解读",
        false,
        14);
    addSeed(
        "MK20260418010",
        MarketSectionType.INSIGHT,
        "综合",
        "全国",
        "部分区域运输成本回落，跨区调货意愿提升。",
        "市场解读",
        "-",
        "-",
        "-",
        "-",
        "解读",
        false,
        16);
    addSeed(
        "MK20260418011",
        MarketSectionType.SIGNAL,
        "螺纹钢",
        "唐山",
        "库存信号",
        "社会库存周降 2.1%",
        "偏多",
        "-",
        "-",
        "偏多",
        "策略",
        false,
        14);
    addSeed(
        "MK20260418012",
        MarketSectionType.SIGNAL,
        "综合",
        "全国",
        "需求信号",
        "工地开工率稳步恢复",
        "中性偏多",
        "-",
        "-",
        "中性偏多",
        "策略",
        false,
        16);
    addSeed(
        "MK20260418013",
        MarketSectionType.SIGNAL,
        "综合",
        "全国",
        "成本信号",
        "焦炭价格趋稳",
        "中性",
        "-",
        "-",
        "中性",
        "策略",
        false,
        18);
  }

  private void addSeed(
      String id,
      MarketSectionType section,
      String category,
      String city,
      String title,
      String subtitle,
      String value,
      String highPrice,
      String lowPrice,
      String extra,
      String tag,
      boolean pinned,
      long minusMinutes) {
    String rangeTag = inferRangeTag(title);
    String normalizedValue = section == MarketSectionType.INSIGHT ? defaultTextOrDash(value) : value;
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    MarketEntity entity =
        new MarketEntity(
            id,
            section,
            category,
            city,
            rangeTag,
            title,
            subtitle,
            normalizedValue,
            highPrice,
            lowPrice,
            extra,
            tag,
            "#",
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.put(id, entity);
  }

  private String inferRangeTag(String title) {
    String normalizedTitle = normalize(title);
    if (normalizedTitle.contains("30日")) {
      return "30日";
    }
    if (normalizedTitle.contains("90日")) {
      return "90日";
    }
    return "7日";
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
