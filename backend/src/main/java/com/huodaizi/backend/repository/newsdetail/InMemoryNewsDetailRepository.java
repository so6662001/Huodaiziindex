package com.huodaizi.backend.repository.newsdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminCreateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryNewsDetailRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, NewsDetailEntity> store = new ConcurrentHashMap<>();

  public InMemoryNewsDetailRepository() {
    seed();
  }

  public List<NewsDetailEntity> listOnlineByNewsIdAndSection(String newsId, NewsDetailSectionType section) {
    String targetNewsId = normalize(newsId);
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> normalize(item.getNewsId()).equals(targetNewsId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(NewsDetailEntity::isPinned).reversed()
                .thenComparing(NewsDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public NewsDetailEntity onlineByNewsIdAndSection(String newsId, NewsDetailSectionType section) {
    return listOnlineByNewsIdAndSection(newsId, section).stream().findFirst().orElse(null);
  }

  public List<NewsDetailEntity> adminListByNewsIdAndSection(String newsId, NewsDetailSectionType section) {
    String targetNewsId = normalize(newsId);
    return store.values().stream()
        .filter(item -> normalize(item.getNewsId()).equals(targetNewsId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(NewsDetailEntity::isPinned).reversed()
                .thenComparing(NewsDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public NewsDetailEntity adminCreate(
      String newsId, NewsDetailSectionType section, NewsDetailAdminCreateRequest request) {
    String id = "ND" + seq.incrementAndGet();
    NewsDetailEntity entity =
        new NewsDetailEntity(
            id,
            newsId,
            section,
            request.title(),
            defaultText(request.category(), "-"),
            defaultText(request.city(), "-"),
            defaultText(request.publishAt(), "-"),
            defaultText(request.source(), "-"),
            defaultText(request.summary(), "-"),
            defaultText(request.content(), "-"),
            defaultText(request.tags(), "-"),
            defaultText(request.relatedNewsId(), "-"),
            defaultText(request.link(), "#"),
            request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status()),
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public NewsDetailEntity adminUpdate(
      String newsId, NewsDetailSectionType section, String id, NewsDetailAdminUpdateRequest request) {
    NewsDetailEntity entity = requireByIdNewsAndSection(id, newsId, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.category(),
        request.city(),
        request.publishAt(),
        request.source(),
        request.summary(),
        request.content(),
        request.tags(),
        request.relatedNewsId(),
        request.link(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public NewsDetailEntity adminChangeStatus(
      String newsId, NewsDetailSectionType section, String id, String status) {
    NewsDetailEntity entity = requireByIdNewsAndSection(id, newsId, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public NewsDetailEntity adminPin(
      String newsId, NewsDetailSectionType section, String id, boolean pinned) {
    NewsDetailEntity entity = requireByIdNewsAndSection(id, newsId, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String newsId, NewsDetailSectionType section, String id) {
    NewsDetailEntity entity = requireByIdNewsAndSection(id, newsId, section);
    store.remove(entity.getId());
  }

  private NewsDetailEntity requireByIdNewsAndSection(String id, String newsId, NewsDetailSectionType section) {
    NewsDetailEntity entity = store.get(id);
    if (entity == null
        || entity.getSectionType() != section
        || !normalize(entity.getNewsId()).equals(normalize(newsId))) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "资讯详情记录不存在");
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
    seedArticle(
        "N20260418001",
        "多地基建项目推进，建材成交回暖",
        "市场",
        "唐山",
        "2026-04-18 09:30",
        "货袋子研究院",
        "重点城市建材成交量环比提升，终端补库情绪恢复，交易节奏逐步加快。",
        "螺纹钢,成交,基建",
        true,
        8);

    seedContent("N20260418001", "01", "本周华北、华东多个重点城市建材成交较上周明显回升，主要受基建项目开工节奏加快和终端阶段性补库推动。市场询单活跃度提升，低价资源成交占比下降。", true, 5);
    seedContent("N20260418001", "02", "从区域表现看，唐山、天津的螺纹钢与盘螺资源流转速度较快，贸易商出货节奏趋于均衡。华东部分城市因天气改善，工地需求恢复，成交重心小幅上移。", false, 4);
    seedContent("N20260418001", "03", "短期来看，市场仍将围绕库存变化与需求兑现展开博弈。若后续项目资金到位持续改善，现货市场有望保持偏稳偏强运行。", false, 3);

    seedRelated(
        "N20260418001",
        "N20260418002",
        "钢铁行业阶段性去库存，社会库存连续下降",
        "库存",
        "2026-04-18 10:10",
        false,
        3);
    seedRelated(
        "N20260418001",
        "N20260418003",
        "运输成本趋稳，北材南下线路运价小幅回调",
        "产业链",
        "2026-04-18 10:45",
        false,
        2);

    seedTip("N20260418001", "01", "1. 关注品类与城市维度差异", true, 2);
    seedTip("N20260418001", "02", "2. 结合库存与成交数据交叉验证", false, 2);
    seedTip("N20260418001", "03", "3. 配合行情中心进行价格判断", false, 1);

    seedAd(
        "N20260418001",
        "品牌资讯合作",
        "支持资讯内容与城市分站联动投放，提升品牌曝光与线索获取。",
        "/cooperation/news",
        true,
        1);
  }

  private void seedArticle(
      String newsId,
      String title,
      String category,
      String city,
      String publishAt,
      String source,
      String summary,
      String tags,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "ND" + newsId.substring(1) + "01",
        newsId,
        NewsDetailSectionType.ARTICLE,
        title,
        category,
        city,
        publishAt,
        source,
        summary,
        "-",
        tags,
        "-",
        "/news/" + newsId,
        pinned,
        minusMinutes);
  }

  private void seedContent(String newsId, String order, String paragraph, boolean pinned, long minusMinutes) {
    addSeed(
        "ND" + newsId.substring(1) + "1" + order,
        newsId,
        NewsDetailSectionType.CONTENT,
        "正文段落",
        "-",
        "-",
        order,
        "-",
        "-",
        paragraph,
        "-",
        "-",
        "#",
        pinned,
        minusMinutes);
  }

  private void seedRelated(
      String newsId,
      String relatedNewsId,
      String title,
      String category,
      String publishAt,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "ND" + newsId.substring(1) + "2" + relatedNewsId.substring(relatedNewsId.length() - 2),
        newsId,
        NewsDetailSectionType.RELATED,
        title,
        category,
        "-",
        publishAt,
        "-",
        "-",
        "-",
        "-",
        relatedNewsId,
        "/news/" + relatedNewsId,
        pinned,
        minusMinutes);
  }

  private void seedTip(
      String newsId, String order, String tip, boolean pinned, long minusMinutes) {
    addSeed(
        "ND" + newsId.substring(1) + "3" + order,
        newsId,
        NewsDetailSectionType.TIP,
        tip,
        "-",
        "-",
        order,
        "-",
        "-",
        tip,
        "-",
        "-",
        "#",
        pinned,
        minusMinutes);
  }

  private void seedAd(String newsId, String title, String content, String link, boolean pinned, long minusMinutes) {
    addSeed(
        "ND" + newsId.substring(1) + "401",
        newsId,
        NewsDetailSectionType.AD,
        title,
        "广告",
        "-",
        "-",
        "-",
        content,
        content,
        "-",
        "-",
        link,
        pinned,
        minusMinutes);
  }

  private void addSeed(
      String id,
      String newsId,
      NewsDetailSectionType sectionType,
      String title,
      String category,
      String city,
      String publishAt,
      String source,
      String summary,
      String content,
      String tags,
      String relatedNewsId,
      String link,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    NewsDetailEntity entity =
        new NewsDetailEntity(
            id,
            newsId,
            sectionType,
            title,
            category,
            city,
            publishAt,
            source,
            summary,
            content,
            tags,
            relatedNewsId,
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
