package com.huodaizi.backend.repository.sitecenter;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminCreateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterAdminUpdateRequest;
import com.huodaizi.backend.dto.sitecenter.SiteCenterSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySiteCenterRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, SiteCenterEntity> store = new ConcurrentHashMap<>();

  public InMemorySiteCenterRepository() {
    seed();
  }

  public List<SiteCenterEntity> listOnlineBySection(SiteCenterSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(SiteCenterEntity::isPinned).reversed()
                .thenComparing(SiteCenterEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<SiteCenterEntity> adminListBySection(SiteCenterSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(SiteCenterEntity::isPinned).reversed()
                .thenComparing(SiteCenterEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SiteCenterEntity adminCreate(SiteCenterSectionType section, SiteCenterAdminCreateRequest request) {
    String id = "SC" + seq.incrementAndGet();
    String status = request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status());

    String region = defaultText(request.region(), "-");
    String citySlug = defaultText(request.citySlug(), "-");
    String title = request.title();
    String subtitle = defaultText(request.subtitle(), "-");
    String value = defaultText(request.value(), "-");
    String extra = defaultText(request.extra(), "-");
    String link = defaultText(request.link(), "#");

    if (section == SiteCenterSectionType.CITY) {
      title = defaultText(request.cityName(), request.title());
      subtitle = defaultText(request.focus(), "-");
      value = defaultText(request.update(), "-");
      String generatedSlug = toSlug(title);
      if (generatedSlug.isBlank()) {
        generatedSlug = "city-" + id.toLowerCase(Locale.ROOT);
      }
      citySlug = defaultText(request.citySlug(), generatedSlug);
      extra = defaultText(request.extra(), "-");
      link = defaultText(request.link(), "/site/" + citySlug);
    }

    SiteCenterEntity entity =
        new SiteCenterEntity(
            id,
            section,
            region,
            citySlug,
            title,
            subtitle,
            value,
            extra,
            link,
            status,
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public SiteCenterEntity adminUpdate(
      SiteCenterSectionType section, String id, SiteCenterAdminUpdateRequest request) {
    SiteCenterEntity entity = requireByIdAndSection(id, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String region = request.region();
    String citySlug = request.citySlug();
    String title = request.title();
    String subtitle = request.subtitle();
    String value = request.value();
    String extra = request.extra();
    String link = request.link();
    if (section == SiteCenterSectionType.CITY) {
      title = request.cityName() != null ? request.cityName() : title;
      subtitle = request.focus() != null ? request.focus() : subtitle;
      value = request.update() != null ? request.update() : value;
      citySlug = request.citySlug() != null ? request.citySlug() : citySlug;
    }
    entity.update(
        region, citySlug, title, subtitle, value, extra, link, normalizedStatus, request.pinned());
    return entity;
  }

  public SiteCenterEntity adminChangeStatus(SiteCenterSectionType section, String id, String status) {
    SiteCenterEntity entity = requireByIdAndSection(id, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public SiteCenterEntity adminPin(SiteCenterSectionType section, String id, boolean pinned) {
    SiteCenterEntity entity = requireByIdAndSection(id, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(SiteCenterSectionType section, String id) {
    SiteCenterEntity entity = requireByIdAndSection(id, section);
    store.remove(entity.getId());
  }

  private SiteCenterEntity requireByIdAndSection(String id, SiteCenterSectionType section) {
    SiteCenterEntity entity = store.get(id);
    if (entity == null || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分站中心记录不存在");
    }
    return entity;
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
    seedStat("已开通城市分站", "42", true, 8);
    seedStat("当日新增供求", "1,286", false, 7);
    seedStat("分站入驻企业", "9,600+", false, 6);
    seedStat("可投放广告位", "320", false, 5);

    seedCity("华北", "建材与型钢流通活跃，港口与园区联动强。", "唐山", "tangshan", "螺纹钢 / 型钢", "今日新增 126 条供求", false, 5);
    seedCity("华北", "建材与型钢流通活跃，港口与园区联动强。", "天津", "tianjin", "热卷 / 板材", "今日新增 92 条供求", false, 4);
    seedCity("华北", "建材与型钢流通活跃，港口与园区联动强。", "邯郸", "handan", "中厚板 / 螺纹钢", "今日新增 81 条供求", false, 3);
    seedCity("华东", "终端加工与贸易密度高，仓配需求集中。", "无锡", "wuxi", "热卷 / 中厚板", "今日新增 138 条供求", false, 5);
    seedCity("华东", "终端加工与贸易密度高，仓配需求集中。", "南京", "nanjing", "建材 / 型钢", "今日新增 74 条供求", false, 4);
    seedCity("华东", "终端加工与贸易密度高，仓配需求集中。", "上海", "shanghai", "板材 / 管材", "今日新增 67 条供求", false, 3);
    seedCity("华中华南", "项目需求与区域配送协同明显，物流线路密集。", "武汉", "wuhan", "螺纹钢 / 中厚板", "今日新增 96 条供求", false, 5);
    seedCity("华中华南", "项目需求与区域配送协同明显，物流线路密集。", "佛山", "foshan", "镀锌卷 / 管材", "今日新增 118 条供求", false, 4);
    seedCity("华中华南", "项目需求与区域配送协同明显，物流线路密集。", "郑州", "zhengzhou", "建材 / 型钢", "今日新增 83 条供求", false, 3);

    seedAdProduct("分站首页焦点位", "适合品牌曝光与招商推广，支持按城市、品类定向。", "3000元/月起", true, 2);
    seedAdProduct("分站信息流广告位", "融入供求与资讯流，提升点击与询盘转化。", "120元/天起", false, 2);
    seedAdProduct("仓储物流推荐位", "面向仓库与车队专线，按线路与车型精准触达。", "1800元/月起", false, 1);
  }

  private void seedStat(String label, String value, boolean pinned, long minusMinutes) {
    addSeed(
        "SC" + seq.incrementAndGet(),
        SiteCenterSectionType.STAT,
        "-",
        "-",
        label,
        "-",
        value,
        "-",
        "#",
        pinned,
        minusMinutes);
  }

  private void seedCity(
      String region,
      String regionDesc,
      String cityName,
      String slug,
      String focus,
      String update,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "SC" + seq.incrementAndGet(),
        SiteCenterSectionType.CITY,
        region,
        slug,
        cityName,
        focus,
        update,
        regionDesc,
        "/site/" + slug,
        pinned,
        minusMinutes);
  }

  private void seedAdProduct(
      String title, String description, String price, boolean pinned, long minusMinutes) {
    addSeed(
        "SC" + seq.incrementAndGet(),
        SiteCenterSectionType.AD_PRODUCT,
        "-",
        "-",
        title,
        description,
        price,
        "-",
        "#",
        pinned,
        minusMinutes);
  }

  private void addSeed(
      String id,
      SiteCenterSectionType section,
      String region,
      String citySlug,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      boolean pinned,
      long minusMinutes) {
    SiteCenterEntity entity =
        new SiteCenterEntity(
            id,
            section,
            region,
            citySlug,
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

  private String toSlug(String text) {
    return text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
  }
}
