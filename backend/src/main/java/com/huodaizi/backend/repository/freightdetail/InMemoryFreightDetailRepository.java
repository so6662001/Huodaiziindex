package com.huodaizi.backend.repository.freightdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminCreateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdetail.FreightDetailSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFreightDetailRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, FreightDetailEntity> store = new ConcurrentHashMap<>();

  public InMemoryFreightDetailRepository() {
    seed();
  }

  public List<FreightDetailEntity> listOnlineByFreightIdAndSection(
      String freightId, FreightDetailSectionType section) {
    String targetFreightId = normalize(freightId);
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> normalize(item.getFreightId()).equals(targetFreightId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(FreightDetailEntity::isPinned).reversed()
                .thenComparing(FreightDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public FreightDetailEntity onlineByFreightIdAndSection(
      String freightId, FreightDetailSectionType section) {
    return listOnlineByFreightIdAndSection(freightId, section).stream().findFirst().orElse(null);
  }

  public List<FreightDetailEntity> adminListByFreightIdAndSection(
      String freightId, FreightDetailSectionType section) {
    String targetFreightId = normalize(freightId);
    return store.values().stream()
        .filter(item -> normalize(item.getFreightId()).equals(targetFreightId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(FreightDetailEntity::isPinned).reversed()
                .thenComparing(FreightDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public FreightDetailEntity adminCreate(
      String freightId, FreightDetailSectionType section, FreightDetailAdminCreateRequest request) {
    String id = "FD" + seq.incrementAndGet();
    String status = request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status());
    FreightDetailEntity entity;
    switch (section) {
      case MAIN ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  defaultText(request.provider(), "-"),
                  defaultText(request.route(), "-"),
                  defaultText(request.vehicle(), "-"),
                  defaultText(request.loadRange(), "-"),
                  defaultText(request.frequency(), "-"),
                  defaultText(request.timeliness(), "-"),
                  defaultText(request.quote(), "-"),
                  defaultText(request.serviceTags(), "-"),
                  defaultText(request.description(), "-"),
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case SERVICE_TAG ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.serviceTags(), request.content()),
                  defaultText(request.description(), "-"),
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case RELATED_LINE ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  defaultText(request.provider(), "-"),
                  defaultText(request.route(), "-"),
                  "-",
                  "-",
                  "-",
                  defaultText(request.timeliness(), "-"),
                  defaultText(request.quote(), "-"),
                  "-",
                  defaultText(request.description(), "-"),
                  defaultText(request.relatedId(), "-"),
                  "-",
                  "-",
                  "-",
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case RELATED_DEMAND ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.description(), "-"),
                  defaultText(request.relatedId(), "-"),
                  "-",
                  "-",
                  "-",
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case CONTACT ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.description(), "-"),
                  "-",
                  defaultText(request.contactName(), request.provider()),
                  defaultText(request.contactPhone(), request.content()),
                  defaultText(request.serviceStatus(), "-"),
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case AD ->
          entity =
              new FreightDetailEntity(
                  id,
                  freightId,
                  section,
                  request.title(),
                  "广告",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.description(), "-"),
                  "-",
                  "-",
                  "-",
                  "-",
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "不支持的 section");
    }
    store.put(id, entity);
    return entity;
  }

  public FreightDetailEntity adminUpdate(
      String freightId, FreightDetailSectionType section, String id, FreightDetailAdminUpdateRequest request) {
    FreightDetailEntity entity = requireByIdFreightAndSection(id, freightId, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.provider(),
        request.route(),
        request.vehicle(),
        request.loadRange(),
        request.frequency(),
        request.timeliness(),
        request.quote(),
        request.serviceTags(),
        request.description(),
        request.relatedId(),
        request.contactName(),
        request.contactPhone(),
        request.serviceStatus(),
        request.link(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public FreightDetailEntity adminChangeStatus(
      String freightId, FreightDetailSectionType section, String id, String status) {
    FreightDetailEntity entity = requireByIdFreightAndSection(id, freightId, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public FreightDetailEntity adminPin(
      String freightId, FreightDetailSectionType section, String id, boolean pinned) {
    FreightDetailEntity entity = requireByIdFreightAndSection(id, freightId, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String freightId, FreightDetailSectionType section, String id) {
    FreightDetailEntity entity = requireByIdFreightAndSection(id, freightId, section);
    store.remove(entity.getId());
  }

  private FreightDetailEntity requireByIdFreightAndSection(
      String id, String freightId, FreightDetailSectionType section) {
    FreightDetailEntity entity = store.get(id);
    if (entity == null
        || entity.getSectionType() != section
        || !normalize(entity.getFreightId()).equals(normalize(freightId))) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "车线详情记录不存在");
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
    seedMain(
        "F20260418001",
        "唐山宏运车队",
        "唐山 → 无锡",
        "13米平板",
        "30-35吨",
        "每日发车",
        "48小时",
        "120元/吨起",
        "覆盖唐山至无锡主流钢材流向，支持整车与拼车，适合中短周期补库运输。",
        true,
        8);

    seedServiceTag("F20260418001", "01", "可回单", true, 7);
    seedServiceTag("F20260418001", "02", "支持夜装", false, 6);
    seedServiceTag("F20260418001", "03", "北材南下稳定班次", false, 5);

    seedRelatedLine(
        "F20260418001",
        "F20260418002",
        "天津北方专线 · 天津 → 佛山",
        "72小时 · 165元/吨起",
        "/logistics/freight/F20260418002",
        false,
        4);
    seedRelatedLine(
        "F20260418001",
        "F20260418003",
        "武汉联速物流 · 武汉 → 长沙",
        "24小时 · 98元/吨起",
        "/logistics/freight/F20260418003",
        false,
        3);
    seedRelatedLine(
        "F20260418001",
        "F20260418004",
        "佛山华南车队 · 佛山 → 广州",
        "12小时 · 85元/吨起",
        "/logistics/freight/F20260418004",
        false,
        2);

    seedRelatedDemand(
        "F20260418001", "D20260418003", "唐山到无锡螺纹钢运输需求，48小时内到货", "/logistics/demand/D20260418003", false, 3);
    seedRelatedDemand(
        "F20260418001", "D20260418002", "佛山终端企业短期仓储需求，需短驳协同", "/logistics/demand/D20260418002", false, 2);
    seedRelatedDemand(
        "F20260418001", "D20260418001", "唐山螺纹钢短期仓储需求，要求可夜间作业", "/logistics/demand/D20260418001", false, 1);

    seedContact("F20260418001", "王**", "138****7862", "可接新单", true, 2);
    seedAd(
        "F20260418001",
        "车队专线推广",
        "支持按起终点与车型精准投放，提升运输线索转化效率。",
        "/cooperation/freight",
        true,
        1);
  }

  private void seedMain(
      String freightId,
      String provider,
      String route,
      String vehicle,
      String loadRange,
      String frequency,
      String timeliness,
      String quote,
      String desc,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "FD" + freightId.substring(1) + "01",
        freightId,
        FreightDetailSectionType.MAIN,
        provider,
        provider,
        route,
        vehicle,
        loadRange,
        frequency,
        timeliness,
        quote,
        "可回单,支持夜装,北材南下稳定班次",
        desc,
        "-",
        "-",
        "-",
        "-",
        "/logistics/freight/" + freightId,
        pinned,
        minusMinutes);
  }

  private void seedServiceTag(
      String freightId, String order, String tag, boolean pinned, long minusMinutes) {
    addSeed(
        "FD" + freightId.substring(1) + "1" + order,
        freightId,
        FreightDetailSectionType.SERVICE_TAG,
        "服务能力-" + order,
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        tag,
        "-",
        "-",
        "-",
        "-",
        "-",
        "#",
        pinned,
        minusMinutes);
  }

  private void seedRelatedLine(
      String freightId,
      String relatedId,
      String title,
      String subtitle,
      String link,
      boolean pinned,
      long minusMinutes) {
    String[] parsed = parseSubtitle(subtitle);
    addSeed(
        "FD" + freightId.substring(1) + "2" + relatedId.substring(relatedId.length() - 2),
        freightId,
        FreightDetailSectionType.RELATED_LINE,
        title,
        title,
        extractRoute(title),
        "-",
        "-",
        "-",
        parsed[0],
        parsed[1],
        "-",
        "-",
        relatedId,
        "-",
        "-",
        "-",
        link,
        pinned,
        minusMinutes);
  }

  private void seedRelatedDemand(
      String freightId,
      String demandId,
      String title,
      String link,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "FD" + freightId.substring(1) + "3" + demandId.substring(demandId.length() - 2),
        freightId,
        FreightDetailSectionType.RELATED_DEMAND,
        title,
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        demandId,
        "-",
        "-",
        "-",
        link,
        pinned,
        minusMinutes);
  }

  private void seedContact(
      String freightId,
      String contactName,
      String contactPhone,
      String serviceStatus,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "FD" + freightId.substring(1) + "401",
        freightId,
        FreightDetailSectionType.CONTACT,
        "联系方式",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "登录后可查看完整联系方式并发起在线沟通。",
        "-",
        contactName,
        contactPhone,
        serviceStatus,
        "#",
        pinned,
        minusMinutes);
  }

  private void seedAd(
      String freightId, String title, String content, String link, boolean pinned, long minusMinutes) {
    addSeed(
        "FD" + freightId.substring(1) + "501",
        freightId,
        FreightDetailSectionType.AD,
        title,
        "广告",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        "-",
        content,
        "-",
        "-",
        "-",
        "-",
        link,
        pinned,
        minusMinutes);
  }

  private void addSeed(
      String id,
      String freightId,
      FreightDetailSectionType sectionType,
      String title,
      String provider,
      String route,
      String vehicle,
      String loadRange,
      String frequency,
      String timeliness,
      String quote,
      String serviceTags,
      String description,
      String relatedId,
      String contactName,
      String contactPhone,
      String serviceStatus,
      String link,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    FreightDetailEntity entity =
        new FreightDetailEntity(
            id,
            freightId,
            sectionType,
            title,
            provider,
            route,
            vehicle,
            loadRange,
            frequency,
            timeliness,
            quote,
            serviceTags,
            description,
            relatedId,
            contactName,
            contactPhone,
            serviceStatus,
            link,
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.put(id, entity);
  }

  private String[] parseSubtitle(String subtitle) {
    if (subtitle == null || subtitle.isBlank()) {
      return new String[] {"-", "-"};
    }
    String[] parts = subtitle.split("·");
    if (parts.length < 2) {
      return new String[] {subtitle.trim(), "-"};
    }
    return new String[] {parts[0].trim(), parts[1].trim()};
  }

  private String extractRoute(String title) {
    if (title == null || title.isBlank()) {
      return "-";
    }
    int idx = title.indexOf("·");
    if (idx < 0 || idx + 1 >= title.length()) {
      return title.trim();
    }
    return title.substring(idx + 1).trim();
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
