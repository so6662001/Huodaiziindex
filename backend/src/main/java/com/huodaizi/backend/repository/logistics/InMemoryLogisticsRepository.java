package com.huodaizi.backend.repository.logistics;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.logistics.LogisticsAdminCreateRequest;
import com.huodaizi.backend.dto.logistics.LogisticsSectionUpdateRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLogisticsRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<LogisticsSectionType, ConcurrentMap<String, LogisticsSectionEntity>> store =
      new ConcurrentHashMap<>();

  public InMemoryLogisticsRepository() {
    for (LogisticsSectionType type : LogisticsSectionType.values()) {
      store.put(type, new ConcurrentHashMap<>());
    }
    seed();
  }

  public List<LogisticsSectionEntity> listByType(LogisticsSectionType type) {
    return listByType(type, false);
  }

  public List<LogisticsSectionEntity> listByType(LogisticsSectionType type, boolean onlyOnline) {
    return store.get(type).values().stream()
        .filter(item -> !onlyOnline || STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(LogisticsSectionEntity::isPinned).reversed()
                .thenComparing(LogisticsSectionEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public LogisticsSectionEntity firstByType(LogisticsSectionType type) {
    return listByType(type, true).stream()
        .findFirst()
        .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND.getCode(), type + " 数据为空"));
  }

  public List<LogisticsSectionEntity> search(String city, String type, String keyword) {
    String cityKey = normalize(city);
    String typeKey = normalize(type);
    String keywordKey = normalize(keyword);
    return store.values().stream()
        .flatMap(m -> m.values().stream())
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> cityKey.isBlank() || normalize(item.getCity()).contains(cityKey))
        .filter(
            item ->
                typeKey.isBlank()
                    || normalize(item.getType().name()).contains(typeKey)
                    || normalize(item.getType().sectionName()).contains(typeKey))
        .filter(
            item ->
                keywordKey.isBlank()
                    || normalize(item.getTitle()).contains(keywordKey)
                    || normalize(item.getSubtitle()).contains(keywordKey)
                    || normalize(item.getExtra()).contains(keywordKey))
        .sorted(
            Comparator.comparing(LogisticsSectionEntity::isPinned).reversed()
                .thenComparing(LogisticsSectionEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public LogisticsSectionEntity create(LogisticsSectionType type, LogisticsAdminCreateRequest request) {
    String id = type.prefix() + seq.incrementAndGet();
    String status = request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status());
    LogisticsSectionEntity entity =
        new LogisticsSectionEntity(
            id,
            type,
            request.title(),
            request.subtitle(),
            defaultText(request.city(), "全国"),
            defaultText(request.value(), "-"),
            defaultText(request.extra(), "-"),
            request.link(),
            request.content(),
            status,
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());
    store.get(type).put(id, entity);
    return entity;
  }

  public LogisticsSectionEntity update(
      LogisticsSectionType type, String id, LogisticsSectionUpdateRequest request) {
    LogisticsSectionEntity entity = requireByTypeAndId(type, id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.subtitle(),
        request.city(),
        request.value(),
        request.extra(),
        request.link(),
        request.content(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public LogisticsSectionEntity changeStatus(LogisticsSectionType type, String id, String status) {
    LogisticsSectionEntity entity = requireByTypeAndId(type, id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public LogisticsSectionEntity pin(LogisticsSectionType type, String id, boolean pinned) {
    LogisticsSectionEntity entity = requireByTypeAndId(type, id);
    entity.setPinned(pinned);
    return entity;
  }

  public void delete(LogisticsSectionType type, String id) {
    LogisticsSectionEntity removed = store.get(type).remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储物流记录不存在");
    }
  }

  private LogisticsSectionEntity requireByTypeAndId(LogisticsSectionType type, String id) {
    LogisticsSectionEntity entity = store.get(type).get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "未找到记录: " + type.name() + " / " + id);
    }
    return entity;
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
    seedEntity(
        LogisticsSectionType.QUICK_ENTRY,
        "QK2026041801",
        "发仓储需求",
        "快速匹配本地仓库",
        "全国",
        "立即发布",
        "-",
        "/logistics/demand/storage/new",
        "快捷入口",
        STATUS_ONLINE,
        true,
        1);
    seedEntity(
        LogisticsSectionType.QUICK_ENTRY,
        "QK2026041802",
        "发运输需求",
        "对接车队与专线",
        "全国",
        "立即发布",
        "-",
        "/logistics/demand/freight/new",
        "快捷入口",
        STATUS_ONLINE,
        false,
        2);
    seedEntity(
        LogisticsSectionType.QUICK_ENTRY,
        "QK2026041803",
        "找仓库",
        "按城市和库型筛选",
        "全国",
        "立即查找",
        "-",
        "/logistics/warehouse",
        "快捷入口",
        STATUS_ONLINE,
        false,
        3);
    seedEntity(
        LogisticsSectionType.QUICK_ENTRY,
        "QK2026041804",
        "找车找线",
        "按线路快速询价",
        "全国",
        "立即查找",
        "-",
        "/logistics/freight",
        "快捷入口",
        STATUS_ONLINE,
        false,
        4);

    seedEntity(
        LogisticsSectionType.WAREHOUSE,
        "WH2026041801",
        "唐山海港仓储中心",
        "室内库 / 20吨行车 / 可夜间作业",
        "唐山",
        "0.9元/吨/天",
        "仓储服务",
        "/logistics/warehouse/1",
        "推荐仓库",
        STATUS_ONLINE,
        true,
        5);
    seedEntity(
        LogisticsSectionType.WAREHOUSE,
        "WH2026041802",
        "无锡城南钢材仓",
        "露天场 / 分拣服务 / 日吞吐800吨",
        "无锡",
        "0.8元/吨/天",
        "仓储服务",
        "/logistics/warehouse/2",
        "推荐仓库",
        STATUS_ONLINE,
        false,
        6);
    seedEntity(
        LogisticsSectionType.WAREHOUSE,
        "WH2026041803",
        "佛山顺德物流仓",
        "室内库 / 临港短驳 / 24小时值守",
        "佛山",
        "1.1元/吨/天",
        "仓储服务",
        "/logistics/warehouse/3",
        "推荐仓库",
        STATUS_ONLINE,
        false,
        7);

    seedEntity(
        LogisticsSectionType.FREIGHT,
        "FR2026041801",
        "唐山 → 无锡",
        "13米平板",
        "唐山",
        "120元/吨起",
        "48小时",
        "/logistics/freight/1",
        "推荐专线",
        STATUS_ONLINE,
        true,
        8);
    seedEntity(
        LogisticsSectionType.FREIGHT,
        "FR2026041802",
        "天津 → 佛山",
        "17.5米平板",
        "天津",
        "165元/吨起",
        "72小时",
        "/logistics/freight/2",
        "推荐专线",
        STATUS_ONLINE,
        false,
        9);
    seedEntity(
        LogisticsSectionType.FREIGHT,
        "FR2026041803",
        "武汉 → 成都",
        "13米高栏",
        "武汉",
        "108元/吨起",
        "36小时",
        "/logistics/freight/3",
        "推荐专线",
        STATUS_ONLINE,
        false,
        10);

    seedEntity(
        LogisticsSectionType.STORAGE_DEMAND,
        "SD2026041801",
        "郑州钢贸商求300吨室内库，需2天内入库",
        "仓储需求",
        "郑州",
        "300吨",
        "2天内入库",
        "/logistics/demand/SD2026041801",
        "最新仓储需求",
        STATUS_ONLINE,
        true,
        11);
    seedEntity(
        LogisticsSectionType.STORAGE_DEMAND,
        "SD2026041802",
        "佛山终端企业求500吨短期仓储，可夜间作业",
        "仓储需求",
        "佛山",
        "500吨",
        "可夜间作业",
        "/logistics/demand/SD2026041802",
        "最新仓储需求",
        STATUS_ONLINE,
        false,
        12);
    seedEntity(
        LogisticsSectionType.STORAGE_DEMAND,
        "SD2026041803",
        "南京项目部求200吨中板仓储，需分批出库",
        "仓储需求",
        "南京",
        "200吨",
        "分批出库",
        "/logistics/demand/SD2026041803",
        "最新仓储需求",
        STATUS_ONLINE,
        false,
        13);

    seedEntity(
        LogisticsSectionType.TRANSPORT_DEMAND,
        "TD2026041801",
        "唐山到无锡螺纹钢 260吨，13米平板",
        "运输需求",
        "唐山",
        "260吨",
        "13米平板",
        "/logistics/demand/TD2026041801",
        "最新运输需求",
        STATUS_ONLINE,
        true,
        14);
    seedEntity(
        LogisticsSectionType.TRANSPORT_DEMAND,
        "TD2026041802",
        "天津到广州热卷 180吨，48小时到货",
        "运输需求",
        "天津",
        "180吨",
        "48小时到货",
        "/logistics/demand/TD2026041802",
        "最新运输需求",
        STATUS_ONLINE,
        false,
        15);
    seedEntity(
        LogisticsSectionType.TRANSPORT_DEMAND,
        "TD2026041803",
        "武汉到长沙型钢 120吨，次日发车",
        "运输需求",
        "武汉",
        "120吨",
        "次日发车",
        "/logistics/demand/TD2026041803",
        "最新运输需求",
        STATUS_ONLINE,
        false,
        16);

    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041801",
        "唐山分站",
        "热门城市入口",
        "唐山",
        "分站",
        "本地仓储物流供需",
        "/site/tangshan",
        "城市入口",
        STATUS_ONLINE,
        true,
        17);
    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041802",
        "邯郸分站",
        "热门城市入口",
        "邯郸",
        "分站",
        "本地仓储物流供需",
        "/site/handan",
        "城市入口",
        STATUS_ONLINE,
        false,
        18);
    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041803",
        "天津分站",
        "热门城市入口",
        "天津",
        "分站",
        "本地仓储物流供需",
        "/site/tianjin",
        "城市入口",
        STATUS_ONLINE,
        false,
        19);
    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041804",
        "无锡分站",
        "热门城市入口",
        "无锡",
        "分站",
        "本地仓储物流供需",
        "/site/wuxi",
        "城市入口",
        STATUS_ONLINE,
        false,
        20);
    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041805",
        "佛山分站",
        "热门城市入口",
        "佛山",
        "分站",
        "本地仓储物流供需",
        "/site/foshan",
        "城市入口",
        STATUS_ONLINE,
        false,
        21);
    seedEntity(
        LogisticsSectionType.CITY_STATION,
        "CT2026041806",
        "武汉分站",
        "热门城市入口",
        "武汉",
        "分站",
        "本地仓储物流供需",
        "/site/wuhan",
        "城市入口",
        STATUS_ONLINE,
        false,
        22);

    seedEntity(
        LogisticsSectionType.AD_SLOT,
        "AD2026041801",
        "仓储物流招商专区",
        "支持城市、路线、车型与品类精准投放",
        "全国",
        "咨询投放",
        "分站仓储物流推荐位",
        "/site/ad?placement=%E5%88%86%E7%AB%99%E4%BB%93%E5%82%A8%E7%89%A9%E6%B5%81%E6%8E%A8%E8%8D%90%E4%BD%8D",
        "广告位",
        STATUS_ONLINE,
        true,
        23);
  }

  private void seedEntity(
      LogisticsSectionType type,
      String id,
      String title,
      String subtitle,
      String city,
      String value,
      String extra,
      String link,
      String content,
      String status,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    LogisticsSectionEntity entity =
        new LogisticsSectionEntity(
            id,
            type,
            title,
            subtitle,
            city,
            value,
            extra,
            link,
            content,
            status,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.get(type).put(id, entity);
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
