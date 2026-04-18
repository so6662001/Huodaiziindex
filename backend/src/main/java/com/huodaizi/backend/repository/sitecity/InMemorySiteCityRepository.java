package com.huodaizi.backend.repository.sitecity;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminCreateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCityAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecity.SiteCitySectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySiteCityRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, SiteCityEntity> store = new ConcurrentHashMap<>();

  public InMemorySiteCityRepository() {
    seed();
  }

  public List<SiteCityEntity> listByCityAndSection(
      String citySlug, SiteCitySectionType section, boolean onlyOnline) {
    String normalizedCity = normalize(citySlug);
    return store.values().stream()
        .filter(item -> normalize(item.getCitySlug()).equals(normalizedCity))
        .filter(item -> item.getSectionType() == section)
        .filter(item -> !onlyOnline || STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(SiteCityEntity::isPinned).reversed()
                .thenComparing(SiteCityEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<SiteCityEntity> adminList(String citySlug, SiteCitySectionType section) {
    return listByCityAndSection(citySlug, section, false);
  }

  public SiteCityEntity adminCreate(
      String citySlug, SiteCitySectionType section, SiteCityAdminCreateRequest request) {
    String id = "SC" + seq.incrementAndGet();
    SiteCityEntity entity =
        new SiteCityEntity(
            id,
            citySlug,
            section,
            request.title(),
            defaultText(request.subtitle(), "-"),
            defaultText(request.value(), "-"),
            defaultText(request.extra(), "-"),
            defaultText(request.link(), "#"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public SiteCityEntity adminUpdate(
      String citySlug, SiteCitySectionType section, String id, SiteCityAdminUpdateRequest request) {
    SiteCityEntity entity = requireByIdAndScope(citySlug, section, id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.subtitle(),
        request.value(),
        request.extra(),
        request.link(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public SiteCityEntity adminChangeStatus(
      String citySlug, SiteCitySectionType section, String id, String status) {
    SiteCityEntity entity = requireByIdAndScope(citySlug, section, id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public SiteCityEntity adminPin(
      String citySlug, SiteCitySectionType section, String id, boolean pinned) {
    SiteCityEntity entity = requireByIdAndScope(citySlug, section, id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String citySlug, SiteCitySectionType section, String id) {
    SiteCityEntity entity = requireByIdAndScope(citySlug, section, id);
    store.remove(entity.getId());
  }

  private SiteCityEntity requireByIdAndScope(String citySlug, SiteCitySectionType section, String id) {
    SiteCityEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分站记录不存在");
    }
    if (!normalize(entity.getCitySlug()).equals(normalize(citySlug)) || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分站记录不存在");
    }
    return entity;
  }

  public String cityName(String citySlug) {
    return switch (normalize(citySlug)) {
      case "tangshan" -> "唐山";
      case "wuxi" -> "无锡";
      case "foshan" -> "佛山";
      case "wuhan" -> "武汉";
      case "zhengzhou" -> "郑州";
      case "chengdu" -> "成都";
      default -> citySlug;
    };
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
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

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private void seed() {
    seedCity("tangshan", "唐山", "TS");
    seedCity("wuxi", "无锡", "WX");
    seedCity("foshan", "佛山", "FS");
    seedCity("wuhan", "武汉", "WH");
  }

  private void seedCity(String citySlug, String cityName, String cityCode) {
    addSeed(
        "SC" + cityCode + "M01",
        citySlug,
        SiteCitySectionType.MARKET,
        "螺纹钢",
        "本地现货主流价",
        "3620元/吨",
        "+25",
        "/market/rebar/" + citySlug,
        true,
        6);
    addSeed(
        "SC" + cityCode + "M02",
        citySlug,
        SiteCitySectionType.MARKET,
        "热卷",
        "本地现货主流价",
        "3780元/吨",
        "-10",
        "/market/hot/" + citySlug,
        false,
        8);

    addSeed(
        "SC" + cityCode + "S01",
        citySlug,
        SiteCitySectionType.SPOT,
        cityName + "螺纹钢 HRB400E 12-25mm",
        "现货 420吨",
        "可当天提货",
        "-",
        "/spot",
        true,
        10);
    addSeed(
        "SC" + cityCode + "B01",
        citySlug,
        SiteCitySectionType.BUY,
        "求购" + cityName + "热卷 Q235B",
        "需求 260吨",
        "现货优先",
        "-",
        "/buy",
        false,
        12);
    addSeed(
        "SC" + cityCode + "L01",
        citySlug,
        SiteCitySectionType.LOGISTICS,
        cityName + "海港仓储中心",
        "室内库 / 20吨行车",
        "日吞吐1200吨",
        "-",
        "/logistics",
        false,
        14);
    addSeed(
        "SC" + cityCode + "C01",
        citySlug,
        SiteCitySectionType.COMPANY,
        cityName + "宏信钢贸有限公司",
        "主营钢材贸易",
        "供应链服务",
        "-",
        "/site/" + citySlug,
        false,
        16);
    addSeed(
        "SC" + cityCode + "A01",
        citySlug,
        SiteCitySectionType.AD,
        cityName + "分站品牌推广位",
        "分站焦点位",
        "支持品类与时段定向",
        "广告",
        "/site/ad?city=" + citySlug,
        true,
        18);
  }

  private void addSeed(
      String id,
      String city,
      SiteCitySectionType section,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    SiteCityEntity entity =
        new SiteCityEntity(
            id,
            city,
            section,
            title,
            subtitle,
            value,
            extra,
            link,
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
