package com.huodaizi.backend.repository.news;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.news.NewsAdminCreateRequest;
import com.huodaizi.backend.dto.news.NewsAdminUpdateRequest;
import com.huodaizi.backend.dto.news.NewsListRequest;
import com.huodaizi.backend.dto.news.NewsSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryNewsRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, NewsEntity> store = new ConcurrentHashMap<>();

  public InMemoryNewsRepository() {
    seed();
  }

  public List<NewsEntity> listOnline(NewsListRequest request, NewsSectionType section) {
    String category = normalize(request.category());
    String city = normalize(request.city());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                category.isBlank()
                    || "全部".equals(category)
                    || normalize(item.getCategory()).contains(category))
        .filter(
            item -> city.isBlank() || "全部城市".equals(city) || normalize(item.getCity()).contains(city))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getSummary()).contains(keyword))
        .sorted(
            Comparator.comparing(NewsEntity::isPinned).reversed()
                .thenComparing(NewsEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<NewsEntity> adminList(NewsSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(NewsEntity::isPinned).reversed()
                .thenComparing(NewsEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public NewsEntity adminCreate(NewsSectionType section, NewsAdminCreateRequest request) {
    String id = "NW" + seq.incrementAndGet();
    NewsEntity entity =
        new NewsEntity(
            id,
            section,
            defaultText(request.category(), "市场"),
            defaultText(request.city(), "全国"),
            request.title(),
            defaultText(request.summary(), "-"),
            defaultText(request.publishAt(), LocalDateTime.now().toString()),
            defaultText(request.link(), "#"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public NewsEntity adminUpdate(String id, NewsSectionType section, NewsAdminUpdateRequest request) {
    NewsEntity entity = requireByIdAndSection(id, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.category(),
        request.city(),
        request.title(),
        request.summary(),
        request.publishAt(),
        request.link(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public NewsEntity adminChangeStatus(String id, NewsSectionType section, String status) {
    NewsEntity entity = requireByIdAndSection(id, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public NewsEntity adminPin(String id, NewsSectionType section, boolean pinned) {
    NewsEntity entity = requireByIdAndSection(id, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id, NewsSectionType section) {
    NewsEntity entity = requireByIdAndSection(id, section);
    store.remove(entity.getId());
  }

  private NewsEntity requireByIdAndSection(String id, NewsSectionType section) {
    NewsEntity entity = store.get(id);
    if (entity == null || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "资讯记录不存在");
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
    addSeed(
        "NW20260418001",
        NewsSectionType.NEWS,
        "市场",
        "唐山",
        "多地基建项目推进，建材成交回暖",
        "本周重点城市建材成交量回升，终端补库意愿增强，市场情绪偏稳。",
        "2026-04-18 09:30",
        "/news/N20260418001",
        true,
        8);
    addSeed(
        "NW20260418002",
        NewsSectionType.NEWS,
        "库存",
        "无锡",
        "钢铁行业阶段性去库存，社会库存连续下降",
        "主要品类社会库存已连续两周下行，部分区域资源流转速度明显提升。",
        "2026-04-18 10:10",
        "/news/N20260418002",
        false,
        10);
    addSeed(
        "NW20260418003",
        NewsSectionType.NEWS,
        "产业链",
        "天津",
        "运输成本趋稳，北材南下线路运价小幅回调",
        "干线运价波动收窄，部分热门线路出现回程车资源恢复，跨区调货效率提高。",
        "2026-04-18 10:45",
        "/news/N20260418003",
        false,
        12);
    addSeed(
        "NW20260418004",
        NewsSectionType.NEWS,
        "政策",
        "上海",
        "行业规范进一步完善，交易合规要求持续强化",
        "相关政策持续明确交易流程与资质要求，平台型企业合规能力成为核心竞争力。",
        "2026-04-18 11:20",
        "/news/N20260418004",
        false,
        14);

    addSeed(
        "NW20260418005",
        NewsSectionType.HOT_READ,
        "热门",
        "全国",
        "螺纹钢价格震荡走强，终端采购节奏加快",
        "热门阅读",
        "2026-04-18 11:25",
        "/news/N20260418005",
        true,
        5);
    addSeed(
        "NW20260418006",
        NewsSectionType.HOT_READ,
        "热门",
        "全国",
        "热卷现货流通改善，华东成交稳中有增",
        "热门阅读",
        "2026-04-18 11:26",
        "/news/N20260418006",
        false,
        6);
    addSeed(
        "NW20260418007",
        NewsSectionType.HOT_READ,
        "热门",
        "全国",
        "仓储物流需求活跃，短驳与专线协同增强",
        "热门阅读",
        "2026-04-18 11:27",
        "/news/N20260418007",
        false,
        7);
    addSeed(
        "NW20260418008",
        NewsSectionType.AD,
        "广告",
        "全国",
        "品牌资讯合作",
        "支持城市与品类定向曝光，帮助企业提升行业影响力与线索转化。",
        "2026-04-18 11:40",
        "/news",
        true,
        4);
  }

  private void addSeed(
      String id,
      NewsSectionType sectionType,
      String category,
      String city,
      String title,
      String summary,
      String publishAt,
      String link,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    NewsEntity entity =
        new NewsEntity(
            id,
            sectionType,
            category,
            city,
            title,
            summary,
            publishAt,
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
