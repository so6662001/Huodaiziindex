package com.huodaizi.backend.repository.siteaddetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminCreateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailLeadSubmitRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailSectionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySiteAdDetailRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, SiteAdDetailEntity> store = new ConcurrentHashMap<>();

  public InMemorySiteAdDetailRepository() {
    seed();
  }

  public List<SiteAdDetailEntity> onlineList(String placementId, SiteAdDetailSectionType section) {
    String target = normalizePlacementId(placementId);
    return store.values().stream()
        .filter(item -> item.getPlacementId().equals(target))
        .filter(item -> item.getSectionType() == section)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(SiteAdDetailEntity::isPinned).reversed()
                .thenComparing(SiteAdDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SiteAdDetailEntity onlineSingle(String placementId, SiteAdDetailSectionType section) {
    return onlineList(placementId, section).stream().findFirst().orElse(null);
  }

  public List<SiteAdDetailEntity> onlinePlacements() {
    return store.values().stream()
        .filter(item -> item.getSectionType() == SiteAdDetailSectionType.PLACEMENT)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(SiteAdDetailEntity::isPinned).reversed()
                .thenComparing(SiteAdDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public String submitLead(String placementId, SiteAdDetailLeadSubmitRequest request) {
    String normalizedPlacementId = normalizePlacementId(placementId);
    ensurePlacementExists(normalizedPlacementId);

    String id = "SADL" + seq.incrementAndGet();
    SiteAdDetailEntity entity =
        new SiteAdDetailEntity(
            id,
            normalizedPlacementId,
            SiteAdDetailSectionType.LEAD,
            request.companyName(),
            request.contactName(),
            request.phone(),
            defaultText(request.city(), "全国"),
            defaultText(request.duration(), "30天"),
            defaultText(request.budget(), "-"),
            defaultText(request.remark(), "-"),
            "#",
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return "LEAD" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + "-" + id;
  }

  public List<SiteAdDetailEntity> adminList(
      String placementId, SiteAdDetailSectionType section) {
    String target = normalizePlacementId(placementId);
    return store.values().stream()
        .filter(item -> item.getPlacementId().equals(target))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(SiteAdDetailEntity::isPinned).reversed()
                .thenComparing(SiteAdDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SiteAdDetailEntity adminCreate(
      String placementId, SiteAdDetailSectionType section, SiteAdDetailAdminCreateRequest request) {
    String target = normalizePlacementId(placementId);
    String id = "SADD" + seq.incrementAndGet();

    String title = defaultText(request.title(), "-");
    String subtitle = defaultText(request.subtitle(), "-");
    String value = defaultText(request.value(), "-");
    String extra = defaultText(request.extra(), "-");
    String link = defaultText(request.link(), "#");

    if (section == SiteAdDetailSectionType.BENEFIT) {
      value = defaultText(request.content(), "-");
      extra = defaultText(request.extra(), "-");
    } else if (section == SiteAdDetailSectionType.FAQ) {
      subtitle = defaultText(request.content(), "-");
      value = "-";
      extra = "-";
    } else if (section == SiteAdDetailSectionType.LEAD) {
      title = defaultText(request.companyName(), "-");
      subtitle = defaultText(request.contactName(), "-");
      value = defaultText(request.phone(), "-");
      extra =
          defaultText(request.city(), "全国")
              + "|"
              + defaultText(request.duration(), "30天")
              + "|"
              + defaultText(request.budget(), "-")
              + "|"
              + defaultText(request.remark(), "-");
    }

    SiteAdDetailEntity entity =
        new SiteAdDetailEntity(
            id,
            target,
            section,
            title,
            subtitle,
            value,
            extra,
            link,
            request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status()),
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public SiteAdDetailEntity adminUpdate(
      String placementId, SiteAdDetailSectionType section, String id, SiteAdDetailAdminUpdateRequest request) {
    String target = normalizePlacementId(placementId);
    SiteAdDetailEntity entity = requireByIdAndPlacementIdAndSection(id, target, section);

    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());

    String title = request.title();
    String subtitle = request.subtitle();
    String value = request.value();
    String extra = request.extra();
    String link = request.link();

    if (section == SiteAdDetailSectionType.BENEFIT && request.content() != null) {
      value = request.content();
    }
    if (section == SiteAdDetailSectionType.FAQ && request.content() != null) {
      subtitle = request.content();
    }

    entity.update(title, subtitle, value, extra, link, normalizedStatus, request.pinned());
    return entity;
  }

  public SiteAdDetailEntity adminChangeStatus(
      String placementId, SiteAdDetailSectionType section, String id, String status) {
    String target = normalizePlacementId(placementId);
    SiteAdDetailEntity entity = requireByIdAndPlacementIdAndSection(id, target, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public SiteAdDetailEntity adminPin(
      String placementId, SiteAdDetailSectionType section, String id, boolean pinned) {
    String target = normalizePlacementId(placementId);
    SiteAdDetailEntity entity = requireByIdAndPlacementIdAndSection(id, target, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String placementId, SiteAdDetailSectionType section, String id) {
    String target = normalizePlacementId(placementId);
    SiteAdDetailEntity entity = requireByIdAndPlacementIdAndSection(id, target, section);
    store.remove(entity.getId());
  }

  private void ensurePlacementExists(String placementId) {
    boolean exists =
        store.values().stream()
            .anyMatch(
                item ->
                    item.getPlacementId().equals(placementId)
                        && item.getSectionType() == SiteAdDetailSectionType.PLACEMENT
                        && STATUS_ONLINE.equals(item.getStatus()));
    if (!exists) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "广告位不存在或已下线");
    }
  }

  private SiteAdDetailEntity requireByIdAndPlacementIdAndSection(
      String id, String placementId, SiteAdDetailSectionType section) {
    SiteAdDetailEntity entity = store.get(id);
    if (entity == null
        || !entity.getPlacementId().equals(placementId)
        || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "广告位详情记录不存在");
    }
    return entity;
  }

  private String normalizePlacementId(String placementId) {
    if (placementId == null || placementId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "placementId 不能为空");
    }
    return placementId.trim().toUpperCase(Locale.ROOT);
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
    seedPlacement(
        "SA001",
        "分站首页焦点位",
        "首页首屏黄金位置，覆盖高频访问入口",
        "3000元/月起",
        "首页首屏曝光、品牌心智建立、招商活动集中展示",
        "城市定向|品类定向|曝光报表",
        "日均UV 1.8w+",
        true,
        10);
    seedPlacement(
        "SA002",
        "分站信息流广告位",
        "嵌入供求与资讯信息流，自然触达",
        "120元/天起",
        "适合活动引流、询盘转化、持续曝光",
        "按城市投放|按品类投放",
        "CTR 3.6%",
        false,
        9);
    seedPlacement(
        "SA003",
        "分站仓储物流推荐位",
        "物流频道核心推荐区，面向仓配客户",
        "1800元/月起",
        "按线路与车型精准定向，促进有效留资",
        "线路定向|车型定向|线索复盘",
        "留资成本下降22%",
        false,
        8);

    seedBenefit("SA001", "城市定向", "支持按城市分站投放，精准覆盖目标市场", 7);
    seedBenefit("SA001", "品类定向", "支持按螺纹钢/热卷/型钢等品类定向展示", 6);
    seedBenefit("SA001", "数据复盘", "提供曝光、点击、留资报表，便于运营优化", 5);

    seedFaq("SA001", "最低投放周期多久？", "支持7天起投，建议30天以上以获得稳定转化", 4);
    seedFaq("SA001", "是否支持素材代设计？", "支持基础素材规范指导，可对接设计服务", 3);
    seedFaq("SA001", "如何查看投放效果？", "中台可查看曝光、点击、留资等核心数据", 2);
  }

  private void seedPlacement(
      String placementId,
      String title,
      String subtitle,
      String price,
      String desc,
      String supportTags,
      String exposure,
      boolean pinned,
      long minusHours) {
    SiteAdDetailEntity entity =
        new SiteAdDetailEntity(
            "SADD" + seq.incrementAndGet(),
            placementId,
            SiteAdDetailSectionType.PLACEMENT,
            title,
            subtitle,
            price,
            supportTags + "|" + exposure + "|" + desc,
            "/site/ad/" + placementId,
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusHours(minusHours));
    store.put(entity.getId(), entity);
  }

  private void seedBenefit(String placementId, String title, String content, long minusHours) {
    SiteAdDetailEntity entity =
        new SiteAdDetailEntity(
            "SADD" + seq.incrementAndGet(),
            placementId,
            SiteAdDetailSectionType.BENEFIT,
            title,
            content,
            "-",
            "-",
            "#",
            STATUS_ONLINE,
            false,
            LocalDateTime.now().minusHours(minusHours));
    store.put(entity.getId(), entity);
  }

  private void seedFaq(String placementId, String question, String answer, long minusHours) {
    SiteAdDetailEntity entity =
        new SiteAdDetailEntity(
            "SADD" + seq.incrementAndGet(),
            placementId,
            SiteAdDetailSectionType.FAQ,
            question,
            answer,
            "-",
            "-",
            "#",
            STATUS_ONLINE,
            false,
            LocalDateTime.now().minusHours(minusHours));
    store.put(entity.getId(), entity);
  }
}
