package com.huodaizi.backend.repository.warehousedetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminCreateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryWarehouseDetailRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, WarehouseDetailEntity> store = new ConcurrentHashMap<>();

  public InMemoryWarehouseDetailRepository() {
    seed();
  }

  public List<WarehouseDetailEntity> listOnlineByWarehouseIdAndSection(
      String warehouseId, WarehouseDetailSectionType section) {
    String targetId = normalize(warehouseId);
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> normalize(item.getWarehouseId()).equals(targetId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(WarehouseDetailEntity::isPinned).reversed()
                .thenComparing(WarehouseDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public WarehouseDetailEntity onlineByWarehouseIdAndSection(
      String warehouseId, WarehouseDetailSectionType section) {
    return listOnlineByWarehouseIdAndSection(warehouseId, section).stream().findFirst().orElse(null);
  }

  public List<WarehouseDetailEntity> adminListByWarehouseIdAndSection(
      String warehouseId, WarehouseDetailSectionType section) {
    String targetId = normalize(warehouseId);
    return store.values().stream()
        .filter(item -> normalize(item.getWarehouseId()).equals(targetId))
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(WarehouseDetailEntity::isPinned).reversed()
                .thenComparing(WarehouseDetailEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public WarehouseDetailEntity adminCreate(
      String warehouseId, WarehouseDetailSectionType section, WarehouseDetailAdminCreateRequest request) {
    String id = "WD" + seq.incrementAndGet();
    String status = request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status());
    WarehouseDetailEntity entity;
    switch (section) {
      case MAIN ->
          entity =
              new WarehouseDetailEntity(
                  id,
                  warehouseId,
                  section,
                  request.title(),
                  defaultText(request.city(), "-"),
                  defaultText(request.warehouseType(), "-"),
                  defaultText(request.capacity(), "-"),
                  defaultText(request.throughput(), "-"),
                  defaultText(request.capability(), "-"),
                  defaultText(request.quote(), "-"),
                  defaultText(request.address(), "-"),
                  defaultText(request.workTime(), "-"),
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
      case RELATED_WAREHOUSE ->
          entity =
              new WarehouseDetailEntity(
                  id,
                  warehouseId,
                  section,
                  request.title(),
                  defaultText(request.city(), "-"),
                  defaultText(request.warehouseType(), "-"),
                  "-",
                  "-",
                  "-",
                  defaultText(request.quote(), "-"),
                  "-",
                  "-",
                  "-",
                  "-",
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
              new WarehouseDetailEntity(
                  id,
                  warehouseId,
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
                  "-",
                  "-",
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
              new WarehouseDetailEntity(
                  id,
                  warehouseId,
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
                  "-",
                  defaultText(request.description(), "-"),
                  "-",
                  defaultText(request.contactName(), "-"),
                  defaultText(request.contactPhone(), "-"),
                  defaultText(request.serviceStatus(), "-"),
                  defaultText(request.link(), "#"),
                  status,
                  request.pinned() != null && request.pinned(),
                  LocalDateTime.now());
      case AD ->
          entity =
              new WarehouseDetailEntity(
                  id,
                  warehouseId,
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

  public WarehouseDetailEntity adminUpdate(
      String warehouseId, WarehouseDetailSectionType section, String id, WarehouseDetailAdminUpdateRequest request) {
    WarehouseDetailEntity entity = requireByIdWarehouseAndSection(id, warehouseId, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.city(),
        request.warehouseType(),
        request.capacity(),
        request.throughput(),
        request.capability(),
        request.quote(),
        request.address(),
        request.workTime(),
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

  public WarehouseDetailEntity adminChangeStatus(
      String warehouseId, WarehouseDetailSectionType section, String id, String status) {
    WarehouseDetailEntity entity = requireByIdWarehouseAndSection(id, warehouseId, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public WarehouseDetailEntity adminPin(
      String warehouseId, WarehouseDetailSectionType section, String id, boolean pinned) {
    WarehouseDetailEntity entity = requireByIdWarehouseAndSection(id, warehouseId, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String warehouseId, WarehouseDetailSectionType section, String id) {
    WarehouseDetailEntity entity = requireByIdWarehouseAndSection(id, warehouseId, section);
    store.remove(entity.getId());
  }

  private WarehouseDetailEntity requireByIdWarehouseAndSection(
      String id, String warehouseId, WarehouseDetailSectionType section) {
    WarehouseDetailEntity entity = store.get(id);
    if (entity == null
        || entity.getSectionType() != section
        || !normalize(entity.getWarehouseId()).equals(normalize(warehouseId))) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓库详情记录不存在");
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
        "W20260418001",
        "唐山海港仓储中心",
        "唐山",
        "室内库",
        "50,000吨",
        "日吞吐1,200吨",
        "20吨行车 / 夜间作业",
        "0.9元/吨/天",
        "唐山市海港开发区港前路 88 号",
        "7x24 小时作业",
        "螺纹钢,型钢,热卷,可分批出库,短驳协同",
        "靠近主干道与港区，适合钢材集散、中转与短期周转存储。",
        true,
        8);

    seedRelatedWarehouse(
        "W20260418001",
        "W20260418002",
        "无锡城南钢材仓",
        "无锡",
        "露天场",
        "0.8元/吨/天",
        "/logistics/warehouse/W20260418002",
        false,
        4);
    seedRelatedWarehouse(
        "W20260418001",
        "W20260418003",
        "佛山顺德物流仓",
        "佛山",
        "综合库",
        "1.1元/吨/天",
        "/logistics/warehouse/W20260418003",
        false,
        3);
    seedRelatedWarehouse(
        "W20260418001",
        "W20260418004",
        "武汉临港钢材库",
        "武汉",
        "室内库",
        "1.0元/吨/天",
        "/logistics/warehouse/W20260418004",
        false,
        2);

    seedRelatedDemand(
        "W20260418001",
        "D20260418001",
        "唐山螺纹钢短期仓储需求，需2天内入库",
        "/logistics/demand/D20260418001",
        false,
        3);
    seedRelatedDemand(
        "W20260418001",
        "D20260418002",
        "佛山终端企业短期仓储需求，需夜间作业",
        "/logistics/demand/D20260418002",
        false,
        2);
    seedRelatedDemand(
        "W20260418001",
        "D20260418003",
        "武汉项目部求中板仓储，需分批出库",
        "/logistics/demand/D20260418003",
        false,
        1);

    seedContact("W20260418001", "张**", "139****4832", "可接新单", true, 2);
    seedAd(
        "W20260418001",
        "仓储服务推广",
        "支持按城市、品类和库型精准投放，提升仓储线索对接效率。",
        "/cooperation/warehouse",
        true,
        1);
  }

  private void seedMain(
      String warehouseId,
      String name,
      String city,
      String warehouseType,
      String capacity,
      String throughput,
      String capability,
      String quote,
      String address,
      String workTime,
      String serviceTags,
      String desc,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "WD" + warehouseId.substring(1) + "01",
        warehouseId,
        WarehouseDetailSectionType.MAIN,
        name,
        city,
        warehouseType,
        capacity,
        throughput,
        capability,
        quote,
        address,
        workTime,
        serviceTags,
        desc,
        "-",
        "-",
        "-",
        "-",
        "/logistics/warehouse/" + warehouseId,
        pinned,
        minusMinutes);
  }

  private void seedRelatedWarehouse(
      String warehouseId,
      String relatedId,
      String title,
      String city,
      String warehouseType,
      String quote,
      String link,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "WD" + warehouseId.substring(1) + "2" + relatedId.substring(relatedId.length() - 2),
        warehouseId,
        WarehouseDetailSectionType.RELATED_WAREHOUSE,
        title,
        city,
        warehouseType,
        "-",
        "-",
        "-",
        quote,
        "-",
        "-",
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
      String warehouseId,
      String relatedId,
      String title,
      String link,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "WD" + warehouseId.substring(1) + "3" + relatedId.substring(relatedId.length() - 2),
        warehouseId,
        WarehouseDetailSectionType.RELATED_DEMAND,
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
        "-",
        relatedId,
        "-",
        "-",
        "-",
        link,
        pinned,
        minusMinutes);
  }

  private void seedContact(
      String warehouseId,
      String contactName,
      String contactPhone,
      String serviceStatus,
      boolean pinned,
      long minusMinutes) {
    addSeed(
        "WD" + warehouseId.substring(1) + "401",
        warehouseId,
        WarehouseDetailSectionType.CONTACT,
        "联系方式",
        "-",
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
      String warehouseId, String title, String content, String link, boolean pinned, long minusMinutes) {
    addSeed(
        "WD" + warehouseId.substring(1) + "501",
        warehouseId,
        WarehouseDetailSectionType.AD,
        title,
        "广告",
        "-",
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
      String warehouseId,
      WarehouseDetailSectionType sectionType,
      String title,
      String city,
      String warehouseType,
      String capacity,
      String throughput,
      String capability,
      String quote,
      String address,
      String workTime,
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
    WarehouseDetailEntity entity =
        new WarehouseDetailEntity(
            id,
            warehouseId,
            sectionType,
            title,
            city,
            warehouseType,
            capacity,
            throughput,
            capability,
            quote,
            address,
            workTime,
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
